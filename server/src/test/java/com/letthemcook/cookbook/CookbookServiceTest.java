package com.letthemcook.cookbook;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookbookServiceTest {
  @Mock
  private CookbookRepository cookbookRepository;
  @Mock
  private SequenceGeneratorService sequenceGeneratorService;
  @Mock
  private JwtService jwtService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RecipeRepository recipeRepository;
  @InjectMocks
  private CookbookService cookbookService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    cookbookService = new CookbookService(cookbookRepository, sequenceGeneratorService, jwtService, userRepository, recipeRepository);
  }

  @AfterEach
  public void tearDown() {
    cookbookRepository.deleteAll();
    recipeRepository.deleteAll();
  }

  // ######################################### Add Recipe to Cookbook With Recipe ID Tests #########################################

  @Test
  public void testAddRecipeToCookbookRecipeIdSuccess() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);

    // Verify empty cookbook
    assertEquals(0, cookbook.getRecipeIds().size());

    // Mock services
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(cookbookRepository.save(cookbook)).thenReturn(cookbook);

    // Test
    cookbookService.addRecipeToCookbook(1L, 1L);

    // Verify
    assertEquals(1, cookbook.getRecipeIds().size());
  }

  @Test
  public void testAddRecipeToCookbookRecipeIdConflict() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);
    cookbook.addRecipe(1L);

    // Verify cookbook with recipe
    assertEquals(1, cookbook.getRecipeIds().size());

    // Mock services
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);

    // Test
    try {
      cookbookService.addRecipeToCookbook(1L, 1L);
    } catch (ResponseStatusException e) {
      assertEquals("Recipe already in cookbook", e.getMessage());
      assertEquals(409, e.getStatus().value());
    }
  }

  // ######################################### Add Recipe to Cookbook With AccessToken Tests #########################################

  @Test
  public void testAddRecipeToCookbookAccessTokenSuccess() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);

    // Verify empty cookbook
    assertEquals(0, cookbook.getRecipeIds().size());

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("test");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setChecklist(checklist);
    recipe.setTitle("test");

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(cookbookRepository.save(cookbook)).thenReturn(cookbook);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    // Test
    cookbookService.addRecipeToCookbook(1L, "accessToken");

    // Verify
    assertEquals(1, cookbook.getRecipeIds().size());
  }

  @Test
  public void testAddRecipeToCookbookAccessTokenConflict() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);
    cookbook.addRecipe(1L);

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("test");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setChecklist(checklist);
    recipe.setTitle("test");

    // Verify cookbook with recipe
    assertEquals(1, cookbook.getRecipeIds().size());

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    // Test
    try {
      cookbookService.addRecipeToCookbook(1L, "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(409, e.getStatus().value());
    }
  }

  @Test
  public void testAddRecipeToCookbookAccessTokenNotFound() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);

    // Verify empty cookbook
    assertEquals(0, cookbook.getRecipeIds().size());

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(recipeRepository.getById(1L)).thenReturn(null);

    // Test
    try {
      cookbookService.addRecipeToCookbook(1L, "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(404, e.getStatus().value());
    }
  }

  // ######################################### Remove Recipe from Cookbook Tests #########################################

  @Test
  public void testRemoveRecipeFromCookbookSuccess() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);
    cookbook.addRecipe(1L);

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("test");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setChecklist(checklist);
    recipe.setTitle("test");

    // Verify cookbook with recipe
    assertEquals(1, cookbook.getRecipeIds().size());

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(cookbookRepository.save(cookbook)).thenReturn(cookbook);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    // Test
    cookbookService.removeRecipeFromCookbook(1L, "accessToken");

    // Verify
    assertEquals(0, cookbook.getRecipeIds().size());
  }

  @Test
  public void testRemoveRecipeFromCookbookNotFound() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Verify empty cookbook
    assertEquals(0, cookbook.getRecipeIds().size());

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);

    // Test
    try {
      cookbookService.removeRecipeFromCookbook(1L, "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(404, e.getStatus().value());
    }
  }

  @Test
  public void testRemoveRecipeFromCookbookNotInCookbook() {
    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("test");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setChecklist(checklist);
    recipe.setTitle("test");

    // Verify empty cookbook
    assertEquals(0, cookbook.getRecipeIds().size());

    // Mock services
    when(jwtService.extractUsername(Mockito.any())).thenReturn("testUser");
    when(userRepository.getByUsername(Mockito.any())).thenReturn(user);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);
    when(recipeRepository.getById(1L)).thenReturn(recipe);

    // Test
    try {
      cookbookService.removeRecipeFromCookbook(1L, "accessToken");
    } catch (ResponseStatusException e) {
      assertEquals(404, e.getStatus().value());
    }
  }
}
