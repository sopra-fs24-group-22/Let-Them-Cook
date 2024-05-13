package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionRepository;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import com.letthemcook.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class SessionRequestService {
  private final SessionRequestRepository sessionRequestRepository;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;

  @Autowired
  public SessionRequestService(@Qualifier("sessionRequestRepository") SessionRequestRepository sessionRequestRepository, JwtService jwtService, UserRepository userRepository, SessionRepository sessionRepository) {
    this.sessionRequestRepository = sessionRequestRepository;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
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

  public SingleSessionRequests getSingleSessionRequest(Long sessionId, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    Long userId = userRepository.getByUsername(username).getId();
    Session session = sessionRepository.getById(sessionId);

    // Check if the user is the host of the session
    if (!Objects.equals(session.getHostId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the host of this session");
    }

    List<SessionRequest> userSessionRequests = getSessionRequestsContainingSessionId(sessionId);
    HashMap<Long, QueueStatus> sessionRequests = new HashMap<Long, QueueStatus>();

    // Add request information to the
    for (SessionRequest sessionRequest : userSessionRequests) {
         QueueStatus status = sessionRequest.getUserSessions().get(sessionId);
         sessionRequests.put(sessionRequest.getUserId(), status);
    }

    SingleSessionRequests singleSessionRequests = new SingleSessionRequests();
    singleSessionRequests.setSessionRequests(sessionRequests);

    return singleSessionRequests;
  }

  // ################################# Util #################################################################

  public void deleteSessionRequest(Long userId) {
    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);
    sessionRequestRepository.delete(sessionRequest);
  }

  public List<SessionRequest> getSessionRequestsContainingSessionId(Long sessionId) {
    List<SessionRequest> allSessionRequests = sessionRequestRepository.findAll();
    List<SessionRequest> sessionRequestsContainingSessionId = new ArrayList<>();

    for (SessionRequest sessionRequest : allSessionRequests) {
      if (sessionRequest.getUserSessions().containsKey(sessionId)) {
        sessionRequestsContainingSessionId.add(sessionRequest);
      }
    }

    return sessionRequestsContainingSessionId;
  }
}
