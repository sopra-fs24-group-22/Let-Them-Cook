package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.sessionrequest.QueueStatus;
import com.letthemcook.sessionrequest.SessionRequest;
import com.letthemcook.sessionrequest.SessionRequestRepository;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.videosdk.VideoSDKService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
  @Mock
  private MongoTemplate mongoTemplate;
  @Mock
  private SessionRequestRepository sessionRequestRepository;
  @Captor
  private ArgumentCaptor<Session> sessionCaptor;
  @InjectMocks
  private SessionService sessionService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionService = new SessionService(sessionRepository, sequenceGeneratorService, userRepository, jwtService, recipeRepository, mongoTemplate, videoSDKService, sessionRequestRepository);
    // Setup session
    Session session = new Session();
    session.setId(1L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setMaxParticipantCount(2);
    session.setParticipants(new ArrayList<>(Arrays.asList(5L, 6L)));
    session.setSessionName("sessionName");
    session.setDate(LocalDateTime.now().plusDays(2));


    SessionUserState sessionUserState = new SessionUserState();
    sessionUserState.setSessionId(session.getId());
    sessionUserState.setRecipeSteps(5);
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    currentStepValues.put(1L, new Boolean[sessionUserState.getRecipeSteps()]);
    sessionUserState.setCurrentStepValues(currentStepValues);
    session.setSessionUserState(sessionUserState);

    lenient().when(sessionRepository.getById(1L)).thenReturn(session);

    // Setup recipe
    Recipe recipe = new Recipe();
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Step 1");
    recipe.setChecklist(checklist);
    recipe.setId(1L);
    lenient().when(recipeRepository.getById(1L)).thenReturn(recipe);
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

  // ######################################### Update Session Tests #####################################

  @Test
  public void updateSessionReturnsUpdatedSession() {
    String accessToken = "accessToken";
    Session session = new Session();
    session.setId(1L);
    session.setSessionName("Updated Session");

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(new User());
    when(sessionRepository.getById(1L)).thenReturn(session);

    sessionService.updateSession(session, accessToken);

    verify(sessionRepository, times(1)).save(session);
  }

  @Test
  public void updateSessionThrowsExceptionWhenSessionNotFound() {
    String accessToken = "accessToken";
    Session session = new Session();
    session.setId(1L);

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(sessionRepository.getById(1L)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.updateSession(session, accessToken));
  }

  @Test
  public void updateSessionThrowsExceptionWhenUserNotAuthorized() {
    String accessToken = "accessToken";
    Session session = new Session();
    session.setId(1L);
    session.setHostId(2L);

    when(jwtService.extractUsername(accessToken)).thenReturn("username");
    when(userRepository.getByUsername("username")).thenReturn(new User());
    when(sessionRepository.getById(1L)).thenReturn(session);

    assertThrows(ResponseStatusException.class, () -> sessionService.updateSession(session, accessToken));
  }

  // ######################################### Get Session Credentials Tests ##############################

  @Test
  public void getSessionCredentialsReturnsSessionForHost() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(user.getId());

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);

    Session result = sessionService.getSessionCredentials(sessionId, accessToken);

    assertEquals(session, result);
  }

  @Test
  public void getSessionCredentialsReturnsSessionForAcceptedParticipant() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(2L);
    ArrayList<Long> participants = new ArrayList<>();
    session.setParticipants(participants);
    session.setCurrentParticipantCount(0);
    SessionRequest sessionRequest = new SessionRequest();
    HashMap<Long, QueueStatus> userSessions = new HashMap<>();
    userSessions.put(sessionId, QueueStatus.ACCEPTED);
    sessionRequest.setUserSessions(userSessions);

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(sessionRequestRepository.getSessionRequestByUserId(user.getId())).thenReturn(sessionRequest);

    Session result = sessionService.getSessionCredentials(sessionId, accessToken);

    assertEquals(session, result);
  }

  @Test
  public void getSessionCredentialsThrowsWhenSessionNotFound() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("username");

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSessionCredentials(sessionId, accessToken));
  }

  @Test
  public void getSessionCredentialsThrowsWhenUserNotAccepted() {
    Long sessionId = 1L;
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("username");
    Session session = new Session();
    session.setId(sessionId);
    session.setHostId(2L);
    SessionRequest sessionRequest = new SessionRequest();
    HashMap<Long, QueueStatus> userSessions = new HashMap<>();
    userSessions.put(sessionId, QueueStatus.PENDING);
    sessionRequest.setUserSessions(userSessions);

    when(jwtService.extractUsername(accessToken)).thenReturn(user.getUsername());
    when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
    when(sessionRepository.getById(sessionId)).thenReturn(session);
    when(sessionRequestRepository.getSessionRequestByUserId(user.getId())).thenReturn(sessionRequest);

    assertThrows(ResponseStatusException.class, () -> sessionService.getSessionCredentials(sessionId, accessToken));
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
    HashMap<Long, Date> lastActiveUsers = new HashMap<>();
    currentStepValues.put(1L, new Boolean[sessionUserState.getRecipeSteps()]);
    sessionUserState.setCurrentStepValues(currentStepValues);
    sessionUserState.setLastActiveUsers(lastActiveUsers);
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

  // ######################################### Check step Tests #########################################

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

  // ######################################### Get sessions Tests #########################################

  @Test
  public void testGetSessionsNoParamsSuccess() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();

    Session session = new Session();
    session.setId(2L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("sessionName");
    session.setDate(LocalDateTime.now().plusDays(1));

    // Mock Services
    List<Session> sessions = new ArrayList<>();
    sessions.add(session);
    sessions.add(sessionRepository.getById(1L));
    when(mongoTemplate.find(any(), eq(Session.class))).thenReturn(sessions);

    // Perform test
    List<Session> result = sessionService.getSessions(limit, offset, allParams);

    assertEquals(2, result.size());
  }

  @Test
  public void testGetSessionsByNameSuccess() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();
    allParams.put(QueryParams.SESSION_NAME.getValue(), "name");

    Session session = new Session();
    session.setId(2L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("testSession");
    session.setDate(LocalDateTime.now().plusDays(1));

    // Mock Services
    List<Session> sessions = new ArrayList<>();
    sessions.add(sessionRepository.getById(1L));

    when(mongoTemplate.find(any(), eq(Session.class))).thenReturn(sessions);

    // Perform test
    List<Session> result = sessionService.getSessions(limit, offset, allParams);

    assertEquals(1, result.size());
    assertEquals(sessionRepository.getById(1L), result.get(0));
  }

  @Test
  public void testGetSessionsByDateSuccess() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();
    allParams.put(QueryParams.DATE.getValue(), LocalDateTime.parse("2021-01-01T00:00:00").toString());

    Session session = new Session();
    session.setId(2L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("sessionName");
    session.setDate(LocalDateTime.now().plusDays(1));

    // Mock Services
    List<Session> sessions = new ArrayList<>();
    sessions.add(session);
    sessions.add(sessionRepository.getById(1L));
    when(mongoTemplate.find(any(), eq(Session.class))).thenReturn(sessions);

    // Perform test
    List<Session> result = sessionService.getSessions(limit, offset, allParams);

    assertEquals(2, result.size());
    assertEquals(session, result.get(0));
  }

  @Test
  public void testGetSessionsByDateAndNameSuccess() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();
    allParams.put(QueryParams.DATE.getValue(), LocalDateTime.now().plusDays(1).toString());
    allParams.put(QueryParams.SESSION_NAME.getValue(), "name");

    Session session = new Session();
    session.setId(2L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("sessionName");
    session.setDate(LocalDateTime.now());

    // Mock Services
    List<Session> sessions = new ArrayList<>();
    sessions.add(sessionRepository.getById(1L));
    when(mongoTemplate.find(any(), eq(Session.class))).thenReturn(sessions);

    // Perform test
    List<Session> result = sessionService.getSessions(limit, offset, allParams);

    assertEquals(1, result.size());
    assertEquals(sessionRepository.getById(1L), result.get(0));
  }

  // ######################################### Get open sessions Tests #########################################

  @Test
  public void testGetOpenSessionsNotFull() {
    // Setup
    Session session = sessionRepository.getById(1L);

    // Mock services
    when(sessionRepository.findAllByDateAfter(any())).thenReturn(List.of(session));

    // Perform test
    List<Session> result = sessionService.getOpenSessions();

    assertEquals(1, result.size());
    assertEquals(session, result.get(0));
  }

  @Test
  public void testGetOpenSessionsFullDoesntReturn() {
    // Setup
    Session session = sessionRepository.getById(1L);
    session.setCurrentParticipantCount(2);
    session.setCurrentParticipantCount(2);

    // Mock services
    when(sessionRepository.findAllByDateAfter(any())).thenReturn(List.of(session));

    // Perform test
    List<Session> result = sessionService.getOpenSessions();

    assertEquals(0, result.size());
  }

  // ######################################### Util Tests #########################################

  @Test
  public void deleteSessionByUserSuccessfullyDeletesSession() {
    Session session = new Session();

    sessionService.deleteSessionByUser(session);

    verify(sessionRepository, times(1)).delete(session);
  }

  @Test
  public void updateSessionHostNameUpdatesNameForAllSessionsOfUser() {
    Long userId = 1L;
    String newUsername = "newUser";
    Session session1 = new Session();
    session1.setId(1L);
    session1.setHostId(userId);
    session1.setHostName("oldUser");

    Session session2 = new Session();
    session2.setId(2L);
    session2.setHostId(userId);
    session2.setHostName("oldUser");

    List<Session> sessions = Arrays.asList(session1, session2);

    when(sessionRepository.getByHostId(userId)).thenReturn(sessions);

    sessionService.updateSessionHostName(userId, newUsername);

    verify(sessionRepository, times(2)).save(sessionCaptor.capture());
    List<Session> updatedSessions = sessionCaptor.getAllValues();

    for (Session updatedSession : updatedSessions) {
      assertEquals(newUsername, updatedSession.getHostName());
    }
  }

  @Test
  public void updateSessionHostNameDoesNothingForUserWithNoSessions() {
    Long userId = 1L;
    String newUsername = "newUser";

    when(sessionRepository.getByHostId(userId)).thenReturn(new ArrayList<>());

    sessionService.updateSessionHostName(userId, newUsername);

    verify(sessionRepository, times(0)).save(any(Session.class));
  }
}
