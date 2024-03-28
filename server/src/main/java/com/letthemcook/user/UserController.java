package com.letthemcook.user;

import org.springframework.http.HttpStatus;
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

  @PostMapping("/auth/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void loginUser(@RequestBody UserDTO userDTO) {
    User user = DTOMapper.INSTANCE.convertUserDTOToEntity(userDTO);
    userService.loginUser(user);
  }
}
