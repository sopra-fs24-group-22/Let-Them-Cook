package com.letthemcook.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.dto.SessionPostDTO;
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

import java.util.ArrayList;
import java.util.Date;

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
      Date date = new Date();

      Session session = new Session();
      session.setId(1L);
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(2);
      session.setHostId(1L);
      session.setRecipeId(4L);
      session.setParticipants(participantList);
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
}
