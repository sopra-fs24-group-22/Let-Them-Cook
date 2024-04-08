package com.letthemcook.user;

import com.letthemcook.auth.config.GlobalExceptionHandler;
import com.letthemcook.auth.config.JwtHelper;
import com.letthemcook.auth.refreshToken.RefreshToken;
import com.letthemcook.auth.refreshToken.RefreshTokenRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Service
@Transactional
public class UserService {
  @Autowired
  private final UserRepository userRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtHelper jwtHelper;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, SequenceGeneratorService sequenceGeneratorService, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, JwtHelper jwtHelper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.authenticationManager = authenticationManager;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtHelper = jwtHelper;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    checkIfUserExists(newUser);
    newUser.setId(sequenceGeneratorService.getSequenceNumber(User.SEQUENCE_NAME));

    // Generate token
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(newUser);
    refreshTokenRepository.save(refreshToken);

    newUser.setAccessToken(jwtHelper.generateAccessToken(newUser));
    newUser.setRefreshToken(jwtHelper.generateRefreshToken(newUser, refreshToken));

    // hash the password and store the hashed password as well as the salt inside the database
    // User encryptedUser = userAuthenticationProvider.hashNewPassword(newUser);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    userRepository.save(newUser);
    return newUser;
  }

  public User loginUser(User checkUser) {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(checkUser.getEmail(), checkUser.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      User user = (User) authentication.getPrincipal();

      // Generate token
      RefreshToken refreshToken = new RefreshToken();
      refreshToken.setUser(user);
      refreshTokenRepository.save(refreshToken);

      user.setAccessToken(jwtHelper.generateAccessToken(user));
      user.setRefreshToken(jwtHelper.generateRefreshToken(user, refreshToken));
      userRepository.save(user);

      return user;
  }

  public User logoutUser(User logoutUser) {
    checkIfUserExists(logoutUser);
    User user = userRepository.getByEmail(logoutUser.getEmail());
    // user.setToken(null);
    userRepository.save(user);
    return user;
  }

  public User refreshToken(String refreshTokenString) {
    // Token valid and exists in DB
    if(jwtHelper.validateRefreshToken(refreshTokenString) && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
      refreshTokenRepository.deleteById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString));

      User user = userRepository.getById(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));

      // Generate new token
      RefreshToken refreshToken = new RefreshToken();
      refreshToken.setUser(user);
      refreshTokenRepository.save(refreshToken);

      String accessToken = jwtHelper.generateAccessToken(user);
      String newRefreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);

      user.setAccessToken(accessToken);
      user.setRefreshToken(newRefreshTokenString);

      return user;
    }
    throw new BadCredentialsException("invalid token");
  }

// ######################################### Util #########################################

  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.getByEmail(userToBeCreated.getEmail());

    String baseErrorMessage = "add user failed because email already exists";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "email", "is"));
    }
  }
}
