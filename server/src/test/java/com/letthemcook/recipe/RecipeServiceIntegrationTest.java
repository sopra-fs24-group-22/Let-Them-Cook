package com.letthemcook.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:test_application.properties")
@Disabled
public class RecipeServiceIntegrationTest {
  //TODO: Integration tests
  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private RecipeService recipeService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setUp() {
    recipeRepository.deleteAll();
  }

  // ######################################### Get Recipes Tests #########################################

  @Test
  public void testGetRecipesOnlyPublic() {
    // Setup recipes
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setCreatorId(1L);
    recipe.setCreatorName("Test User");
    recipe.setChecklist(new ArrayList<>());
    recipe.setIngredients(new ArrayList<>());
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(0);
    recipeRepository.save(recipe);

    recipe = new Recipe();
    recipe.setId(2L);
    recipe.setTitle("Test Recipe");
    recipe.setCreatorId(1L);
    recipe.setCreatorName("Test User");
    recipe.setChecklist(new ArrayList<>());
    recipe.setIngredients(new ArrayList<>());
    recipe.setCookingTimeMin(10);
    recipe.setPrivacyStatus(1);
    recipeRepository.save(recipe);

    // Perform test
    assertEquals(1, recipeService.getRecipes(10, 0, new HashMap<>()).size());
  }
}
