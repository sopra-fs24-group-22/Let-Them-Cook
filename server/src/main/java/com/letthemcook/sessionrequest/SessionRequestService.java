package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionRepository;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    Session session = sessionRepository.getById(sessionId);

    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);
    if (sessionRequest.getUserSessions().containsKey(sessionId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already sent a session request for this session");
    }

    int participants = getSessionRequestsContainingSessionId(sessionId, true).size();
    int maxParticipants = session.getMaxParticipantCount();

    if (participants >= maxParticipants) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "The session is full");
    }

    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    sessionRequestRepository.save(sessionRequest);
  }

  public void processSessionRequest(Long sessionId, SessionRequest sessionRequestUser, Boolean evaluate) {
    Long userId = sessionRequestUser.getUserId();
    Session session = sessionRepository.getById(sessionId);
    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);

    if (!sessionRequest.getUserSessions().containsKey(sessionId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user has not sent a session request for this session");
    }

    // Check if session is full
    if (session.getAcceptedParticipantCount() >= session.getMaxParticipantCount()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "The session is full");
    }

    QueueStatus status = sessionRequest.getUserSessions().get(sessionId);

    if (status == QueueStatus.ACCEPTED || status == QueueStatus.REJECTED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "The request has already been accepted or rejected");
    }

    if (evaluate) {
      sessionRequest.getUserSessions().put(sessionId, QueueStatus.ACCEPTED);
      session.setAcceptedParticipantCount(session.getAcceptedParticipantCount() + 1);
    } else {
      sessionRequest.getUserSessions().put(sessionId, QueueStatus.REJECTED);
    }
    sessionRequestRepository.save(sessionRequest);
    sessionRepository.save(session);
  }

  public SessionRequest getSessionRequests(String sessionId) {
    String username = jwtService.extractUsername(sessionId);
    Long userId = userRepository.getByUsername(username).getId();

    return sessionRequestRepository.getSessionRequestByUserId(userId);
  }

  public ArrayList<SingleSessionRequests> getSingleSessionRequest(Long sessionId, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    Long userId = userRepository.getByUsername(username).getId();
    Session session = sessionRepository.getById(sessionId);

    // Check if the user is the host of the session
    if (!Objects.equals(session.getHostId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the host of this session");
    }

    // All session requests containing the session id
    List<SessionRequest> userSessionRequests = getSessionRequestsContainingSessionId(sessionId, false);

    ArrayList<SingleSessionRequests> singleSessionRequests = new ArrayList<>();

    // Add request information to the list
    for (SessionRequest sessionRequest : userSessionRequests) {
         QueueStatus status = sessionRequest.getUserSessions().get(sessionId);

         User user = userRepository.getById(sessionRequest.getUserId());
         SingleSessionRequests singleSessionRequest = new SingleSessionRequests();
         singleSessionRequest.setUserId(user.getId());
         singleSessionRequest.setUsername(user.getUsername());
         singleSessionRequest.setQueueStatus(status);

          singleSessionRequests.add(singleSessionRequest);
    }

    return singleSessionRequests;
  }

  // ################################# Util #################################################################

  public void deleteSessionRequest(Long userId) {
    SessionRequest sessionRequest = sessionRequestRepository.getSessionRequestByUserId(userId);
    sessionRequestRepository.delete(sessionRequest);
  }

  // Get all session requests containing the session id, if alreadyAccepted is true, return only the requests that have been accepted
  public List<SessionRequest> getSessionRequestsContainingSessionId(Long sessionId, Boolean alreadyAccepted) {
    List<SessionRequest> allSessionRequests = sessionRequestRepository.findAll();
    List<SessionRequest> sessionRequestsContainingSessionId = new ArrayList<>();

    // Add all session requests containing the session id and only the processed ones if alreadyProcessed is true
    for (SessionRequest sessionRequest : allSessionRequests) {
      if (sessionRequest.getUserSessions().containsKey(sessionId)) {
        if (!alreadyAccepted) {
          sessionRequestsContainingSessionId.add(sessionRequest);
        } else {
          QueueStatus status = sessionRequest.getUserSessions().get(sessionId);
          if (status == QueueStatus.ACCEPTED) {
            sessionRequestsContainingSessionId.add(sessionRequest);
          }
        }
      }
    }

    return sessionRequestsContainingSessionId;
  }
}
