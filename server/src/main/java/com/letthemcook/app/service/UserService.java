package com.letthemcook.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.letthemcook.app.constant.UserStatus;
import com.letthemcook.app.entity.User;
import com.letthemcook.app.repository.UserRepository;
import com.letthemcook.app.rest.dto.UserPutDTO;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getSingleUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    return user;
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreationDate(System.currentTimeMillis() / 1000L);
    checkIfUserExists(newUser);
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public void updateUser(UserPutDTO userToUpdate, Long id, String token) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    if (!user.getToken().equals(token)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are only allowed to edit your own user profile!");
    }

    user.setUsername(userToUpdate.getUsername());
    System.out.println(userToUpdate.getBirthday());
    user.setBirthday(userToUpdate.getBirthday());
    userRepository.save(user);
    userRepository.flush();
  }

  public User loginUser(User userToBeLoggedIn) {
    User user = checkUserCredentialsAndReturnUser(userToBeLoggedIn);

    log.debug("User successfully logged in: {}", user);
    return user;
  }

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    }
  }

  private User checkUserCredentialsAndReturnUser(User userToBeLoggedIn) {
    User user = userRepository.findByUsername(userToBeLoggedIn.getUsername());
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The username provided does not exist!");
    }
    if (!user.getPassword().equals(userToBeLoggedIn.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password provided is incorrect!");
    }
    return user;
  }
}
