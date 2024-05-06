package com.letthemcook.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.dto.RecipeGetDTO;
import com.letthemcook.recipe.dto.RecipePostDTO;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@WebAppConfiguration
@ContextConfiguration
public class RecipeControllerTest {
  @MockBean
  private RecipeService recipeService;
  @MockBean
  private RecipeRepository recipeRepository;
  @MockBean
  private UserController userController;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RecipeController recipeController;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setIngredients(ingredients);
    recipe.setCookingTimeMin(10);
    recipe.setCreatorId(1L);
    recipe.setCreatorName("Test User");

    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);
    recipeRepository.save(recipe);
  }

  @AfterEach
  public void tearDown() {
    recipeRepository.deleteAll();
  }

  // ######################################### Create Recipe Tests #########################################

  @Test
  @WithMockUser(username = "testUser", password = "testPassword")
  public void testCreateRecipeSuccess() throws Exception {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    RecipePostDTO recipeRequest = new RecipePostDTO();
    recipeRequest.setTitle("Test Recipe");
    recipeRequest.setChecklist(checklist);
    recipeRequest.setIngredients(ingredients);
    recipeRequest.setCreatorId(1L);

    // Mock recipe service
    Recipe recipe = recipeRepository.getById(1L);
    when(recipeService.createRecipe(Mockito.any(), Mockito.any())).thenReturn(recipe);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
              .header("Authorization", "Bearer testToken")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(recipeRequest)))
            .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void testCreateRecipeFailureUnauthorized() throws Exception {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");
    ArrayList<String> ingredients = new ArrayList<>();
    ingredients.add("Test Ingredient");

    RecipePostDTO recipeRequest = new RecipePostDTO();
    recipeRequest.setTitle("Test Recipe 2");
    recipeRequest.setChecklist(checklist);
    recipeRequest.setIngredients(ingredients);
    recipeRequest.setCreatorId(1L);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(recipeRequest)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Get Recipe Tests #########################################

  @Test
  @WithMockUser
  public void testGetRecipeSuccess() throws Exception {
    // Mock recipe service
    Recipe recipe = recipeRepository.getById(1L);
    when(recipeService.getRecipe(1L, "accessToken")).thenReturn(recipe);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/1")
              .header("Authorization", "Bearer testToken")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testGetRecipeFailureUnauthorized() throws Exception {
    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/1")
              .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  // ######################################### Get Recipes Tests #########################################

  @Test
  @WithMockUser
  public void testGetRecipesSuccess() throws Exception {
    // Mock recipe service
    List<Recipe> recipes = new ArrayList<>();
    Recipe recipe = recipeRepository.getById(1L);
    recipes.add(recipe);
    when(recipeService.getRecipes(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(recipes);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes")
              .header("Authorization", "Bearer testToken")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].creatorId").value(1L))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].creatorName").value("Test User"))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Recipe"))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].checklist[0]").value("Test Step"))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].ingredients[0]").value("Test Ingredient"))
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].cookingTimeMin").value(10));
  }

  // ######################################### Put Recipes Tests #########################################
  @Test
  @WithMockUser
  public void updateRecipeSuccess() throws Exception {
    RecipeGetDTO recipeGetDTO = new RecipeGetDTO();
    recipeGetDTO.setId(1L);
    recipeGetDTO.setTitle("Updated Recipe");
    Recipe recipe = new Recipe();

    doNothing().when(recipeService).updateRecipe(Mockito.any(), Mockito.any());

    mockMvc.perform(put("/api/recipe")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(recipeGetDTO)))
            .andExpect(status().isOk());
  }

  @Test
  public void updateRecipeFailureUnauthorized() throws Exception {
    RecipeGetDTO recipeGetDTO = new RecipeGetDTO();
    recipeGetDTO.setId(1L);
    recipeGetDTO.setTitle("Updated Recipe");

    mockMvc.perform(put("/api/recipe")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(recipeGetDTO)))
            .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  public void updateRecipeFailureNotFound() throws Exception {
    RecipeGetDTO recipeGetDTO = new RecipeGetDTO();
    recipeGetDTO.setId(999L);
    recipeGetDTO.setTitle("Updated Recipe");

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(recipeService).updateRecipe(any(Recipe.class), anyString());

    mockMvc.perform(put("/api/recipe")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(recipeGetDTO)))
            .andExpect(status().isNotFound());
  }
}
