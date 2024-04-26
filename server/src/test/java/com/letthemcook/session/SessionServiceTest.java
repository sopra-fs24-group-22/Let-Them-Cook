package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.videosdk.VideoSDKService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
  @Mock
  private SessionRepository sessionRepository;
  @Mock
  private SequenceGeneratorService sequenceGeneratorService;
  @Mock
  private JwtService jwtService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private VideoSDKService videoSDKService;
  @Mock MongoTemplate mongoTemplate;

  @InjectMocks
  private SessionService sessionService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionService = new SessionService(sessionRepository, sequenceGeneratorService, userRepository, jwtService, recipeRepository, mongoTemplate, videoSDKService);
  }

  @AfterEach
  public void tearDown() {
  }

  // ######################################### Create Session Tests #####################################

  @Test
  public void createSessionReturnsExpectedSession() throws IOException {
    Session session = new Session();
    session.setRecipeId(1L);
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    String username = "username";
    Long userId = 1L;
    String roomId = "roomId";
    Recipe recipe = new Recipe();
    recipe.setChecklist(new ArrayList<>());

    when(jwtService.extractUsername(accessToken)).thenReturn(username);
    when(userRepository.getByUsername("username")).thenReturn(user);
    when(sequenceGeneratorService.getSequenceNumber(Session.SEQUENCE_NAME)).thenReturn(1L);
    when(videoSDKService.fetchRoomId()).thenReturn(roomId);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    Session result = sessionService.createSession(session, accessToken);

    assertEquals(1L, result.getId());
    assertEquals(userId, result.getHostId());
    assertEquals(roomId, result.getRoomId());
    assertEquals(new HashMap<Long, Integer>(), result.getChecklistCount());
  }

  @Test
  public void createSessionThrowsIOExceptionWhenFetchRoomIdFails() throws IOException {
    Session session = new Session();
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);
    when(sequenceGeneratorService.getSequenceNumber(Session.SEQUENCE_NAME)).thenReturn(1L);
    when(videoSDKService.fetchRoomId()).thenThrow(new IOException());

    assertThrows(IOException.class, () -> sessionService.createSession(session, accessToken));
  }

  // ######################################### Get Session Tests #########################################
  @Test
  public void getSessionReturnsExpectedSession() {
    Session expectedSession = new Session();
    expectedSession.setId(1L);
    when(sessionRepository.getById(1L)).thenReturn(expectedSession);

    Session result = sessionService.getSession(1L);

    assertEquals(expectedSession, result);
  }

  @Test
  public void getSessionThrowsResponseStatusExceptionWhenSessionNotFound() {
    when(sessionRepository.getById(1L)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSession(1L));
  }

  // ######################################### Get Session Tests #########################################

  @Test
  public void deleteSessionSuccessfullyDeletesSession() {
    String accessToken = "accessToken";
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(user.getId());

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    sessionService.deleteSession(sessionId, accessToken);

    verify(sessionRepository, times(1)).deleteById(sessionId);
  }

  @Test
  public void deleteSessionThrowsExceptionWhenSessionNotFound() {
    String accessToken = "accessToken";
    Long sessionId = 1L;

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(sessionRepository.getById(sessionId)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.deleteSession(sessionId, accessToken));
  }

  @Test
  public void deleteSessionThrowsExceptionWhenUserNotAuthorized() {
    String accessToken = "accessToken";
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(2L);

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    assertThrows(ResponseStatusException.class, () -> sessionService.deleteSession(sessionId, accessToken));
  }

  // ######################################### Get Session Credentials Tests ##############################

  @Test
  public void getSessionCredentialsReturnsExpectedSession() {
    String accessToken = "accessToken";
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(user.getId());
    session.setCurrentParticipantCount(0);
    session.setMaxParticipantCount(2);
    ArrayList<Long> participants = new ArrayList<>();
    participants.add(1L);
    session.setParticipants(participants);
    session.setCurrentParticipantCount(1);

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    Session result = sessionService.getSessionCredentials(sessionId, accessToken);

    assertEquals(sessionId, result.getId());
    assertEquals(1, result.getCurrentParticipantCount());
  }

  @Test
  public void getSessionCredentialsThrowsExceptionWhenSessionIsFull() {
    String accessToken = "accessToken";
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(user.getId());
    session.setCurrentParticipantCount(2);
    session.setMaxParticipantCount(2);


    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSessionCredentials(sessionId, accessToken));
  }

  @Test
  public void getSessionCredentialsThrowsExceptionWhenSessionNotFound() {
    String accessToken = "Bearer accessToken";
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    user.setUsername("username");


    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(null);

    try {
      sessionService.getSessionCredentials(sessionId, "Bearer accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
    }
  }

  // ######################################### Get Checklist Tests #########################################

  @Test
  public void testGetChecklistCountReturnsExpectedChecklistCount() {
    // Setup user
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup session
    Long sessionId = 1L;
    Session session = new Session();
    session.setId(sessionId);

    ArrayList<Long> participants = new ArrayList<>();
    participants.add(1L);
    session.setParticipants(participants);

    HashMap<Long, Integer> checklistCount = new HashMap<>();
    checklistCount.put(1L, 5);
    checklistCount.put(2L, 3);
    checklistCount.put(3L, 0);
    session.setChecklistCount(checklistCount);

    // Mock services
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(jwtService.extractUsername("Bearer accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    HashMap<Long, Integer> result = sessionService.getChecklistCount(sessionId, "Bearer accessToken");

    assertEquals(checklistCount, result);
  }

  @Test
  public void testGetChecklistCountThrowsExceptionWhenSessionNotFound() {
    Long sessionId = 1L;
    when(sessionRepository.getById(sessionId)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.getChecklistCount(sessionId, "Bearer accessToken"));
  }

  @Test
  public void testGetChecklistCountThrowsExceptionWhenUserNotAuthorized() {
    // Setup unauthorized user
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup session
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(2L);

    assertThrows(ResponseStatusException.class, () -> sessionService.getChecklistCount(sessionId, "Bearer accessToken"));
  }
}
