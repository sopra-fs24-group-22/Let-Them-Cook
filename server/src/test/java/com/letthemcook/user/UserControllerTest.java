package com.letthemcook.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.UserAuthenticationProvider;
import com.letthemcook.user.UserController;
import com.letthemcook.user.UserService;
import com.letthemcook.user.User;
import com.letthemcook.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import org.hamcrest.core.IsNull;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private UserService userService;

  @BeforeEach
  public void setup() {
    // Save test User
    User user = new User();
    user.setId(1L);
    user.setPassword("Test");
    user.setEmail("testUser@gmail.com");
    user.setUsername("TestUser");
    user.setFirstName("Max");
    user.setLastName("Mustermann");

    given(userRepository.findById(1L)).willReturn(user);
  }

  // ######################################### Login Route #########################################
  @Test
  @WithMockUser
  public void loginUser_validInput() throws Exception {
    // Setup environment
    User user = userRepository.findById(1L);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("Test");
    userDTO.setEmail("testUser@gmail.com");

    given(userService.loginUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(asJsonString(userDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
            .andExpect(jsonPath("$.lastName", is(user.getLastName())))
            .andExpect(jsonPath("$.email", is(user.getEmail())));
  }

  @Test
  public void loginUser_invalidEmail() throws Exception {
    // Setup environment
    User user = userRepository.findById(1L);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("Test");
    userDTO.setEmail("wrongUser@gmail.com");

    given(userService.loginUser(Mockito.any())).willReturn(user);

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
  public void loginUser_invalidPassword() throws Exception {
    // Setup environment
    User user = userRepository.findById(1L);

    UserDTO userDTO = new UserDTO();
    userDTO.setPassword("WrongPassword");
    userDTO.setEmail("testUser@gmail.com");

    given(userService.loginUser(Mockito.any())).willReturn(user);
    given(userRepository.findByEmail(Mockito.any())).willReturn(user);

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