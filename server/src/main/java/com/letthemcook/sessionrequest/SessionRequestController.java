package com.letthemcook.sessionrequest;

import com.letthemcook.rest.mapper.DTORequestSessionMapper;
import com.letthemcook.rest.mapper.DTOSingleSessionRequestMapper;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestGetSingleDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestsGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  @GetMapping("/api/session_request")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionRequestsGetDTO> getSessionRequests(@RequestHeader("Authorization") String accessToken) throws IOException {
    SessionRequest sessionRequest = sessionRequestService.getSessionRequests(accessToken);
    SessionRequestsGetDTO sessionRequestGetDTO = DTORequestSessionMapper.INSTANCE.convertEntityToGetSessionRequestsDTO(sessionRequest);

    return ResponseEntity.status(HttpStatus.OK).body(sessionRequestGetDTO);
  }

  @GetMapping("/api/session_request/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<SessionRequestGetSingleDTO>> getSingleSessionRequests(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) throws IOException {
    ArrayList<SingleSessionRequests> singleSessionRequests = sessionRequestService.getSingleSessionRequest(sessionId, accessToken);

    ArrayList<SessionRequestGetSingleDTO> sessionRequestGetSingleDTOs = new ArrayList<>();
    for (SingleSessionRequests singleSessionRequest : singleSessionRequests) {
      SessionRequestGetSingleDTO sessionRequestGetSingleDTO = DTOSingleSessionRequestMapper.INSTANCE.convertEntityToGetSingleSessionRequestsDTO(singleSessionRequest);
      sessionRequestGetSingleDTOs.add(sessionRequestGetSingleDTO);
    }

    return ResponseEntity.status(HttpStatus.OK).body(sessionRequestGetSingleDTOs);
  }
}
