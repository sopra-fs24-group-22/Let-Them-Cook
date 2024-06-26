package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.cookbook.Cookbook;
import com.letthemcook.cookbook.CookbookRepository;
import com.letthemcook.cookbook.CookbookService;
import com.letthemcook.rating.Rating;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private SequenceGeneratorService sequenceGeneratorService;
  @Mock
  private JwtService jwtService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private CookbookService cookbookservice;
  @Mock
  private CookbookRepository cookbookRepository;
  @Mock
  private MongoTemplate mongoTemplate;
  @Captor
  private ArgumentCaptor<Recipe> recipeCaptor;
  @InjectMocks
  private RecipeService recipeService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    recipeService = new RecipeService(recipeRepository, sequenceGeneratorService, jwtService, userRepository, cookbookservice, cookbookRepository, mongoTemplate);
  }

  @AfterEach
  public void tearDown() {
    recipeRepository.deleteAll();
    cookbookRepository.deleteAll();
  }

  // ######################################### Create Recipe Tests #########################################

  @Test
  public void testCreateRecipeSuccess() {
    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);

    // Mock Services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(sequenceGeneratorService.getSequenceNumber(Mockito.any())).thenReturn(recipe.getId());

    // Perform test
    Recipe result = recipeService.createRecipe(recipe, "accessToken");

    Mockito.verify(recipeRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(recipe.getCreatorId(), result.getCreatorId());
    assertEquals(recipe.getId(), result.getId());
    assertEquals(recipe.getTitle(), result.getTitle());
    assertEquals(recipe.getChecklist(), result.getChecklist());
  }

  // ######################################### Get Recipe Tests #########################################

  @Test
  public void testGetRecipeSuccess() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);

    // Setup Mock user
    User user = new User();
    user.setId(1L);

    // Mock Services
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);

    // Perform test
    Recipe result = recipeService.getRecipe(recipe.getId(), "Bearer accessToken");

    assertEquals(recipe.getCreatorId(), result.getCreatorId());
    assertEquals(recipe.getId(), result.getId());
    assertEquals(recipe.getTitle(), result.getTitle());
    assertEquals(recipe.getChecklist(), result.getChecklist());
    assertEquals(recipe.getIngredients(), result.getIngredients());
  }

  @Test
  public void testGetRecipeFailureNotFound() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);

    // Mock DBSequence
    when(recipeRepository.getById(recipe.getId())).thenReturn(null);
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(new User());

    // Perform test
    try {
      recipeService.getRecipe(recipe.getId(), "Bearer accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
    }
  }

  // ######################################### Delete Recipe Tests #########################################

  @Test
  public void testDeleteRecipeSuccess() {
    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);

    // Setup cookbooks
    ArrayList<Cookbook> cookbooks = new ArrayList<>();
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);
    cookbook.addRecipe(recipe.getId());
    cookbooks.add(cookbook);
    cookbookRepository.save(cookbook);

    cookbook.setId(2L);
    cookbook.setOwnerId(2L);
    cookbooks.add(cookbook);
    cookbookRepository.save(cookbook);

    // Mock Services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    when(cookbookRepository.findCookbookByRecipeIdsContaining(recipe.getId())).thenReturn(cookbooks);

    // Perform test
    recipeService.deleteRecipe(recipe.getId(), "accessToken");

    Mockito.verify(recipeRepository, Mockito.times(1)).deleteById(recipe.getId());
    Mockito.verify(cookbookRepository, Mockito.times(4)).save(Mockito.any());
  }

  @Test
  public void testDeleteRecipeFailureNotFound() {
    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);

    // Mock Services
    when(recipeRepository.getById(recipe.getId())).thenReturn(null);

    // Perform test
    try {
      recipeService.deleteRecipe(recipe.getId(), "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
    }
  }

  @Test
  public void testDeleteRecipeFailureForbidden() {
    // Setup test users
    User user_1 = new User();
    user_1.setId(1L);
    user_1.setUsername("testUser");

    User user_2 = new User();
    user_2.setId(2L);
    user_2.setUsername("testUser2");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);

    // Mock Services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user_2);
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);

    // Perform test
    try {
      recipeService.deleteRecipe(recipe.getId(), "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(e.getStatus(), HttpStatus.FORBIDDEN);
    }
  }

  // ######################################### Get Recipes Tests #########################################

  @Test
  public void testGetRecipesNoParamsSuccess() {
    // Setup test recipes
    ArrayList<Recipe> recipes = new ArrayList<>();
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setCreatorName("testUser");
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(1);
    recipes.add(recipe);

    recipe = new Recipe();
    recipe.setId(2L);
    recipe.setCreatorId(2L);
    recipe.setCreatorName("User2");
    recipe.setTitle("Practice Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);
    recipe.setCookingTimeMin(20);
    recipe.setPrivacyStatus(1);
    recipes.add(recipe);

    // Setup params
    HashMap<String, String> params = new HashMap<>();
    params.put("limit", "10");
    params.put("offset", "0");

    when(mongoTemplate.find(Mockito.any(Query.class), Mockito.eq(Recipe.class))).thenReturn(recipes);

    // Perform test
    assertEquals(recipes, recipeService.getRecipes(10, 0, params));
  }

  // ######################################### Update Recipe Tests #########################################

  @Test
  public void updateRecipeSuccessfullyUpdatesRecipe() {
    String accessToken = "Bearer accessToken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Updated Recipe");
    User user = new User();
    user.setUsername("testUser");
    user.setId(1L);

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    when(userRepository.getByUsername("testUser")).thenReturn(user);

    recipeService.updateRecipe(recipe, accessToken);

    verify(recipeRepository, times(1)).save(recipe);
  }

  @Test
  public void updateRecipeThrowsNotFoundWhenRecipeDoesNotExist() {
    String accessToken = "accessToken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Updated Recipe");

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(recipeRepository.getById(recipe.getId())).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> recipeService.updateRecipe(recipe, accessToken));
  }

  @Test
  public void updateRecipeThrowsForbiddenWhenUserIsNotAuthorized() {
    String accessToken = "accessToken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Updated Recipe");
    User user = new User();
    user.setUsername("anotherUser");
    user.setId(2L);

    when(jwtService.extractUsername(accessToken)).thenReturn("testUser");
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    when(userRepository.getByUsername("testUser")).thenReturn(user);

    assertThrows(ResponseStatusException.class, () -> recipeService.updateRecipe(recipe, accessToken));
  }

  // ######################################### Rate Recipe Tests #########################################

  @Test
  public void testRateRecipeSuccess() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Rating rating = new Rating();

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(2L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setRating(rating);

    // Setup User
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Mock Services
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);

    // Perform test
    recipeService.rateRecipe(recipe.getId(), "Bearer accessToken", 5);
    assertEquals(5, recipeRepository.getById(recipe.getId()).getRating().getAvgTotalRating());
    assertEquals(1, recipeRepository.getById(recipe.getId()).getRating().getNrRatings());
  }

  // ######################################### Util Tests #########################################

  @Test
  public void deleteRecipeByUserSuccessfullyDeletesRecipe() {
    Recipe recipe = new Recipe();

    recipeService.deleteRecipeByUser(recipe);

    verify(recipeRepository, times(1)).delete(recipe);
  }

  @Test
  public void updateRecipeCreatorNameUpdatesNameForAllRecipesOfUser() {
    Long userId = 1L;
    String newUsername = "newUser";
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setCreatorId(userId);
    recipe1.setCreatorName("oldUser");

    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setCreatorId(userId);
    recipe2.setCreatorName("oldUser");

    List<Recipe> recipes = Arrays.asList(recipe1, recipe2);

    when(recipeRepository.getByCreatorId(userId)).thenReturn(recipes);

    recipeService.updateRecipeCreatorName(userId, newUsername);

    verify(recipeRepository, times(2)).save(recipeCaptor.capture());
    List<Recipe> updatedRecipes = recipeCaptor.getAllValues();

    for (Recipe updatedRecipe : updatedRecipes) {
      assertEquals(newUsername, updatedRecipe.getCreatorName());
    }
  }

  @Test
  public void updateRecipeCreatorNameDoesNothingForUserWithNoRecipes() {
    Long userId = 1L;
    String newUsername = "newUser";

    when(recipeRepository.getByCreatorId(userId)).thenReturn(new ArrayList<>());

    recipeService.updateRecipeCreatorName(userId, newUsername);

    verify(recipeRepository, times(0)).save(any(Recipe.class));
  }
}
