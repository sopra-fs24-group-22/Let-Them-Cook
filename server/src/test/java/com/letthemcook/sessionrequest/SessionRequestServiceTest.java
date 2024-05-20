package com.letthemcook.sessionrequest;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
  @Mock
  private SessionRepository sessionRepository;

  @InjectMocks
  private SessionRequestService sessionRequestService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionRequestService = new SessionRequestService(sessionRequestRepository, jwtService, userRepository, sessionRepository);
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

    Session session = new Session();
    session.setId(1L);
    session.setMaxParticipantCount(2);

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(user.getId());
    sessionRequest.setUserSessions(new HashMap<>());

    Long sessionId = 2L;

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(sessionRequestRepository.getSessionRequestByUserId(user.getId())).thenReturn(sessionRequest);
    when(sessionRequestService.getSessionRequestsContainingSessionId(sessionId, true)).thenReturn(new ArrayList<>());

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

  // ######################################### Process Session Request #######################################

  @Test
  public void processSessionRequestSuccessfullyAcceptsRequest() {
    Long sessionId = 1L;
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(userId);
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);

    sessionRequestService.processSessionRequest(sessionId, sessionRequest, true);

    ArgumentCaptor<SessionRequest> sessionRequestCaptor = ArgumentCaptor.forClass(SessionRequest.class);
    verify(sessionRequestRepository, times(1)).save(sessionRequestCaptor.capture());

    SessionRequest capturedSessionRequest = sessionRequestCaptor.getValue();
    assertEquals(QueueStatus.ACCEPTED, capturedSessionRequest.getUserSessions().get(sessionId));
  }

  @Test
  public void processSessionRequestSuccessfullyRejectsRequest() {
    Long sessionId = 1L;
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(userId);
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);

    sessionRequestService.processSessionRequest(sessionId, sessionRequest, false);

    ArgumentCaptor<SessionRequest> sessionRequestCaptor = ArgumentCaptor.forClass(SessionRequest.class);
    verify(sessionRequestRepository, times(1)).save(sessionRequestCaptor.capture());

    SessionRequest capturedSessionRequest = sessionRequestCaptor.getValue();
    assertEquals(QueueStatus.REJECTED, capturedSessionRequest.getUserSessions().get(sessionId));
  }

  @Test
  public void processSessionRequestThrowsExceptionWhenNoRequestExists() {
    Long sessionId = 1L;
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);

    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.processSessionRequest(sessionId, sessionRequest, true));
  }

  @Test
  public void processSessionRequestThrowsExceptionWhenRequestAlreadyProcessed() {
    Long sessionId = 1L;
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.ACCEPTED);

    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.processSessionRequest(sessionId, sessionRequest, true));
  }

  // ######################################### Test Get Single Session Requests #######################################

  @Test
  public void getSingleSessionRequestReturnsCorrectDataWhenUserIsHost() {
    String accessToken = "accessToken";
    String username = "username";
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setUsername(username);

    Session session = new Session();
    Long sessionId = 1L;
    session.setHostId(userId);

    SessionRequest request = new SessionRequest();
    request.setUserId(userId);
    request.setUserSessions(new HashMap<>());
    request.getUserSessions().put(sessionId, QueueStatus.PENDING);

    List<SessionRequest> sessionRequests = new ArrayList<>();
    sessionRequests.add(request);

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    when(jwtService.extractUsername(accessToken)).thenReturn(username);
    when(userRepository.getByUsername(username)).thenReturn(user);
    when(userRepository.getById(userId)).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(sessionRequestService.getSessionRequestsContainingSessionId(sessionId, false)).thenReturn(sessionRequests);

    ArrayList<SingleSessionRequests> result = sessionRequestService.getSingleSessionRequest(sessionId, accessToken);

    assertFalse(result.isEmpty());
    assertEquals(QueueStatus.PENDING, result.get(0).getQueueStatus());
  }

  @Test
  public void getSingleSessionRequestThrowsExceptionWhenUserIsNotHost() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    String username = "username";
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setUsername(username);

    Session session = new Session();
    session.setHostId(2L); // Different from userId

    when(jwtService.extractUsername(accessToken)).thenReturn(username);
    when(userRepository.getByUsername(username)).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.getSingleSessionRequest(sessionId, accessToken));
  }

  @Test
  public void getSingleSessionRequestReturnsEmptyListWhenNoSessionRequests() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    String username = "username";
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setUsername(username);

    Session session = new Session();
    session.setHostId(userId);

    List<SessionRequest> sessionRequests = new ArrayList<>();

    when(jwtService.extractUsername(accessToken)).thenReturn(username);
    when(userRepository.getByUsername(username)).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(sessionRequestService.getSessionRequestsContainingSessionId(sessionId, false)).thenReturn(sessionRequests);

    ArrayList<SingleSessionRequests> result = sessionRequestService.getSingleSessionRequest(sessionId, accessToken);

    assertTrue(result.isEmpty());
  }

  // ######################################### Test Get User SessionRequest #######################################

  @Test
  public void getSessionRequestsReturnsCorrectData() {
    String sessionId = "sessionId";
    String username = "username";
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setUsername(username);

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(userId);

    when(jwtService.extractUsername(sessionId)).thenReturn(username);
    when(userRepository.getByUsername(username)).thenReturn(user);
    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);

    SessionRequest result = sessionRequestService.getSessionRequests(sessionId);

    assertEquals(userId, result.getUserId());
  }

  @Test
  public void getSessionRequestsThrowsExceptionWhenUserNotFound() {
    String sessionId = "sessionId";
    String username = "username";

    when(jwtService.extractUsername(sessionId)).thenReturn(username);
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).when(userRepository).getByUsername(username);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.getSessionRequests(sessionId));
  }

  @Test
  public void getSessionRequestsThrowsExceptionWhenSessionRequestNotFound() {
    String sessionId = "sessionId";
    String username = "username";
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setUsername(username);

    when(jwtService.extractUsername(sessionId)).thenReturn(username);
    when(userRepository.getByUsername(username)).thenReturn(user);
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session request not found")).when(sessionRequestRepository).getSessionRequestByUserId(userId);

    assertThrows(ResponseStatusException.class, () -> sessionRequestService.getSessionRequests(sessionId));
  }

  // ######################################### Test Util #######################################

  @Test
  public void deleteSessionRequestSuccessfullyDeletesRequest() {
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserId(userId);

    when(sessionRequestRepository.getSessionRequestByUserId(userId)).thenReturn(sessionRequest);
    sessionRequestService.deleteSessionRequest(userId);
    verify(sessionRequestRepository, times(1)).delete(sessionRequest);
  }

  @Test
  public void deleteSessionRequestThrowsExceptionWhenSessionRequestNotFound() {
    Long userId = 1L;
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session request not found")).when(sessionRequestRepository).delete(any());
    assertThrows(ResponseStatusException.class, () -> sessionRequestService.deleteSessionRequest(userId));
  }

  @Test
  public void getSessionRequestsContainingSessionIdReturnsCorrectData() {
    Long sessionId = 1L;
    Long userId = 1L;

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);
    sessionRequest.getUserSessions().put(sessionId, QueueStatus.PENDING);

    when(sessionRequestRepository.findAll()).thenReturn(Collections.singletonList(sessionRequest));

    List<SessionRequest> result = sessionRequestService.getSessionRequestsContainingSessionId(sessionId, false);

    assertFalse(result.isEmpty());
    assertEquals(userId, result.get(0).getUserId());
  }

  @Test
  public void getSessionRequestsContainingSessionIdReturnsEmptyListWhenNoMatch() {
    Long sessionId = 1L;
    Long userId = 1L;

    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);
    sessionRequest.getUserSessions().put(sessionId + 1, QueueStatus.PENDING); // Different session id

    when(sessionRequestRepository.findAll()).thenReturn(Collections.singletonList(sessionRequest));

    List<SessionRequest> result = sessionRequestService.getSessionRequestsContainingSessionId(sessionId, false);

    assertTrue(result.isEmpty());
  }
}
