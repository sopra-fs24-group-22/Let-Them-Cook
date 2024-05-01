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
import java.util.Date;
import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
  @Mock
  private MongoTemplate mongoTemplate;
  @Mock
  private SessionUserState sessionUserState;

  @InjectMocks
  private SessionService sessionService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionService = new SessionService(sessionRepository, sequenceGeneratorService, userRepository, jwtService, recipeRepository, mongoTemplate, videoSDKService);
    // Setup session
    Session session = new Session();
    session.setId(1L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("sessionName");
    session.setDate(new Date());

    SessionUserState sessionUserState = new SessionUserState();
    sessionUserState.setSessionId(session.getId());
    sessionUserState.setRecipeSteps(5);
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    currentStepValues.put(1L, new Boolean[sessionUserState.getRecipeSteps()]);
    sessionUserState.setCurrentStepValues(currentStepValues);
    session.setSessionUserState(sessionUserState);

    when(sessionRepository.getById(1L)).thenReturn(session);
  }

  @AfterEach
  public void tearDown() {
  }

  // ######################################### Create Session Tests #####################################

  @Test
  public void createSessionReturnsExpectedSession() throws IOException {
    Session session = sessionRepository.getById(1L);

    User user = new User();
    user.setId(1L);
    String username = "username";
    Long userId = 1L;
    String roomId = "roomId";
    String accessToken = "accessToken";

    Recipe recipe = new Recipe();
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Step 1");
    recipe.setChecklist(checklist);

    SessionUserState sessionUserState = new SessionUserState();
    sessionUserState.setSessionId(1L);
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    currentStepValues.put(1L, new Boolean[recipe.getChecklist().size()]);
    sessionUserState.setCurrentStepValues(currentStepValues);

    when(jwtService.extractUsername(accessToken)).thenReturn(username);
    when(userRepository.getByUsername("username")).thenReturn(user);
    when(sequenceGeneratorService.getSequenceNumber(Session.SEQUENCE_NAME)).thenReturn(1L);
    when(videoSDKService.fetchRoomId()).thenReturn(roomId);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    Session result = sessionService.createSession(session, accessToken);

    assertEquals(1L, result.getId());
    assertEquals(userId, result.getHostId());
    assertEquals(roomId, result.getRoomId());
    assertEquals(sessionUserState.getSessionId(), result.getSessionUserState().getSessionId());
    assertEquals(0, result.getCurrentParticipantCount());
    assertEquals(0, result.getSessionUserState().getCurrentStepValues().size());
  }

  @Test
  public void createSessionThrowsIOExceptionWhenFetchRoomIdFails() throws IOException {
    Session session = sessionRepository.getById(1L);
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
    Session expectedSession = sessionRepository.getById(1L);

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
    Session session = sessionRepository.getById(sessionId);

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
    Session session = sessionRepository.getById(sessionId);
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
    Session session = sessionRepository.getById(sessionId);
    session.setCurrentParticipantCount(2);

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
    Session session = sessionRepository.getById(sessionId);

    ArrayList<Long> participants = new ArrayList<>();
    participants.add(1L);
    session.setParticipants(participants);

    SessionUserState sessionUserState = session.getSessionUserState();
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    currentStepValues.put(1L, new Boolean[sessionUserState.getRecipeSteps()]);
    sessionUserState.setCurrentStepValues(currentStepValues);
    session.setSessionUserState(sessionUserState);

    // Mock services
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(jwtService.extractUsername("Bearer accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    SessionUserState result = sessionService.getSessionUserState(sessionId, "Bearer accessToken");

    assertEquals(sessionUserState, result);
  }

  @Test
  public void testGetChecklistCountThrowsExceptionWhenSessionNotFound() {
    Long sessionId = 1L;
    when(sessionRepository.getById(sessionId)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSessionUserState(sessionId, "Bearer accessToken"));
  }

  @Test
  public void testGetChecklistCountThrowsExceptionWhenUserNotAuthorized() {
    // Setup unauthorized user
    Long sessionId = 1L;
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup session
    Session session = sessionRepository.getById(sessionId);
    session.setId(sessionId);
    session.setHostId(2L);

    // Mock Services
    when(jwtService.extractUsername("Bearer accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSessionUserState(sessionId, "Bearer accessToken"));
  }

  // ######################################### Checkstep Tests #########################################

  @Test
  public void testCheckStepSuccess() {
    // Setup User
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup Session
    Session session = sessionRepository.getById(1L);
    SessionUserState sessionUserState = session.getSessionUserState();
    HashMap<Long, Boolean[]> currentStepValues = sessionUserState.getCurrentStepValues();
    Boolean[] userSteps = currentStepValues.get(user.getId());

    Integer stepIndex = 0;
    Boolean isChecked = true;
    userSteps[stepIndex] = isChecked;
    currentStepValues.put(user.getId(), userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);
    session.setSessionUserState(sessionUserState);

    // Mock Services
    when(sessionRepository.save(session)).thenReturn(session);
    when(jwtService.extractUsername("accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    // Perform test
    Session result = sessionService.checkStep(1L, stepIndex, isChecked, "accessToken");

    assertEquals(session, result);
  }

  @Test
  public void testCheckStepThrowsExceptionWhenStepIndexOutOfBounds() {
    // Setup User
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup Session
    Session session = sessionRepository.getById(1L);
    SessionUserState sessionUserState = session.getSessionUserState();
    HashMap<Long, Boolean[]> currentStepValues = sessionUserState.getCurrentStepValues();

    Integer stepIndex = 5;
    Boolean isChecked = true;

    // Mock Services
    when(jwtService.extractUsername("accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    // Perform test
    assertThrows(IllegalArgumentException.class, () -> sessionService.checkStep(1L, stepIndex, isChecked, "accessToken"));
  }

  @Test
  public void testCheckStepThrowsExceptionWhenSTepIndexNegative() {
    // Setup User
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    // Setup Session
    Session session = sessionRepository.getById(1L);
    SessionUserState sessionUserState = session.getSessionUserState();
    HashMap<Long, Boolean[]> currentStepValues = sessionUserState.getCurrentStepValues();

    Integer stepIndex = -1;
    Boolean isChecked = true;

    // Mock Services
    when(jwtService.extractUsername("accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    // Perform test
    assertThrows(IllegalArgumentException.class, () -> sessionService.checkStep(1L, stepIndex, isChecked, "accessToken"));
  }

  @Test
  public void testCheckStepThrowsExceptionWhenUserNotAuthorized() {
    // Setup User
    User user = new User();
    user.setId(2L);
    user.setUsername("username");

    // Setup Session
    Session session = sessionRepository.getById(1L);
    SessionUserState sessionUserState = session.getSessionUserState();
    HashMap<Long, Boolean[]> currentStepValues = sessionUserState.getCurrentStepValues();

    Integer stepIndex = 0;
    Boolean isChecked = true;

    // Mock Services
    when(jwtService.extractUsername("accessToken")).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(user);

    // Perform test
    assertThrows(ResponseStatusException.class, () -> sessionService.checkStep(1L, stepIndex, isChecked, "accessToken"));
  }

}
