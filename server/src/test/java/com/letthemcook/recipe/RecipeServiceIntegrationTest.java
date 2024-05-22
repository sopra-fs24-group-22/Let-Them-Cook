package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.rating.Rating;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:test_application.properties")
public class RecipeServiceIntegrationTest {
  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private RecipeService recipeService;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserRepository userRepository;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    recipeRepository.deleteAll();
    userRepository.deleteAll();

    // Setup user
    User user = new User();
    user.setRating(new Rating());
    user.setId(1L);
    user.setUsername("Test User");
    user.setEmail("testUser");
    user.setPassword("testPassword");
    user.setRole(UserRole.valueOf("USER"));
    user.setFirstname("Test");
    user.setLastname("User");
    user.setCookbookId(1L);
    userRepository.save(user);

    // Setup recipes
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setCreatorId(2L);
    recipe.setCreatorName("Test User");
    recipe.setChecklist(new ArrayList<>());
    recipe.setIngredients(new ArrayList<>());
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(0);
    recipeRepository.save(recipe);

    recipe = new Recipe();
    recipe.setId(2L);
    recipe.setTitle("Test Recipe");
    recipe.setCreatorId(2L);
    recipe.setCreatorName("Test User");
    recipe.setChecklist(new ArrayList<>());
    recipe.setIngredients(new ArrayList<>());
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(1);
    recipeRepository.save(recipe);

    recipe = new Recipe();
    recipe.setId(3L);
    recipe.setTitle("Test Recipe");
    recipe.setCreatorId(1L);
    recipe.setCreatorName("Test User");
    recipe.setChecklist(new ArrayList<>());
    recipe.setIngredients(new ArrayList<>());
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(1);
    recipeRepository.save(recipe);
  }

  // ######################################### Get Recipes Tests #########################################

  @Test
  public void testGetRecipesOnlyPublic() {
    // Perform test
    assertEquals(2, recipeService.getRecipes(10, 0, new HashMap<>()).size());
  }

  // ######################################### Get Recipe Tests #########################################

  @Test
  public void testGetRecipePublicSuccess() {
    // Generate accessToken
    User user = userRepository.getById(1L);
    String accessToken = jwtService.generateAccessToken(user);

    // Perform test
    Recipe recipe = recipeRepository.getById(2L);
    Recipe resultRecipe = recipeService.getRecipe(2L, accessToken);

    assertEquals(recipe.getId(), resultRecipe.getId());
    assertEquals(recipe.getTitle(), resultRecipe.getTitle());
    assertEquals(recipe.getCreatorId(), resultRecipe.getCreatorId());
    assertEquals(recipe.getCreatorName(), resultRecipe.getCreatorName());
    assertEquals(recipe.getChecklist(), resultRecipe.getChecklist());
    assertEquals(recipe.getIngredients(), resultRecipe.getIngredients());
    assertEquals(recipe.getCookingTimeMin(), resultRecipe.getCookingTimeMin());
    assertEquals(recipe.getPrivacyStatus(), resultRecipe.getPrivacyStatus());
  }

  @Test
  public void testGetRecipePrivateFailure() {
    // Generate accessToken
    User user = userRepository.getById(1L);
    String accessToken = jwtService.generateAccessToken(user);

    // Perform test
    assertThrows(ResponseStatusException.class, () -> recipeService.getRecipe(1L, accessToken));
  }

  @Test
  public void testGetRecipePrivateOwnerSuccess() {
    // TODO: Implement this test
  }

  // ######################################### Rate Recipe Tests #########################################

  @Test
  public void testRateRecipeSuccess() {
    // Generate accessToken
    Long userId = 1L;
    User user = userRepository.getById(userId);
    String accessToken = jwtService.generateAccessToken(user);

    // Perform test
    Long recipeId = 2L;
    Integer rating = 5;
    recipeService.rateRecipe(2L, accessToken, rating);
    Recipe recipe = recipeRepository.getById(recipeId);

    assertEquals(1, recipe.getRating().getNrRatings());
    assertEquals(rating, recipe.getRating().getUserRatings().get(user.getId()));
    assertEquals(5, recipe.getRating().getAvgTotalRating());
  }

  @Test
  public void testRateRecipeTwiceReplaceOldRating() {
    // Generate accessToken
    User user = userRepository.getById(1L);
    String accessToken = jwtService.generateAccessToken(user);

    // Perform test
    Integer oldRating = 5;
    Integer newRating = 3;
    recipeService.rateRecipe(2L, accessToken, oldRating);
    recipeService.rateRecipe(2L, accessToken, newRating);
    Recipe recipe = recipeRepository.getById(2L);

    assertEquals(1, recipe.getRating().getNrRatings());
    assertEquals(newRating, recipe.getRating().getUserRatings().get(user.getId()));
    assertEquals(3, recipe.getRating().getAvgTotalRating());
  }

  @Test
  public void testRateOwnRecipeFailure() {
    // Generate accessToken
    User user = userRepository.getById(1L);
    String accessToken = jwtService.generateAccessToken(user);

    // Perform test
    assertThrows(ResponseStatusException.class, () -> recipeService.rateRecipe(3L, accessToken, 5));
  }
}