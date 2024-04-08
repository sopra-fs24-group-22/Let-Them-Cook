package com.letthemcook.auth.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
  public ResponseStatusException handleException(Exception ex) {
    log.error("Default Exception Handler -> caught:", ex);
    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseStatusException handleSecurityException(Exception ex) {
    if(ex instanceof BadCredentialsException) {
      return new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    }
    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
  }
}
