package com.letthemcook.session;

import com.letthemcook.rest.mapper.DTOChecklistMapper;
import com.letthemcook.rest.mapper.DTOSessionMapper;
import com.letthemcook.session.dto.*;
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

  @PostMapping("/api/session")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createSession(@RequestBody SessionPostDTO sessionPostDTO, @RequestHeader("Authorization") String accessToken) throws IOException {
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

  @PutMapping("/api/session")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> updateSession(@RequestBody SessionDTO sessionPutDTO, @RequestHeader("Authorization") String accessToken) {
    Session session = DTOSessionMapper.INSTANCE.convertEntityToSessionPutDTO(sessionPutDTO);
    sessionService.updateSession(session, accessToken);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/api/session/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    sessionService.deleteSession(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/api/session/credentials/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionCredentialsDTO> getSessionCredentials(@PathVariable Long sessionId, @RequestHeader("Authorization") String accessToken) {
    Session session = sessionService.getSessionCredentials(sessionId, accessToken);

    return ResponseEntity.status(HttpStatus.OK).body(DTOSessionMapper.INSTANCE.convertEntityToSessionCredentialsDTO(session));
  }

  @GetMapping("/api/sessions")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<SessionDTO>> getSessions(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(required = false) Map<String,String> allParams) {
    List<Session> queriedSessions = sessionService.getSessions(limit, offset, allParams);

    // Convert each user to the API representation
    ArrayList<SessionDTO> sessionsGetDTOS = new ArrayList<>();
    for (Session session : queriedSessions) {
      sessionsGetDTOS.add(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
    }

    return ResponseEntity.status(HttpStatus.OK).body(sessionsGetDTOS);
  }

  @PutMapping("/api/session/{id}/checklist")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> updateChecklist(@PathVariable Long id, @RequestBody CheckPutDTO checkPutDTO, @RequestHeader("Authorization") String accessToken) {
    ChecklistStep checklistStep = DTOChecklistMapper.INSTANCE.convertCheckPutDTOToEntity(checkPutDTO);

    sessionService.checkStep(id, checklistStep.getStepIndex(), checklistStep.getIsChecked(), accessToken);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/api/session/{id}/checklist")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<SessionUserStateDTO> getChecklist(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    SessionUserState checklist = sessionService.getSessionUserState(id, accessToken);

    return ResponseEntity.status(HttpStatus.OK).body(DTOSessionMapper.INSTANCE.convertEntityToSessionUserStateDTO(checklist));
  }

  @GetMapping("/api/session/me")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<SessionDTO>> getPersonalSessions(@RequestHeader("Authorization") String accessToken) {
    List<Session> queriedSessions = sessionService.getSessionsByUser(accessToken);

    // Convert each user to the API representation
    ArrayList<SessionDTO> sessionsGetDTOS = new ArrayList<>();
    for (Session session : queriedSessions) {
      sessionsGetDTOS.add(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
    }

    return ResponseEntity.status(HttpStatus.OK).body(sessionsGetDTOS);
  }

  @GetMapping("/api/sessions/open")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<SessionDTO>> getOpenSessions() {
    List<Session> queriedSessions = sessionService.getOpenSessions();

    // Convert each user to the API representation
    ArrayList<SessionDTO> sessionsGetDTOS = new ArrayList<>();
    for (Session session : queriedSessions) {
      sessionsGetDTOS.add(DTOSessionMapper.INSTANCE.convertEntityToSingleSessionDTO(session));
    }

    return ResponseEntity.status(HttpStatus.OK).body(sessionsGetDTOS);
  }
}