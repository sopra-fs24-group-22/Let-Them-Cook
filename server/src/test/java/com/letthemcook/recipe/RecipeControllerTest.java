package com.letthemcook.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.auth.config.JwtService;
import com.letthemcook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);
    recipe.setCreatorId(1L);

    recipeRepository.save(recipe);
  }

  @AfterEach
  public void tearDown() {
    recipeRepository.deleteAll();
  }

  // ######################################### Recipe Tests #########################################

  @Test
  @WithMockUser
  public void testCreateRecipeSuccess() throws Exception {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipeRequest = new Recipe();
    recipeRequest.setTitle("Test Recipe 2");
    recipeRequest.setChecklist(checklist);
    recipeRequest.setCreatorId(1L);

    // Mock recipe service
    when(recipeService.createRecipe(recipeRequest)).thenReturn(recipeRequest);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(recipeRequest)))
            .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithAnonymousUser
  public void testCreateRecipeFailureUnauthorized() throws Exception {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipeRequest = new Recipe();
    recipeRequest.setTitle("Test Recipe 2");
    recipeRequest.setChecklist(checklist);
    recipeRequest.setCreatorId(1L);

    // Perform test
    mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
              .contentType(MediaType.APPLICATION_JSON)
              .content(new ObjectMapper().writeValueAsString(recipeRequest)))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
