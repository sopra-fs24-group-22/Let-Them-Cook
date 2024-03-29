package com.letthemcook.user;

import com.letthemcook.auth.UserAuthenticationProvider;
import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final UserAuthenticationProvider userAuthenticationProvider;

  @Autowired
  private SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, UserAuthenticationProvider userAuthenticationProvider) {
    this.userRepository = userRepository;
    this.userAuthenticationProvider = userAuthenticationProvider;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setId(sequenceGeneratorService.getSequenceNumber(User.SEQUENCE_NAME));
    checkIfUserExists(newUser);

    // hash the password and store the hashed password as well as the salt inside the database
    User encryptedUser = hashUserPassword(newUser);
    userRepository.save(encryptedUser);
    return encryptedUser;
  }

  public User loginUser(User checkUser) {
    User user = new User();
    user.setToken(userAuthenticationProvider.createToken(checkUser.getEmail()));
    return user;
  }

// ######################################### Util #########################################

  public User hashUserPassword(User hashUser) {
    // create a random salt
    SecureRandom random = new SecureRandom();
    byte[] hashSalt = new byte[16];
    random.nextBytes(hashSalt);

    // Encrypt the input password
    String hashPassword = hashUser.getPassword();
    KeySpec spec = new PBEKeySpec(hashPassword.toCharArray(), hashSalt, 65536, 128);
      SecretKeyFactory factory = null;
      try {
          factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException(e);
      }
      byte[] hash = new byte[0];
      try {
          hash = factory.generateSecret(spec).getEncoded();
      } catch (InvalidKeySpecException e) {
          throw new RuntimeException(e);
      }

    // Convert salt and password to string and store them
    String password = Base64.getEncoder().encodeToString(hash);
    String salt = Base64.getEncoder().encodeToString(hashSalt);

    hashUser.setPassword(password);
    hashUser.setSalt(salt);

    return hashUser;
  }

  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.findByEmail(userToBeCreated.getEmail());

    String baseErrorMessage = "add user failed because email already exists";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "email", "is"));
    }
  }
}
