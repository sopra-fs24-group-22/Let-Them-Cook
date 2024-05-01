package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionRequestServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private JwtService jwtService;
  @Mock
  private SessionRequestRepository sessionRequestRepository;

  @InjectMocks
  private SessionRequestService sessionRequestService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionRequestService = new SessionRequestService(sessionRequestRepository, jwtService, userRepository);
  }

  @AfterEach
  public void tearDown() {
  }

  // ######################################### Create new Session Request #########################################

  @Test
  public void createSessionRequestSuccessfullyCreatesRequest() {
    Long userId = 1L;

    sessionRequestService.createSessionRequest(userId);

    ArgumentCaptor<SessionRequest> sessionRequestCaptor = ArgumentCaptor.forClass(SessionRequest.class);
    verify(sessionRequestRepository, times(1)).save(sessionRequestCaptor.capture());

    SessionRequest capturedSessionRequest = sessionRequestCaptor.getValue();
    assertEquals(userId, capturedSessionRequest.getUserId());
    assertTrue(capturedSessionRequest.getUserSessions().isEmpty());
  }

  // ######################################### Send Session Request #########################################

  @Test
  public void sendSessionRequestSuccessfullySendsRequest() {

    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    String accessToken = "accessToken";

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(user.getId());
    sessionRequest.setUserSessions(new HashMap<>());

    Long sessionId = 2L;

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRequestRepository.getSessionRequestByUserId(user.getId())).thenReturn(sessionRequest);

    sessionRequestService.sendSessionRequest(sessionId, accessToken);

    ArgumentCaptor<SessionRequest> sessionRequestCaptor = ArgumentCaptor.forClass(SessionRequest.class);
    verify(sessionRequestRepository, times(1)).save(sessionRequestCaptor.capture());

    SessionRequest capturedSessionRequest = sessionRequestCaptor.getValue();
    assertTrue(capturedSessionRequest.getUserSessions().containsKey(sessionId));
    assertEquals(QueueStatus.PENDING, capturedSessionRequest.getUserSessions().get(sessionId));
  }

  @Test
  public void sendSessionRequestThrowsExceptionWhenSessionRequestAlreadyExists() {
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    String accessToken = "accessToken";

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(user.getId());
    sessionRequest.setUserSessions(new HashMap<>());

    Long sessionId = 2L;

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRequestRepository.getSessionRequestByUserId(user.getId())).thenReturn(sessionRequest);

    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.sendSessionRequest(sessionId, accessToken));
  }
}
