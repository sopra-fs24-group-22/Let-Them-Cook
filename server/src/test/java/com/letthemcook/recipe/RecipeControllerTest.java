package com.letthemcook.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.dto.RecipePostDTO;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setCreatorId(1L);

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

    RecipePostDTO recipeRequest = new RecipePostDTO();
    recipeRequest.setTitle("Test Recipe");
    recipeRequest.setChecklist(checklist);
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

    RecipePostDTO recipeRequest = new RecipePostDTO();
    recipeRequest.setTitle("Test Recipe 2");
    recipeRequest.setChecklist(checklist);
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
    when(recipeService.getRecipe(1L)).thenReturn(recipe);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/1")
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
}
