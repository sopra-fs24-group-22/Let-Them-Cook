package com.letthemcook.sessionrequest;

import com.letthemcook.rest.mapper.DTORequestSessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class SessionRequestController {
  private final SessionRequestService sessionRequestService;

  @Autowired
  public SessionRequestController(SessionRequestService sessionRequestService) {
    this.sessionRequestService = sessionRequestService;
  }

  @PostMapping("/api/session_request/{sessionId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createSessionRequest(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) throws IOException {
    sessionRequestService.sendSessionRequest(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
