package com.letthemcook.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtAuthFilter;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.rest.mapper.DTOUserMapper;
import com.letthemcook.user.dto.GetMeRequestDTO;
import com.letthemcook.user.dto.LoginRequestDTO;
import com.letthemcook.user.dto.LogoutRequestDTO;
import com.letthemcook.user.dto.RegisterRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@WebAppConfiguration
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private UserService userService;
  @MockBean
  private JwtAuthFilter jwtAuthFilter;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserController userController;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    // Setup test user
    User user = new User();
    user.setUsername("test@test.com");
    user.setPassword("password");
    user.setFirstname("Test");
    user.setLastname("User");
    user.setEmail("test@user.com");

    userRepository.save(user);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  // ######################################### Login Tests #########################################

  @Test
  public void testLoginSuccess() throws Exception {
    // Setup login request
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
    loginRequestDTO.setUsername("test@test.com");
    loginRequestDTO.setPassword("password");

    // Mock token
    Token token = new Token();
    token.setAccessToken("accessToken");
    token.setRefreshToken("refreshToken");

    // Mock userService
    when(userService.loginUser(any(User.class))).thenReturn(token);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(token.getAccessToken()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(token.getRefreshToken()));
  }

  @Test
  @WithMockUser
  public void testLoginFailure() throws Exception {
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
    loginRequestDTO.setUsername("test@test.com");
    loginRequestDTO.setPassword("wrongpassword");

    when(userService.loginUser(any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Refresh Token Tests #########################################

  @Test
  public void testReturnNewTokenWhenRefreshTokenIsValid() throws Exception {
    // Setup token
    Token token = new Token();
    token.setAccessToken("newAccessToken");
    token.setRefreshToken("validRefreshToken");

    // Mock userService
    when(userService.refreshAccessToken(anyString())).thenReturn(token);

    // Perform request
    mockMvc.perform(MockMvcRequestBuilders.get("/auth/refresh")
                    .cookie(new Cookie("refreshToken", "validRefreshToken")))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(token.getAccessToken()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(token.getRefreshToken()));
  }

  @Test
  public void testReturnErrorWhenRefreshTokenIsInvalid() throws Exception {
    // Mock userService
    when(userService.refreshAccessToken(anyString())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

    // Perform request
    mockMvc.perform(MockMvcRequestBuilders.get("/auth/refresh")
                    .cookie(new Cookie("refreshToken", "invalidRefreshToken")))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void testReturnErrorWhenRefreshTokenIsMissing() throws Exception {
    // Perform request
    mockMvc.perform(MockMvcRequestBuilders.get("/auth/refresh"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  // ######################################### Register Route #########################################
  @Test
  public void registerUser_validInput() throws Exception {
    // Setup registration request
    RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
    registerRequestDTO.setUsername("test@other.com");
    registerRequestDTO.setPassword("password");
    registerRequestDTO.setFirstname("Test");
    registerRequestDTO.setLastname("User");
    registerRequestDTO.setEmail("test@other.com");

    // Mock token
    Token token = new Token();
    token.setAccessToken("accessToken");
    token.setRefreshToken("refreshToken");

    // Mock userService
    when(userService.createUser(any(User.class))).thenReturn(token);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(token.getAccessToken()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(token.getRefreshToken()));
  }

  @Test
  public void registerUser_takenEmail() throws Exception {
    // Setup registration request
    RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
    registerRequestDTO.setUsername("test@other.com");
    registerRequestDTO.setPassword("password");
    registerRequestDTO.setFirstname("Test");
    registerRequestDTO.setLastname("User");
    registerRequestDTO.setEmail("test@user.com");

    // Mock userService
    when(userService.createUser(any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  public void registerUser_takenUsername() throws Exception {
    // Setup registration request
    RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
    registerRequestDTO.setUsername("test@test.com");
    registerRequestDTO.setPassword("password");
    registerRequestDTO.setFirstname("Test");
    registerRequestDTO.setLastname("User");
    registerRequestDTO.setEmail("test@other.com");

    // Mock userService
    when(userService.createUser(any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  // ######################################### Logout Route #########################################

  @Test
  public void logoutUser() throws Exception {
    // Setup registration request
    LogoutRequestDTO logoutRequestDTO = new LogoutRequestDTO();
    logoutRequestDTO.setUsername("test@other.com");
    logoutRequestDTO.setEmail("test@other.com");

    // Mock token
    Token token = new Token();
    token.setAccessToken("accessToken");
    token.setRefreshToken("refreshToken");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(logoutRequestDTO)))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andExpect(MockMvcResultMatchers.jsonPath("$.Cookies").doesNotExist());
  }

  @Test
  public void getUser_returnsUserDetails_whenAccessTokenIsValid() throws Exception {
    // Given
    String validAccessToken = "validAccessToken";
    User user = new User();
    user.setUsername("test@test.com");
    user.setPassword("password");
    user.setFirstname("Test");
    user.setLastname("User");
    user.setEmail("test@user.com");

    GetMeRequestDTO expectedResponse = DTOUserMapper.INSTANCE.convertEntityToGetMeResponseDTO(user);

    when(userService.getUser(validAccessToken)).thenReturn(user);

    // When & Then
    mockMvc.perform(MockMvcRequestBuilders.get("/user/me")
                    .cookie(new Cookie("accessToken", validAccessToken)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value(user.getFirstname()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value(user.getLastname()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
  }

  @Test
  public void getUser_returnsUnauthorized_whenAccessTokenIsInvalid() throws Exception {
    // Given
    String invalidAccessToken = "invalidAccessToken";

    when(userService.getUser(invalidAccessToken)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token"));

    // When & Then
    mockMvc.perform(MockMvcRequestBuilders.get("/user/me")
                    .cookie(new Cookie("accessToken", invalidAccessToken)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void getUser_returnsBadRequest_whenAccessTokenIsMissing() throws Exception {
    // When & Then
    mockMvc.perform(MockMvcRequestBuilders.get("/user/me"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

}