package com.letthemcook.cookbook;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CookbookController.class)
@WebAppConfiguration
@ContextConfiguration
public class CookbookControllerTest {
  @MockBean
  private CookbookService cookbookService;
  @MockBean
  private CookbookRepository cookbookRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CookbookController cookbookController;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);

    // Setup test cookbook
    Cookbook cookbook = new Cookbook(1L);
    cookbook.setId(1L);
    cookbook.setOwnerId(1L);
    cookbook.addRecipe(1L);
    when(cookbookRepository.getByOwnerId(1L)).thenReturn(cookbook);

    // Setup test user
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setPassword("password");
    user.setFirstname("Test");
    user.setLastname("User");
    user.setEmail("test@User.com");
    user.setCookbookId(1L);
    when(userRepository.getByUsername("testUser")).thenReturn(user);
  }

  @AfterEach
  public void tearDown() {
    cookbookRepository.deleteAll();
  }

  // ######################################### Add recipe to cookbook test #########################################

  @Test
  @WithMockUser
  public void testAddRecipeToCookbookSuccess() throws Exception {
    Cookbook cookbook = cookbookRepository.getByOwnerId(1L);
    when(cookbookService.addRecipeToCookbook(1L, "testToken")).thenReturn(cookbook);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/cookbook/recipe/1")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType("application/json"))
            .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser
  public void testAddRecipeToCookbookConflict() throws Exception {
    when(cookbookService.addRecipeToCookbook(anyLong(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/cookbook/recipe/1")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType("application/json"))
            .andExpect(status().isConflict());
  }

  @Test
  public void testAddRecipeToCookbookUnauthorized() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/cookbook/recipe/1")
                    .with(csrf())
                    .contentType("application/json"))
            .andExpect(status().isUnauthorized());
  }

  // ######################################### Remove recipe from cookbook test #########################################

  @Test
  @WithMockUser
  public void testRemoveRecipeFromCookbookSuccess() throws Exception {
    Cookbook cookbook = cookbookRepository.getByOwnerId(1L);
    when(cookbookService.removeRecipeFromCookbook(1L, "testToken")).thenReturn(cookbook);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/cookbook/recipe/1")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType("application/json"))
            .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  public void testRemoveRecipeFromCookbookNotFound() throws Exception {
    when(cookbookService.removeRecipeFromCookbook(anyLong(), anyString())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/cookbook/recipe/1")
                    .header("Authorization", "Bearer testToken")
                    .with(csrf())
                    .contentType("application/json"))
            .andExpect(status().isNotFound());
  }
}
