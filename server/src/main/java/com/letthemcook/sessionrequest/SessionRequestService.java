package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import com.letthemcook.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Service
@Transactional
public class SessionRequestService {
  private final SessionRequestRepository sessionRequestRepository;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Autowired
  public SessionRequestService(@Qualifier("sessionRequestRepository") SessionRequestRepository sessionRequestRepository, JwtService jwtService, UserRepository userRepository) {
    this.sessionRequestRepository = sessionRequestRepository;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  public void createSessionRequest(Long userId) {
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(userId);

    HashMap<Long, QueueStatus> userSessions = new HashMap<>();
    sessionRequest.setUserSessions(userSessions);

    sessionRequestRepository.save(sessionRequest);
  }

  public void sendSessionRequest(Long sessionId, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    Long userId = userRepository.getByUsername(username).getId();

    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);
    if (sessionRequest.getUserSessions().containsKey(sessionId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already sent a session request for this session");
    }
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    sessionRequestRepository.save(sessionRequest);
  }

  public void processSessionRequest(Long sessionId, SessionRequest sessionRequestUser, Boolean evaluate) {
    Long userId = sessionRequestUser.getUserId();
    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);

    if (!sessionRequest.getUserSessions().containsKey(sessionId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user has not sent a session request for this session");
    }

    if (sessionRequest.getUserSessions().get(sessionId) == QueueStatus.ACCEPTED || sessionRequest.getUserSessions().get(sessionId) == QueueStatus.REJECTED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "The request has already been accepted or rejected");
    }

    if (evaluate) {
      sessionRequest.getUserSessions().put(sessionId, QueueStatus.ACCEPTED);
    } else {
      sessionRequest.getUserSessions().put(sessionId, QueueStatus.REJECTED);
    }
    sessionRequestRepository.save(sessionRequest);
  }

  public SessionRequest getSessionRequests(String sessionId) {
    String username = jwtService.extractUsername(sessionId);
    Long userId = userRepository.getByUsername(username).getId();

    return sessionRequestRepository.getSessionRequestByUserId(userId);
  }

  public void deleteSessionRequest(Long userId) {
    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);
    sessionRequestRepository.delete(sessionRequest);
  }
}
