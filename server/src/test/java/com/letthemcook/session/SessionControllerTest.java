package com.letthemcook.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.dto.CheckPutDTO;
import com.letthemcook.session.dto.SessionDTO;
import com.letthemcook.session.dto.SessionPostDTO;
import com.letthemcook.user.User;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(SessionController.class)
  @WebAppConfiguration
  @ContextConfiguration
  public class SessionControllerTest {
    @MockBean
    private SessionService sessionService;
    @MockBean
    private SessionRepository sessionRepository;
    @MockBean
    private UserController userController;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SessionController sessionController;

    // ######################################### Setup & Teardown #########################################

    @BeforeEach
    public void setup() {
      // Setup test session
      ArrayList<Long> participantList = new ArrayList<>();
      participantList.add(2L);
      LocalDateTime date = LocalDateTime.now().plusDays(1);

      Session session = new Session();
      session.setId(1L);
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(2);
      session.setHostId(1L);
      session.setHostName("testUser");
      session.setRecipeId(4L);
      session.setRecipeName("Test Recipe");
      session.setMaxParticipantCount(3);
      session.setParticipants(participantList);
      session.setCurrentParticipantCount(1);
      session.setDate(date);

      SessionUserState sessionUserState = new SessionUserState();
      sessionUserState.setSessionId(session.getId());
      sessionUserState.setRecipeSteps(3);
      sessionUserState.setLastActiveUsers(new HashMap<>());
      sessionUserState.setCurrentStepValues(new HashMap<>());
      session.setSessionUserState(sessionUserState);

      when(sessionRepository.getById(session.getId())).thenReturn(session);
      sessionRepository.save(session);
    }

    @AfterEach
    public void tearDown() {
      sessionRepository.deleteAll();
    }

    // ######################################### Create Session Tests #########################################

    @Test
    @WithMockUser(username = "testUser", password = "testPassword")
    public void testCreateSessionSuccess() throws Exception {
      // Setup test session
      Date date = new Date();

      SessionPostDTO sessionRequest = new SessionPostDTO();
      sessionRequest.setSessionName("Test Session");
      sessionRequest.setMaxParticipantCount(3);
      sessionRequest.setRecipe(2L);
      sessionRequest.setDate(date);
      String objectMapper = new ObjectMapper().writeValueAsString(sessionRequest);
      System.out.println(objectMapper);

      // Mock session service
      Session session = sessionRepository.getById(1L);
      when(sessionService.createSession(any(), any())).thenReturn(session);

      // Perform test
      MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                      .header("Authorization", "Bearer testToken")
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(new ObjectMapper().writeValueAsString(sessionRequest)))
              .andExpect(MockMvcResultMatchers.status().isCreated())
              .andReturn();

      String res = result.getResponse().getContentAsString();
      System.out.println(res);
    }

    @Test
    public void testCreateSessionFailureUnauthorized() throws Exception {
      // Setup test session
      Date date = new Date();

      SessionPostDTO sessionRequest = new SessionPostDTO();
      sessionRequest.setSessionName("Test Session");
      sessionRequest.setMaxParticipantCount(3);
      sessionRequest.setRecipe(2L);
      sessionRequest.setDate(date);

      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(new ObjectMapper().writeValueAsString(sessionRequest)))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // ######################################### Get Session Tests #########################################

    @Test
    @WithMockUser
    public void testGetSessionSuccess() throws Exception {
      // Mock session service
      Session session = sessionRepository.getById(1L);
      when(sessionService.getSession(1L)).thenReturn(session);

      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1")
                      .header("Authorization", "Bearer testToken")
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetSessionFailureUnauthorized() throws Exception {
      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1")
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // ######################################### Get Sessions Tests #########################################

    @Test
    @WithMockUser
    public void testGetSessionsSuccess() throws Exception {
      // Setup test sessions
      Date date = new Date();

      Session session = new Session();
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(3);
      session.setRecipeId(2L);
      session.setDate(date);
      session.setHostId(1L);
      session.setParticipants(new ArrayList<>());

      ArrayList<Session> sessions = new ArrayList<>();

      for(int i = 0; i < 15; i++) {
        session.setId((long) i);
        sessions.add(session);
      }

      // Mock session service
      when(sessionService.getSessions(null, null, null)).thenReturn(sessions);

      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.get("/api/sessions")
                      .header("Authorization", "Bearer testToken")
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetSessionsFailureUnauthorized() throws Exception {
      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.get("/api/sessions")
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

  // ######################################### Get Personal Session Tests #########################################

  @Test
  @WithMockUser(username = "testUser", password = "testPassword")
  public void testGetMeSuccess() throws Exception {
    // Setup User
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup session user is participant of
    Session session = sessionRepository.getById(1L);
    List<Session> sessions = new ArrayList<>();
    sessions.add(session);

    // Mock Services
    when(sessionService.getSessionsByUser(any())).thenReturn(sessions);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/me")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(session.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].host").value(session.getHostId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].recipe").value(session.getRecipeId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].sessionName").value(session.getSessionName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].maxParticipantCount").value(session.getMaxParticipantCount()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].currentParticipantCount").value(session.getCurrentParticipantCount()));
  }

  // ######################################### Put Session Tests #########################################

  @Test
  @WithMockUser(username = "testUser", password = "testPassword")
  public void testUpdateSessionSuccess() throws Exception {
    Date date = new Date();
    SessionDTO sessionPutDTO = new SessionDTO();
    sessionPutDTO.setId(1L);
    sessionPutDTO.setSessionName("Updated Session");
    sessionPutDTO.setMaxParticipantCount(3);
    sessionPutDTO.setRecipe(2L);
    sessionPutDTO.setDate(date);
    sessionPutDTO.setHost(1L);
    sessionPutDTO.setParticipants(new ArrayList<>());

    doNothing().when(sessionService).updateSession(any(), any());

    mockMvc.perform(MockMvcRequestBuilders.put("/api/session")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionPutDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testUpdateSessionFailureUnauthorized() throws Exception {
    Date date = new Date();
    SessionDTO sessionPutDTO = new SessionDTO();
    sessionPutDTO.setId(1L);
    sessionPutDTO.setSessionName("Updated Session");
    sessionPutDTO.setMaxParticipantCount(3);
    sessionPutDTO.setRecipe(2L);
    sessionPutDTO.setDate(date);
    sessionPutDTO.setHost(1L);
    sessionPutDTO.setParticipants(new ArrayList<>());

    mockMvc.perform(MockMvcRequestBuilders.put("/api/session")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionPutDTO)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Get Open Sessions Tests #########################################

  @Test
  @WithMockUser
  public void testGetOpenSessionsOneSession() throws Exception {
    // Setup test session
    Session session = sessionRepository.getById(1L);

    // Mock service
    when(sessionService.getOpenSessions()).thenReturn(List.of(session));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/sessions/open")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1));

  }

  @Test
  @WithMockUser
  public void testGetOpenSessionsWithMultipleSessions() throws Exception {
    // Setup test session
    Session session = sessionRepository.getById(1L);
    Session session2 = new Session();
    session2.setId(2L);
    session2.setSessionName("Test Session 2");
    session2.setMaxParticipantCount(3);
    session2.setRecipeId(2L);
    session2.setDate(LocalDateTime.now());
    session2.setHostId(1L);
    session2.setParticipants(new ArrayList<>());

    // Mock service
    when(sessionService.getOpenSessions()).thenReturn(List.of(session, session2));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/sessions/open")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
  }

  // ######################################### Get Checklist Tests #########################################

  @Test
  @WithMockUser
  public void testGetChecklistSuccess() throws Exception {
    // Setup test session
    Session session = sessionRepository.getById(1L);

    // Mock service
    when(sessionService.getSessionUserState(Mockito.anyLong(), anyString())).thenReturn(session.getSessionUserState());

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3));
  }

  @Test
  @WithMockUser
  public void testGetChecklistNotFound() throws Exception {
    // Mock service
    when(sessionService.getSessionUserState(Mockito.anyLong(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser
  public void testGetChecklistUnauthorized() throws Exception {
    // Mock Services
    when(sessionService.getSessionUserState(Mockito.anyLong(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Put Checklist Tests #########################################

  @Test
  @WithMockUser
  public void testPutChecklistSuccess() throws Exception {
    // Setup DTO
    Session session = sessionRepository.getById(1L);
    CheckPutDTO checkPutDTO = new CheckPutDTO();
    checkPutDTO.setIsChecked(true);
    checkPutDTO.setStepIndex(1L);

    // Mock service
    when(sessionService.checkStep(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyBoolean(), anyString())).thenReturn(session);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.put("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(checkPutDTO)))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser
  public void testPutChecklistNotFound() throws Exception {
    // Setup DTO
    CheckPutDTO checkPutDTO = new CheckPutDTO();
    checkPutDTO.setIsChecked(true);
    checkPutDTO.setStepIndex(1L);

    // Mock service
    when(sessionService.checkStep(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyBoolean(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.put("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(checkPutDTO)))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser
  public void testPutChecklistUnauthorized() throws Exception {
    // Setup DTO
    CheckPutDTO checkPutDTO = new CheckPutDTO();
    checkPutDTO.setIsChecked(true);
    checkPutDTO.setStepIndex(1L);

    // Mock service
    when(sessionService.checkStep(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyBoolean(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.put("/api/session/1/checklist")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(checkPutDTO)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Delete Session Tests #########################################

  @Test
  public void testDeleteSessionSuccessfully() {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";

    ResponseEntity<Void> response = sessionController.deleteSession(sessionId, accessToken);

    verify(sessionService, times(1)).deleteSession(sessionId, accessToken);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testDeleteSessionWithInvalidSessionId() {
    Long sessionId = -1L;
    String accessToken = "Bearer accessToken";

    ResponseEntity<Void> response = sessionController.deleteSession(sessionId, accessToken);

    verify(sessionService, times(1)).deleteSession(sessionId, accessToken);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testDeleteSessionWithNullSessionId() {
    Long sessionId = null;
    String accessToken = "Bearer accessToken";

    ResponseEntity<Void> response = sessionController.deleteSession(sessionId, accessToken);

    verify(sessionService, times(1)).deleteSession(sessionId, accessToken);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  // ######################################### Get Session Credentials Tests #########################################

  @Test
  @WithMockUser
  public void testGetSessionCredentialsSuccessfully() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";

    Session session = new Session();
    session.setId(sessionId);
    when(sessionService.getSessionCredentials(sessionId, accessToken)).thenReturn(session);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/credentials/" + sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser
  public void testGetSessionCredentialsUnauthorized() throws Exception {
    String accessToken = "accessToken";
    Long sessionId = 1L;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/credentials/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Get Open Sessions Tests #########################################

  @Test
  @WithMockUser
  public void testPersonalSessionsSuccessfully() throws Exception {
    String accessToken = "Bearer accessToken";

    Session session = new Session();
    session.setId(1L);
    when(sessionService.getSessionsByUser(accessToken)).thenReturn(List.of(session));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/me")
                    .header("Authorization", accessToken)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser
  public void testGetPersonalSessionsWithNoSessions() throws Exception {
    String accessToken = "Bearer accessToken";

    when(sessionService.getSessionsByUser(accessToken)).thenReturn(new ArrayList<>());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/me")
                    .header("Authorization", accessToken)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testGetPersonalSessionsWithUnauthorizedUser() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session/me")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

}

