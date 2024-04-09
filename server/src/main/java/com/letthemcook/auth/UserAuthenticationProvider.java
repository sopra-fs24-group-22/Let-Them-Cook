/*
package com.letthemcook.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.letthemcook.rest.mapper.DTOMapper;
import com.letthemcook.user.User;
import com.letthemcook.user.dto.UserDTO;
import com.letthemcook.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
public class UserAuthenticationProvider {
  @Value("${security.jwt.token.secret-key:secret-key}")
  private String secretKey;

  private final UserRepository userRepository;
  private final AuthenticationService authenticationService;
  public UserAuthenticationProvider(UserRepository userRepository, AuthenticationService authenticationService) {
    this.userRepository = userRepository;
    this.authenticationService = authenticationService;
  }

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String email) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + 3_600_000); // 1 hour
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    return JWT.create()
            .withIssuer(email)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .sign(algorithm);
  }

  public Authentication isTokenValid(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    JWTVerifier verifier = JWT.require(algorithm).build();

    DecodedJWT decoded = verifier.verify(token);

    User user = userRepository.getByEmail(decoded.getIssuer());

    return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
  }

  public Authentication validateCredentials(UserDTO userDTO) {
    authenticationService.authenticateUser(DTOMapper.INSTANCE.convertUserLoginDTOToEntity(userDTO));
    return new UsernamePasswordAuthenticationToken(userDTO, null, Collections.emptyList());
  }

// ######################################### Password Hashing #########################################

  public User hashNewPassword(User user) {
    String password = user.getPassword();
    String[] hashedPasswordAndSalt = hashPassword(new String[] {password});

    user.setPassword(hashedPasswordAndSalt[0]);
    user.setSalt(hashedPasswordAndSalt[1]);
    return user;
  }

  public static Boolean checkUserPassword(User checkUser, User storedUser) {
    // Retrieve salt from user
    String stringSalt = storedUser.getSalt();
    String checkPassword = checkUser.getPassword();

    // Encrypt the input password with the salt and compare it to the stored password
    String[] checkPasswordAndSalt = {checkPassword, stringSalt};
    String[] checkHashPasswordAndSalt = hashPassword(checkPasswordAndSalt);
    return checkHashPasswordAndSalt[0].equals(storedUser.getPassword());
  }

  public static String[] hashPassword(String[] args) {
    String password = args[0];
    byte[] hashSalt;

    // Generate salt if there is none
    if (args.length > 1) {
      String stringSalt = args[1];
      hashSalt = Base64.getDecoder().decode(stringSalt);
    } else {
      SecureRandom random = new SecureRandom();
      hashSalt = new byte[16];
      random.nextBytes(hashSalt);
    }

    // Hash password
    KeySpec spec = new PBEKeySpec(password.toCharArray(), hashSalt, 65536, 128);
    SecretKeyFactory factory;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hash;
    try {
      hash = factory.generateSecret(spec).getEncoded();
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }

    // Convert salt and password to strings and return them
    String hashedPassword = Base64.getEncoder().encodeToString(hash);
    String salt = Base64.getEncoder().encodeToString(hashSalt);
    return new String[] {hashedPassword, salt};
  }
}
*/
