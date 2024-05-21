package com.letthemcook.sessionrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.rest.mapper.DTORequestSessionMapper;
import com.letthemcook.rest.mapper.DTOSingleSessionRequestMapper;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionRepository;
import com.letthemcook.sessionrequest.dto.SessionRequestDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestGetSingleDTO;
import com.letthemcook.sessionrequest.dto.SessionRequestsGetDTO;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionRequestController.class)
@WebAppConfiguration
@ContextConfiguration
public class SessionRequestControllerTest {
  @MockBean
  private SessionRequestService sessionRequestService;
  @MockBean
  private SessionRequestRepository sessionRequestRepository;
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
  private SessionRequestController sessionRequestController;

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

  // ######################################### Create Session Request Test #########################################

  @Test
  @WithMockUser
  public void createSessionRequestReturnsCreatedStatus() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";

    doNothing().when(sessionRequestService).sendSessionRequest(sessionId, accessToken);

    mockMvc.perform(post("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isCreated());

    verify(sessionRequestService, times(1)).sendSessionRequest(sessionId, accessToken);
  }

  @Test
  @WithMockUser
  public void createSessionRequestWithInvalidSessionIdReturnsNotFoundStatus() throws Exception {
    Long sessionId = 999L; // non-existing session id
    String accessToken = "Bearer accessToken";

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(sessionRequestService).sendSessionRequest(sessionId, accessToken);

    mockMvc.perform(post("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isNotFound());
  }

  @Test
  public void createSessionRequestWithoutAuthorizationReturnsUnauthorizedStatus() throws Exception {
    Long sessionId = 1L;

    mockMvc.perform(post("/api/session_request/{sessionId}", sessionId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(new SessionRequestDTO())))
            .andExpect(status().isUnauthorized());
  }

  // ######################################### Accept Session Request Test #########################################

  @Test
  @WithMockUser
  public void acceptSessionRequestReturnsOkStatus() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    doNothing().when(sessionRequestService).processSessionRequest(anyLong(), any(SessionRequest.class), eq(true));

    mockMvc.perform(post("/api/session_request/{sessionId}/accept", sessionId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isOk());

    verify(sessionRequestService, times(1)).processSessionRequest(anyLong(), any(SessionRequest.class), eq(true));
  }

  @Test
  @WithMockUser
  public void acceptSessionRequestWithInvalidSessionIdReturnsNotFoundStatus() throws Exception {
    Long sessionId = 999L; // non-existing session id
    String accessToken = "Bearer accessToken";
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(sessionRequestService).processSessionRequest(anyLong(), any(SessionRequest.class), eq(true));

    mockMvc.perform(post("/api/session_request/{sessionId}/accept", sessionId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isNotFound());
  }

  @Test
  public void acceptSessionRequestWithoutAuthorizationReturnsUnauthorizedStatus() throws Exception {
    Long sessionId = 1L;
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    mockMvc.perform(post("/api/session_request/{sessionId}/accept", sessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isUnauthorized());
  }

  // ######################################### Deny Session Request Test #########################################

  @Test
  @WithMockUser
  public void denySessionRequestReturnsOkStatus() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    doNothing().when(sessionRequestService).processSessionRequest(anyLong(), any(SessionRequest.class), eq(false));

    mockMvc.perform(post("/api/session_request/{sessionId}/deny", sessionId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isOk());

    verify(sessionRequestService, times(1)).processSessionRequest(anyLong(), any(SessionRequest.class), eq(false));
  }

  @Test
  @WithMockUser
  public void denySessionRequestWithInvalidSessionIdReturnsNotFoundStatus() throws Exception {
    Long sessionId = 999L; // non-existing session id
    String accessToken = "Bearer accessToken";
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(sessionRequestService).processSessionRequest(anyLong(), any(SessionRequest.class), eq(false));

    mockMvc.perform(post("/api/session_request/{sessionId}/deny", sessionId)
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isNotFound());
  }

  @Test
  public void denySessionRequestWithoutAuthorizationReturnsUnauthorizedStatus() throws Exception {
    Long sessionId = 1L;
    SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();

    mockMvc.perform(post("/api/session_request/{sessionId}/deny", sessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(sessionRequestDTO))
                    .with(csrf()))
            .andExpect(status().isUnauthorized());
  }

  // ######################################### Get Session Requests Test #########################################

  @Test
  @WithMockUser
  public void getSessionRequestsReturnsOkStatus() throws Exception {
    String accessToken = "Bearer accessToken";
    SessionRequest sessionRequest = new SessionRequest();
    SessionRequestsGetDTO sessionRequestsGetDTO = DTORequestSessionMapper.INSTANCE.convertEntityToGetSessionRequestsDTO(sessionRequest);

    when(sessionRequestService.getSessionRequests(accessToken)).thenReturn(sessionRequest);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session_request")
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(sessionRequestsGetDTO)));

    verify(sessionRequestService, times(1)).getSessionRequests(accessToken);
  }

  @Test
  public void getSessionRequestsWithoutAuthorizationReturnsUnauthorizedStatus() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/session_request")
                    .with(csrf()))
            .andExpect(status().isUnauthorized());
  }

  // ######################################### Get Single Session Requests Test #########################################

  @Test
  @WithMockUser
  public void getSingleSessionRequestsReturnsOkStatus() throws Exception {
    Long sessionId = 1L;
    String accessToken = "Bearer accessToken";
    ArrayList<SingleSessionRequests> singleSessionRequests = new ArrayList<>();
    ArrayList<SessionRequestGetSingleDTO> sessionRequestGetSingleDTOs = new ArrayList<>();

    when(sessionRequestService.getSingleSessionRequest(sessionId, accessToken)).thenReturn(singleSessionRequests);
    for (SingleSessionRequests singleSessionRequest : singleSessionRequests) {
      SessionRequestGetSingleDTO sessionRequestGetSingleDTO = DTOSingleSessionRequestMapper.INSTANCE.convertEntityToGetSingleSessionRequestsDTO(singleSessionRequest);
      sessionRequestGetSingleDTOs.add(sessionRequestGetSingleDTO);
    }

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(sessionRequestGetSingleDTOs)));

    verify(sessionRequestService, times(1)).getSingleSessionRequest(sessionId, accessToken);
  }

  @Test
  public void getSingleSessionRequestsWithoutAuthorizationReturnsUnauthorizedStatus() throws Exception {
    Long sessionId = 1L;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session_request/{sessionId}", sessionId)
                    .with(csrf()))
            .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  public void getSingleSessionRequestsWithInvalidSessionIdReturnsNotFoundStatus() throws Exception {
    Long sessionId = 999L; // non-existing session id
    String accessToken = "Bearer accessToken";

    when(sessionRequestService.getSingleSessionRequest(sessionId, accessToken)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/session_request/{sessionId}", sessionId)
                    .header("Authorization", accessToken)
                    .with(csrf()))
            .andExpect(status().isNotFound());
  }

}
