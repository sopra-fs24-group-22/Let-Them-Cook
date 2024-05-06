package com.letthemcook.sessionrequest;

import com.letthemcook.rest.mapper.DTORequestSessionMapper;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
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
  public ResponseEntity<Void> createSessionRequest(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    sessionRequestService.sendSessionRequest(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/api/session_request/{sessionId}/accept")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> acceptSessionRequest(@PathVariable Long sessionId, @RequestBody SessionRequestDTO sessionRequestDTO, @RequestHeader("Authorization") String accessToken) {
    SessionRequest sessionRequest = DTORequestSessionMapper.INSTANCE.convertSessionRequestDTOToEntity(sessionRequestDTO);
    sessionRequestService.processSessionRequest(sessionId, sessionRequest, true);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/api/session_request/{sessionId}/deny")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> denySessionRequest(@PathVariable Long sessionId, @RequestBody SessionRequestDTO sessionRequestDTO, @RequestHeader("Authorization") String accessToken) {
    SessionRequest sessionRequest = DTORequestSessionMapper.INSTANCE.convertSessionRequestDTOToEntity(sessionRequestDTO);
    sessionRequestService.processSessionRequest(sessionId, sessionRequest, false);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
