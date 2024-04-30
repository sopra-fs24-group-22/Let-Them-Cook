package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.user.UserRepository;
import com.letthemcook.user.UserService;
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
}
