package com.letthemcook.session;

import com.letthemcook.rest.mapper.DTOSessionMapper;
import com.letthemcook.session.dto.SessionCredentialsDTO;
import com.letthemcook.session.dto.SessionDTO;
import com.letthemcook.session.dto.SessionPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class SessionController {
  private final SessionService sessionService;

  @Autowired
  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @PostMapping("/session")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createSession(@RequestBody SessionPostDTO sessionPostDTO, @RequestHeader("Authorization") String accessToken) throws IOException {
    Session session = DTOSessionMapper.INSTANCE.convertSingleSessionDTOToEntity(sessionPostDTO);
    sessionService.createSession(session, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/session/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionDTO> getSession(@PathVariable Long sessionId) {
    Session session = sessionService.getSession(sessionId);

    return ResponseEntity.status(HttpStatus.OK).body(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
  }

  @DeleteMapping("/session/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    sessionService.deleteSession(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/session/credentials/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionCredentialsDTO> getSessionCredentials(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    Session session = sessionService.getSessionCredentials(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.OK).body(DTOSessionMapper.INSTANCE.convertEntityToSessionCredentialsDTO(session));
  }

  @GetMapping("/sessions")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<SessionDTO>> getSessions(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(required = false) Map<String,String> allParams) {
    List<Session> queriedSessions = sessionService.getSessions(limit, offset, allParams);

    // convert each user to the API representation
    ArrayList<SessionDTO> sessionsGetDTOS = new ArrayList<>();
    for (Session session : queriedSessions) {
      sessionsGetDTOS.add(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
    }

    return ResponseEntity.status(HttpStatus.OK).body(sessionsGetDTOS);
  }
}