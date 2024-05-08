package com.letthemcook.sessionrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionController;
import com.letthemcook.session.SessionRepository;
import com.letthemcook.session.SessionService;
import com.letthemcook.user.User;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionRequestController.class)
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
public class SessionRequestControllerTest {
  @MockBean
  private SessionRequestService sessionRequestService;
  @MockBean
  private SessionRequestRepository sessionRequestRepository;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private UserDetailsService userDetailsService;
  @MockBean
  private SessionRepository sessionRepository;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SessionRequestController sessionRequestController;
  @Autowired
  private WebApplicationContext webApplicationContext;


  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {

    Long sessionId = 1L;
    Long userId = 1L;
    SessionRequest sessionRequest = new SessionRequest();
    sessionRequest.setUserSessions(new HashMap<>());
    sessionRequest.setUserId(userId);

    Session session = new Session();
    session.setId(2L);

    sessionRequestRepository.save(sessionRequest);
    sessionRepository.save(session);
  }

  @AfterEach
  public void teardown() {
    sessionRequestRepository.deleteAll();
  }

  // ######################################### Create Session Request #########################################

  @Test
  public void createSessionRequestSuccessfullyCreatesRequest() throws Exception {
    Long sessionId = 2L;
    String accessToken = "accessToken";

    doNothing().when(sessionRequestService).sendSessionRequest(sessionId, accessToken);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    verify(sessionRequestService, times(1)).sendSessionRequest(sessionId, accessToken);
  }

  @Test
  public void createSessionRequestThrowsExceptionWhenServiceFails() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "You have already sent a session request for this session"))
            .when(sessionRequestService).sendSessionRequest(sessionId, accessToken);

    mockMvc.perform(post("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isConflict());

    verify(sessionRequestService, times(1)).sendSessionRequest(sessionId, accessToken);
  }
}
