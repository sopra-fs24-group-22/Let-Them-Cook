package com.letthemcook.user;

import com.letthemcook.auth.config.JwtHelper;
import com.letthemcook.auth.refreshToken.RefreshTokenRepository;
import com.letthemcook.auth.refreshToken.TokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.letthemcook.rest.mapper.DTOMapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

  private final UserService userService;
  private final JwtHelper jwtHelper;
  private final RefreshTokenRepository refreshTokenRepository;
  @Value("${refreshTokenExpirationMs}")
  private long refreshTokenExpirationMs;

  UserController(UserService userService, JwtHelper jwtHelper, RefreshTokenRepository refreshTokenRepository) {
    this.userService = userService;
    this.jwtHelper = jwtHelper;
    this.refreshTokenRepository = refreshTokenRepository;
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
  public TokenDTO loginUser(@RequestBody UserDTO userDTO, HttpServletResponse response) throws IOException {
    User user = DTOMapper.INSTANCE.convertUserLoginDTOToEntity(userDTO);
    user = userService.loginUser(user);
    Cookie cookie = new Cookie("refreshToken", user.getRefreshToken());
    cookie.setSecure(true);
    cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
    response.addCookie(cookie);

    return DTOMapper.INSTANCE.convertEntityToTokenDTO(user);
  }

  @PostMapping("/api/auth/register")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public TokenDTO createUser(@RequestBody UserDTO userPostDTO, HttpServletResponse response) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOToEntity(userPostDTO);
    User createdUser = userService.createUser(userInput);

    Cookie cookie = new Cookie("refreshToken", createdUser.getRefreshToken());
    cookie.setSecure(true);
    cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
    response.addCookie(cookie);

    return DTOMapper.INSTANCE.convertEntityToTokenDTO(createdUser);
  }

  @PostMapping("/api/auth/refresh")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public TokenDTO refreshToken(@CookieValue String refreshToken) {
    User user = userService.refreshToken(refreshToken);
    return DTOMapper.INSTANCE.convertRefreshToTokenDTO(user);
  }
}