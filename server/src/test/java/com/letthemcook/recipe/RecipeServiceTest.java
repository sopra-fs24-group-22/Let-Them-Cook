package com.letthemcook.recipe;

import com.letthemcook.util.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private SequenceGeneratorService sequenceGeneratorService;
  @InjectMocks
  private RecipeService recipeService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    recipeService = new RecipeService(recipeRepository, sequenceGeneratorService);
  }

  @AfterEach
  public void tearDown() {
    recipeRepository.deleteAll();
  }

  // ######################################### Recipe Tests #########################################

  @Test
  public void testCreateRecipeSuccess() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);

    // Mock DBSequence
    when(sequenceGeneratorService.getSequenceNumber(Mockito.any())).thenReturn(recipe.getId());

    // Perform test
    Recipe result = recipeService.createRecipe(recipe);

    Mockito.verify(recipeRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(recipe.getCreatorId(), result.getCreatorId());
    assertEquals(recipe.getId(), result.getId());
    assertEquals(recipe.getTitle(), result.getTitle());
    assertEquals(recipe.getChecklist(), result.getChecklist());
  }
}
