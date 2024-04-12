package com.letthemcook.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
  Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity handleResponseStatusException(ResponseStatusException e) {
    log.info("{}\n{}", e.getMessage(), e.getStatus());
    return ResponseEntity
            .status(e.getStatus())
            .body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleException(Exception e) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.getClass() + e.getMessage());
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, MissingRequestCookieException.class})
  public ResponseEntity handleBadCredentialsException(BadCredentialsException e) {
    log.info("{}\n{}\n{}", e.getMessage(), e.getCause(), e.getClass());
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
  }
}
