package com.letthemcook.user;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.cookbook.CookbookService;
import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private CookbookService cookbookService;

  @InjectMocks
  private UserService userService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    userService = new UserService(userRepository, cookbookService, new SequenceGeneratorService(), authenticationManager, passwordEncoder, jwtService);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  // ######################################### Login Tests #########################################

  @Test
  public void testLoginSuccess() {
    // Setup test user
    User user = new User();
    user.setUsername("testUser");
    user.setPassword("testPassword");

    userRepository.save(user);

    // Mock authentication
    Authentication authentication = mock(Authentication.class);
    authentication.setAuthenticated(true);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

    // Mock token creation
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    when(jwtService.generateAccessToken(any())).thenReturn(accessToken);
    when(jwtService.generateRefreshToken(any(), any())).thenReturn(refreshToken);

    // Perform test
    Token result = userService.loginUser(user);

    assertEquals(accessToken, result.getAccessToken());
    assertEquals(refreshToken, result.getRefreshToken());
  }

  @Test
  public void testLoginFailureInvalidUsername() {
    // Setup test user
    User user = new User();
    user.setUsername("testUser");
    user.setPassword("testPassword");

    userRepository.save(user);

    user.setUsername("wrongUsername");

    // Mock authentication
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

    // Perform test
    Assertions.assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void testLoginFailureInvalidPassword() {
    // Setup test user
    User user = new User();
    user.setUsername("testUser");
    user.setPassword("testPassword");

    userRepository.save(user);

    user.setPassword("wrongPassword");

    // Mock authentication
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

    // Perform test
    Assertions.assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  // ######################################### Refresh Token Tests #########################################

  @Test
  public void shouldReturnNewTokenWhenRefreshAccessTokenIsValid() {
    User user = new User();
    user.setUsername("testUser");

    when(userRepository.getByUsername(anyString())).thenReturn(user);
    when(jwtService.extractUsername(anyString())).thenReturn("testUser");
    when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(true);
    when(jwtService.generateAccessToken(any(User.class))).thenReturn("newAccessToken");

    Token result = userService.refreshAccessToken("validRefreshToken");

    assertEquals("newAccessToken", result.getAccessToken());
    assertEquals("validRefreshToken", result.getRefreshToken());
  }

  @Test
  public void shouldThrowExceptionWhenRefreshAccessTokenIsInvalid() {
    when(userRepository.getByUsername(anyString())).thenReturn(null);
    when(jwtService.extractUsername(anyString())).thenReturn(null);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.refreshAccessToken("invalidRefreshToken"));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    assertEquals("invalid refresh token!", exception.getReason());
  }

  @Test
  public void shouldThrowExceptionWhenRefreshAccessTokenIsNotValidForUser() {
    User user = new User();
    user.setUsername("testUser");

    when(userRepository.getByUsername(anyString())).thenReturn(user);
    when(jwtService.extractUsername(anyString())).thenReturn("testUser");
    when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.refreshAccessToken("invalidRefreshTokenForUser"));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    assertEquals("invalid refresh token!", exception.getReason());
  }
}


