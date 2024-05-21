package com.letthemcook.user;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.auth.token.Token;
import com.letthemcook.cookbook.Cookbook;
import com.letthemcook.rating.Rating;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.recipe.RecipeService;
import com.letthemcook.session.Session;
import com.letthemcook.session.SessionService;
import com.letthemcook.sessionrequest.SessionRequestService;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.cookbook.CookbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.util.ClassUtils.getMethod;

@Service
@Transactional
public class UserService {
  private final UserRepository userRepository;
  private final CookbookService cookbookService;
  private final RecipeService recipeService;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final SessionRequestService sessionRequestService;
  private final MongoTemplate mongoTemplate;
  private final SessionService sessionService;
  private final RecipeRepository recipeRepository;
  Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, CookbookService cookbookService, RecipeService recipeService, SequenceGeneratorService sequenceGeneratorService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, SessionRequestService sessionRequestService, MongoTemplate mongoTemplate, SessionService sessionService, RecipeRepository recipeRepository) {
    this.userRepository = userRepository;
    this.cookbookService = cookbookService;
    this.recipeService = recipeService;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.sessionRequestService = sessionRequestService;
    this.mongoTemplate = mongoTemplate;
    this.sessionService = sessionService;
    this.recipeRepository = recipeRepository;
  }

  public Token createUser(User newUser) {
    checkIfUserExists(newUser);

    // Set user data
    newUser.setId(sequenceGeneratorService.getSequenceNumber(User.SEQUENCE_NAME));
    newUser.setRole(UserRole.USER);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

    // Create cookbook
    Cookbook cookbook = cookbookService.createCookbook(newUser.getId());
    newUser.setCookbookId(cookbook.getId());

    // Create session request
    sessionRequestService.createSessionRequest(newUser.getId());

    // Create token
    Token token = new Token();

    token.setAccessToken(jwtService.generateAccessToken(newUser));
    token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), newUser));

    userRepository.save(newUser);
    return token;
  }
  
  public Token loginUser(User checkUser) {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(checkUser.getUsername(), checkUser.getPassword()));

      if (authentication.isAuthenticated()) {
        Token token = new Token();

        token.setAccessToken(jwtService.generateAccessToken(checkUser));
        token.setRefreshToken(jwtService.generateRefreshToken(new HashMap<>(), checkUser));
        return token;
      }
      else {
        throw new UsernameNotFoundException("invalid user request!");
      }
    } catch (BadCredentialsException e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  public User getUser(String accessToken) {
    String username = jwtService.extractUsername(accessToken);

    return userRepository.getByUsername(username);
    }


  public Token refreshAccessToken(String refreshTokenString) {
    try {
      User user = userRepository.getByUsername(jwtService.extractUsername(refreshTokenString));

      // Token valid and exists in DB
      if (jwtService.isTokenValid(refreshTokenString, user)) {
        // Generate new token
        String accessToken = jwtService.generateAccessToken(user);

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshTokenString);

        return token;
      } else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid refresh token!");
      }
    } catch (Exception e) {
      logger.info("Error generating refreshToken: " + e.getMessage());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid refresh token!");
    }
  }

  public void updateUser(User user, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    User userToUpdate = userRepository.getByUsername(username);

    if (!Objects.equals(username, user.getUsername()) || !(Objects.equals(userToUpdate.getEmail(), user.getEmail()))) {
      checkIfUserExists(user);

      String newUsername = user.getUsername();
      Long userId = userToUpdate.getId();
      recipeService.updateRecipeCreatorName(userId, newUsername);
      sessionService.updateSessionHostName(userId, newUsername);
    }

    User updatedUser = updateUserData(userToUpdate, user);

    // Update user
    userRepository.save(updatedUser);
  }

  public void deleteUser(String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    User user = userRepository.getByUsername(username);

    // Check if user exists
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    // Create Query for user Recipes
    Long userId = user.getId();
    Query recipeQuery = new Query();
    recipeQuery.addCriteria(Criteria.where("creatorId").is(userId));

    // Delete user Recipes
    List<Recipe> recipes = mongoTemplate.find(recipeQuery, Recipe.class);
    for (Recipe recipe : recipes) {
      recipeService.deleteRecipeByUser(recipe);
    }

    // Create Query for user Sessions
    Query sessionQuery = new Query();
    sessionQuery.addCriteria(Criteria.where("hostId").is(userId));

    // Delete user Sessions
    List<Session> sessions = mongoTemplate.find(sessionQuery, Session.class);
    for (Session session : sessions) {
      sessionService.deleteSessionByUser(session);
    }

    // Delete user Cookbook
    cookbookService.deleteCookbook(user.getId());

    // Delete all Session Requests
    sessionRequestService.deleteSessionRequest(user.getId());

    // Delete user
    userRepository.delete(user);
  }


  public List<User> getUsers(Integer limit, Integer offset, Map<String, String> allParams) {
    Query query = new Query();
    query.limit(limit);
    query.skip(offset);

    for (Map.Entry<String, String> param : allParams.entrySet()) {
      if (Stream.of(QueryParams.values()).anyMatch(e -> e.getValue().equals(param.getKey()))) {
        if (param.getKey().toUpperCase().contains("NAME")) {
          query.addCriteria(Criteria.where(param.getKey()).regex(".*" + param.getValue() + ".*", "i"));
        }
        else if (param.getKey().toUpperCase().contains("ID")) {
          query.addCriteria(Criteria.where(param.getKey()).is(Long.parseLong(param.getValue())));
        }
      }
    }

    // Sort alphabetically
    query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.asc("username")));

    return mongoTemplate.find(query, User.class);
  }

  public void postRating(Long id, Integer rating, String accessToken) {
    User userToRate = userRepository.getById(id);

    if (userToRate == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    // Check if rater and ratee are same user
    User rater = userRepository.getByUsername(jwtService.extractUsername(accessToken));

    if(userToRate.getId().equals(rater.getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot rate yourself");
    }

    Rating userRating = userToRate.getRating();
    userRating.addRating(rating, id);
    userToRate.setRating(userRating);

    userRepository.save(userToRate);
  }

  // ######################################### Util #########################################

  protected void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.getByEmail(userToBeCreated.getEmail());
    User userByUsername = userRepository.getByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "Creating user failed because %s already exists";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "email"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "username"));
    }
  }

  protected User updateUserData(User existingUser, User user) {

    Method[] getters = {
            getMethod(User.class, "getUsername"),
            getMethod(User.class, "getFirstname"),
            getMethod(User.class, "getLastname"),
            getMethod(User.class, "getEmail")
    };

    Method[] setters = {
            getMethod(User.class, "setUsername", String.class),
            getMethod(User.class, "setFirstname", String.class),
            getMethod(User.class, "setLastname", String.class),
            getMethod(User.class, "setEmail", String.class)
    };

    for (int i = 0; i < getters.length; i++) {
      try {
        Object value = getters[i].invoke(user);
        if (value != null) {
          setters[i].invoke(existingUser, value);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (user.getPassword() != null) {
      existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    return existingUser;
  }
}
