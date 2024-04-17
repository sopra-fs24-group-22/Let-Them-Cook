package com.letthemcook.session;

import com.letthemcook.rest.mapper.DTOSessionMapper;
import com.letthemcook.session.dto.SessionDTO;
import com.letthemcook.session.dto.SessionPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class SessionController {


  private final SessionService sessionService;

  @Autowired
  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @PostMapping("/api/session")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createSession(@RequestBody SessionPostDTO sessionPostDTO, @RequestHeader("Authorization") String accessToken) {
    Session session = DTOSessionMapper.INSTANCE.convertSingleSessionDTOToEntity(sessionPostDTO);
    sessionService.createSession(session, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/api/session/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionDTO> getSession(@PathVariable Long sessionId) {
    Session session = sessionService.getSession(sessionId);

    return ResponseEntity.status(HttpStatus.OK).body(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
  }

  @DeleteMapping("/api/session/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    sessionService.deleteSession(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
