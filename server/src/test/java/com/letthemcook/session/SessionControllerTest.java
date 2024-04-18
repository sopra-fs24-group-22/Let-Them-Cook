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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

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

      Session session = new Session();
      session.setId(1L);
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(2);
      session.setHost(1L);
      session.setRecipe(4L);
      session.setParticipants(participantList);
      session.setDate("2020-01-01");

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
      SessionPostDTO sessionRequest = new SessionPostDTO();
      sessionRequest.setSessionName("Test Session");
      sessionRequest.setMaxParticipantCount(3);
      sessionRequest.setRecipe(2L);
      sessionRequest.setDate("2020-01-01");

      // Mock recipe service
      Session session = sessionRepository.getById(1L);
      when(sessionService.createSession(Mockito.any(), Mockito.any())).thenReturn(session);

      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                      .header("Authorization", "Bearer testToken")
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(new ObjectMapper().writeValueAsString(sessionRequest)))
              .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testCreateSessionFailureUnauthorized() throws Exception {
      // Setup test recipe
      // Setup test session
      SessionPostDTO sessionRequest = new SessionPostDTO();
      sessionRequest.setSessionName("Test Session");
      sessionRequest.setMaxParticipantCount(3);
      sessionRequest.setRecipe(2L);
      sessionRequest.setDate("2020-01-01");

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
    public void testGetRecipeSuccess() throws Exception {
      // Mock recipe service
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
    public void testGetRecipeFailureUnauthorized() throws Exception {
      // Perform test
      mockMvc.perform(MockMvcRequestBuilders.get("/api/session/1")
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
