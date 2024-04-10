package com.letthemcook.user;

import com.letthemcook.auth.token.Token;
import com.letthemcook.user.dto.LoginRequestDTO;
import com.letthemcook.user.dto.RegisterRequestDTO;
import com.letthemcook.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.letthemcook.rest.mapper.DTOMapper;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

  private final UserService userService;
  @Value("${refreshTokenExpirationMs}")
  private long refreshTokenExpirationMs;

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
  public ResponseEntity loginUser(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws IOException {
    try {
      User user = DTOMapper.INSTANCE.convertUserLoginDTOToEntity(loginRequestDTO);
      Token token = userService.loginUser(user);

      Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
      cookie.setSecure(true);
      cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
      response.addCookie(cookie);

      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
    } catch (ResponseStatusException e) {
      return ResponseEntity
              .status(e.getStatus())
              .body(e.getMessage());
    }
  }

  @PostMapping("/api/auth/register")
  @ResponseBody
  public ResponseEntity createUser(@RequestBody RegisterRequestDTO registerRequestDTO, HttpServletResponse response) {
    try {
      User userInput = DTOMapper.INSTANCE.convertRegisterDTOtoEntity(registerRequestDTO);
      Token token = userService.createUser(userInput);

      Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
      cookie.setSecure(true);
      cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
      response.addCookie(cookie);

      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
    } catch (ResponseStatusException e) {
      return ResponseEntity
              .status(e.getStatus())
              .body(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/api/auth/refresh")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity refreshToken(@CookieValue String refreshToken) {
    try {
      Token token = userService.refreshToken(refreshToken);
      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
    } catch (ResponseStatusException e) {
      return ResponseEntity
              .status(e.getStatus())
              .body(e.getMessage());
    }
  }
}