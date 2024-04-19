package com.letthemcook.user;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.cookbook.Cookbook;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.cookbook.CookbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Service
@Transactional
public class UserService {
  private final UserRepository userRepository;
  private final CookbookService cookbookService;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, CookbookService cookbookService, SequenceGeneratorService sequenceGeneratorService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.cookbookService = cookbookService;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public Token createUser(User newUser) {
    checkIfUserExists(newUser);

    // Set user data
    newUser.setId(sequenceGeneratorService.getSequenceNumber(User.SEQUENCE_NAME));
    newUser.setRole(UserRole.USER);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

    // Create cookbook
    Cookbook cookbook = cookbookService.createCookbook(newUser.getId());
    newUser.setCookbookId(cookbook.getId());

    // Create token
    Token token = new Token();

    token.setAccessToken(jwtService.generateAccessToken(newUser));
    token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), newUser));

    userRepository.save(newUser);
    return token;
  }
  
  public Token loginUser(User checkUser) {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(checkUser.getUsername(), checkUser.getPassword()));

      if (authentication.isAuthenticated()) {
        Token token = new Token();

        token.setAccessToken(jwtService.generateAccessToken(checkUser));
        token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), checkUser));
        return token;
      }
      else {
        throw new UsernameNotFoundException("invalid user request!");
      }
    } catch (BadCredentialsException e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  public User getUser(String accessToken) {
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);

    return userRepository.getByUsername(username);
    }


  public Token refreshAccessToken(String refreshTokenString) {
    try {
      User user = userRepository.getByUsername(jwtService.extractUsername(refreshTokenString));

      // Token valid and exists in DB
      if (jwtService.isTokenValid(refreshTokenString, user)) {
        // Generate new token
        String accessToken = jwtService.generateAccessToken(user);

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshTokenString);

        return token;
      } else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid refresh token!");
      }
    } catch (Exception e) {
      logger.info("Error generating refreshToken: " + e.getMessage());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid refresh token!");
    }
  }

// ######################################### Util #########################################

  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.getByEmail(userToBeCreated.getEmail());
    User userByUsername = userRepository.getByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "Creating user failed because %s already exists";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "email"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "username"));
    }
  }
}
