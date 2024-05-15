package com.letthemcook.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.dto.SessionPostDTO;
import com.letthemcook.user.User;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
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
      // Setup test recipe
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
      when(sessionService.createSession(Mockito.any(), Mockito.any())).thenReturn(session);

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
    when(sessionService.getSessionsByUser(Mockito.any())).thenReturn(sessions);

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
}
