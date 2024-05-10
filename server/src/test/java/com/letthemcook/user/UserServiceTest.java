package com.letthemcook.user;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.cookbook.CookbookService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionService;
import com.letthemcook.sessionrequest.SessionRequestService;
import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private SequenceGeneratorService sequenceGeneratorService;

  @Mock
  private RecipeService recipeService;

  @Mock
  private SessionService sessionService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private CookbookService cookbookService;

  @Mock
  private SessionRequestService sessionRequestService;

  @Mock
  private MongoTemplate mongoTemplate;

  @InjectMocks
  private UserService userService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    userService = new UserService(userRepository, cookbookService, recipeService, sequenceGeneratorService, authenticationManager, passwordEncoder, jwtService, sessionRequestService, mongoTemplate, sessionService);
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

  // ######################################### Get Users Tests #########################################

  @Test
  public void testGetUsersNoParamsReturnsAllUsers() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();

    List<User> users = new ArrayList<>();

    User user = new User();
    user.setUsername("testUser");
    user.setId(1L);
    users.add(user);

    user = new User();
    user.setUsername("User2");
    user.setId(2L);
    users.add(user);

    // Mock Services
    when(mongoTemplate.find(any(), eq(User.class))).thenReturn(users);

    // Perform test
    List<User> result = userService.getUsers(limit, offset, allParams);

    assertEquals(users, result);
  }

  @Test
  public void testGetUsersByUsername() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> allParams = new HashMap<>();
    allParams.put("username", "testUser");

    List<User> users = new ArrayList<>();

    User user = new User();
    user.setUsername("testUser");
    user.setId(1L);
    users.add(user);

    // Mock Services
    when(mongoTemplate.find(any(), eq(User.class))).thenReturn(users);

    // Perform test
    List<User> result = userService.getUsers(limit, offset, allParams);

    assertEquals(users, result);
  }

  // ######################################### Delete User Tests #########################################

  @Test
  public void deleteUserSuccessfullyDeletesUser() throws Exception {
    String accessToken = "accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(userRepository.getByUsername("testUser")).thenReturn(user);

    userService.deleteUser(accessToken);

    verify(userRepository, times(1)).delete(user);
  }

  @Test
  public void deleteUserThrowsNotFoundWhenUserDoesNotExist() throws Exception {
    String accessToken = "accessToken";

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(userRepository.getByUsername("testUser")).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> userService.deleteUser(accessToken));
  }

  @Test
  public void deleteUserSuccessfullyDeletesUserRecipes() throws Exception {
    String accessToken = "Bearer accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    Recipe recipe1 = new Recipe();
    recipe1.setCreatorId(user.getId());
    Recipe recipe2 = new Recipe();
    recipe2.setCreatorId(user.getId());
    List<Recipe> recipes = new ArrayList<>();
    recipes.add(recipe1);
    recipes.add(recipe2);

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(userRepository.getByUsername("testUser")).thenReturn(user);
    when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipes);

    userService.deleteUser(accessToken);

    verify(recipeService, times(2)).deleteRecipeByUser(any(Recipe.class));
  }

  @Test
  public void deleteUserSuccessfullyDeletesUserSessions() throws Exception {
    String accessToken = "Bearer accessToken";
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    Recipe recipe = new Recipe();
    recipe.setCreatorId(user.getId());
    List<Recipe> recipes = new ArrayList<>();
    recipes.add(recipe);

    Session session1 = new Session();
    Session session2 = new Session();
    session1.setHostId(user.getId());
    session2.setHostId(user.getId());
    List<Session> sessions = new ArrayList<>();
    sessions.add(session1);
    sessions.add(session2);

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(userRepository.getByUsername("testUser")).thenReturn(user);
    when(mongoTemplate.find(any(Query.class), eq(Session.class))).thenReturn(sessions);
    when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipes);


    userService.deleteUser(accessToken);

    verify(sessionService, times(2)).deleteSessionByUser(any(Session.class));
  }
}


