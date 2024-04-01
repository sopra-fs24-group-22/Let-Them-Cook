package com.letthemcook.user;

import io.jsonwebtoken.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.letthemcook.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/api/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserDTO> getAllUsers() {
    List<User> users = userService.getUsers();
    List<UserDTO> userDTOs = new ArrayList<>();

    for (User user : users) {
      userDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userDTOs;
  }

  @PostMapping("/api/auth/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserDTO loginUser(@AuthenticationPrincipal UserDTO userDTO) {
    User user = DTOMapper.INSTANCE.convertUserLoginDTOToEntity(userDTO);
    User loggedInUser = userService.loginUser(user);
    return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(loggedInUser);
  }

  @PostMapping("/api/auth/register")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserDTO createUser(@RequestBody UserDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOToEntity(userPostDTO);
    User createdUser = userService.createUser(userInput);
    return DTOMapper.INSTANCE.convertEntityToUserPostDTO(createdUser);
  }
}