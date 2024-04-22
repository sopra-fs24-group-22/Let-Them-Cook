package com.letthemcook.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
  Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
    log.info("{}\n{}", e.getMessage(), e.getStatus());
    return ResponseEntity
            .status(e.getStatus())
            .body(e.getMessage());
  }

  @ExceptionHandler({Exception.class, IOException.class})
  public ResponseEntity<Void> handleException(Exception e) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, MissingRequestCookieException.class, MalformedJwtException.class, MissingRequestHeaderException.class})
  public ResponseEntity<Void> handleBadCredentialsException(BadCredentialsException e) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .build();
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException e, HttpServletResponse response) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setSecure(true);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body("Token expired.");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
  }
}
