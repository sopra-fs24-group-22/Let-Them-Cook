package com.letthemcook.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtAuthFilter;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
@WebAppConfiguration
public class UserControllerTest {

  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private UserService userService;
  @MockBean
  private JwtAuthFilter jwtAuthFilter;
  @MockBean
  private JwtService jwtService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

    // Save test user
    User user = new User();
    user.setId(1L);
    user.setPassword("Test");
    user.setEmail("testUser@gmail.com");
    user.setUsername("TestUser");
    user.setFirstName("Max");
    user.setLastName("Mustermann");

    given(userRepository.getById(Mockito.any())).willReturn(user);
  }

  // ######################################### Login Route #########################################
/*  @Test
  @WithAnonymousUser
  public void loginUser_validInput() throws Exception {
    // Setup environment
    User user = userRepository.getById(1L);

    // Generate test token
    Token token = new Token();
    token.setAccessToken(jwtService.generateAccessToken(user));
    token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), user));

    given(userService.loginUser(Mockito.any())).willReturn(token);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("Test");
    userDTO.setEmail("testUser@gmail.com");

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(asJsonString(userDTO));
    // then
    mockMvc.perform(postRequest)
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.accessToken").value(token.getAccessToken()))
            .andExpect(jsonPath("$.refreshToken").value(token.getRefreshToken()));
  }*/

  @Test
  @WithAnonymousUser
  public void loginUser_invalidEmail() throws Exception {
    // Setup environment
    User user = userRepository.getById(1L);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("Test");
    userDTO.setEmail("wrongUser@gmail.com");

    given(userService.loginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(asJsonString(userDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().is(401));
  }

  @Test
  @WithAnonymousUser
  public void loginUser_invalidPassword() throws Exception {
    // Setup environment
    User user = userRepository.getById(1L);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("WrongPassword");
    userDTO.setEmail("testUser@gmail.com");

    given(userService.loginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(asJsonString(userDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().is(401));
  }

  // ######################################### Util #########################################

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   *
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              String.format("The request body could not be created.%s", e.toString()));
    }
  }
}