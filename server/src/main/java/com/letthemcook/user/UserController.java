package com.letthemcook.user;

import com.letthemcook.auth.token.Token;
import com.letthemcook.auth.token.dto.TokenResponseDTO;
import com.letthemcook.rest.mapper.DTOMapper;
import com.letthemcook.user.dto.LoginRequestDTO;
import com.letthemcook.user.dto.LogoutRequestDTO;
import com.letthemcook.user.dto.RegisterRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class UserController {

  private final UserService userService;
  @Value("${refreshTokenExpirationMs}")
  private long refreshTokenExpirationMs;

  UserController(UserService userService) {
    this.userService = userService;
  }


  // TODO: Create custom error handling to avoid try/catch blocks in every Mapping method
  @PostMapping("/api/auth/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<TokenResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
      User user = DTOMapper.INSTANCE.convertUserLoginDTOToEntity(loginRequestDTO);
      Token token = userService.loginUser(user);

      Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
      response.addCookie(cookie);

      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
  }

  @PostMapping("/api/auth/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> logoutUser(@RequestBody LogoutRequestDTO logoutRequestDTO, HttpServletResponse response) {
      User user = DTOMapper.INSTANCE.convertUserLogoutDTOToEntity(logoutRequestDTO); // for possible future use
      Token token = new Token();

      token.setAccessToken(UUID.randomUUID().toString());
      token.setRefreshToken(UUID.randomUUID().toString());

      Cookie accessTokenCookie = new Cookie("accessToken", null);
      accessTokenCookie.setSecure(true);
      accessTokenCookie.setMaxAge(0);
      accessTokenCookie.setPath("/");
      response.addCookie(accessTokenCookie);

      Cookie refreshTokenCookie = new Cookie("refreshToken", null);
      refreshTokenCookie.setSecure(true);
      refreshTokenCookie.setMaxAge(0);
      refreshTokenCookie.setPath("/");
      response.addCookie(refreshTokenCookie);

      return ResponseEntity.noContent().build();
  }

  @PostMapping("/api/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<TokenResponseDTO> createUser(@RequestBody RegisterRequestDTO registerRequestDTO, HttpServletResponse response) {
      User userInput = DTOMapper.INSTANCE.convertRegisterDTOtoEntity(registerRequestDTO);
      Token token = userService.createUser(userInput);

      Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
      cookie.setSecure(true);
      cookie.setHttpOnly(true);
      cookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));
      response.addCookie(cookie);

      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
  }

  @PostMapping("/api/auth/refresh")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<TokenResponseDTO> refreshToken(@CookieValue String refreshToken) {
      Token token = userService.refreshToken(refreshToken);
      return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToTokenDTO(token));
  }
}