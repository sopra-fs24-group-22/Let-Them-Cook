package com.letthemcook.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.letthemcook.auth.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
  private JwtService jwtService;
  private UserDetails userDetails;

  @BeforeEach
  public void setup() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "SECRET", "6aiJsudhas8cu3fYlk8tGI5Ao39SMGhsr2wwNTNu49HTn6Q6Z6w");
    ReflectionTestUtils.setField(jwtService, "ACCESS_TOKEN_EXPIRATION_MS", 1000);
    ReflectionTestUtils.setField(jwtService, "REFRESH_TOKEN_EXPIRATION_MS", 2000);
    userDetails = new User("testUser", "testPassword", Collections.emptyList());
  }

  @Test
  public void testExtractUsernameFromToken() {
    String token = jwtService.generateAccessToken(userDetails);
    assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
  }

  @Test
  public void testValidateToken() {
    String token = jwtService.generateAccessToken(userDetails);
    assertTrue(jwtService.isTokenValid(token, userDetails));
  }

  @Test
  public void testTokenExpires() throws InterruptedException {
    String token = jwtService.generateAccessToken(userDetails);
    assertTrue(jwtService.isTokenValid(token, userDetails));
    Thread.sleep(5000); // wait for token to expire
    assertFalse(jwtService.isTokenValid(token, userDetails));
  }

  @Test
  public void testGenerateDifferentAccessAndRefreshTokens() {
    String accessToken = jwtService.generateAccessToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(Collections.emptyMap(), userDetails);
    assertNotEquals(accessToken, refreshToken);
  }
}