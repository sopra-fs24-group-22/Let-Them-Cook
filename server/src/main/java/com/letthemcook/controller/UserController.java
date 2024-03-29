package com.letthemcook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.letthemcook.entity.User;
import com.letthemcook.rest.dto.UserGetDTO;
import com.letthemcook.rest.mapper.DTOMapper;
import com.letthemcook.service.UserService;

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
  public List<UserGetDTO> getAllUsers() {
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }
}
