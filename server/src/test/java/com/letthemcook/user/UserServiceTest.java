package com.letthemcook.user;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() {
    userService = new UserService(userRepository, new SequenceGeneratorService(), authenticationManager, passwordEncoder, jwtService);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

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
}


