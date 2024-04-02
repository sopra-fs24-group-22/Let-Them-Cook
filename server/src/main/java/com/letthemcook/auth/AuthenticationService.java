package com.letthemcook.auth;

import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthenticationService {
  private final UserRepository userRepository;
  public AuthenticationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void authenticateUser(User loginUser) {
    User user = userRepository.findByEmail(loginUser.getEmail());
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The email is incorrect.");
    }
    if (!UserAuthenticationProvider.checkUserPassword(loginUser, user)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is incorrect.");
    }
  }
}