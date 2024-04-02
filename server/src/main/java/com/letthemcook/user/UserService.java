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
    newUser.setToken(userAuthenticationProvider.createToken(newUser.getEmail()));

    // hash the password and store the hashed password as well as the salt inside the database
    User encryptedUser = userAuthenticationProvider.hashNewPassword(newUser);
    userRepository.save(encryptedUser);
    return encryptedUser;
  }

  public User loginUser(User checkUser) {
    User user = new User();
    user.setToken(userAuthenticationProvider.createToken(checkUser.getEmail()));
    return user;
  }

  public User logoutUser(User logoutUser) {
    checkIfUserExists(logoutUser);
    User user = userRepository.findByEmail(logoutUser.getEmail());
    user.setToken(null);
    userRepository.save(user);
    return user;
  }


// ######################################### Util #########################################

  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.findByEmail(userToBeCreated.getEmail());

    String baseErrorMessage = "add user failed because email already exists";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "email", "is"));
    }
  }
}
