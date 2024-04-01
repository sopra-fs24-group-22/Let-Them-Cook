package com.letthemcook.auth;

import com.letthemcook.rest.mapper.DTOMapper;
import com.letthemcook.user.User;
import com.letthemcook.user.UserDTO;
import com.letthemcook.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

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
    if (!Objects.equals(user.getPassword(), loginUser.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is incorrect.");
    }
  }
}
