package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
  @InjectMocks
  private RecipeService recipeService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    recipeService = new RecipeService(recipeRepository, sequenceGeneratorService, jwtService, userRepository);
  }

  @AfterEach
  public void tearDown() {
    recipeRepository.deleteAll();
  }

  // ######################################### Create Recipe Tests #########################################

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

  // ######################################### Get Recipe Tests #########################################

  @Test
  public void testGetRecipeSuccess() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);

    // Mock DBSequence
    when(recipeRepository.getById(recipe.getId())).thenReturn(recipe);

    // Perform test
    Recipe result = recipeService.getRecipe(recipe.getId());

    assertEquals(recipe.getCreatorId(), result.getCreatorId());
    assertEquals(recipe.getId(), result.getId());
    assertEquals(recipe.getTitle(), result.getTitle());
    assertEquals(recipe.getChecklist(), result.getChecklist());
  }

  @Test
  public void testGetRecipeFailureNotFound() {
    // Setup test recipe
    ArrayList<String> checklist = new ArrayList<>();
    checklist.add("Test Step");

    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setCreatorId(1L);
    recipe.setTitle("Test Recipe");
    recipe.setChecklist(checklist);

    // Mock DBSequence
    when(recipeRepository.getById(recipe.getId())).thenReturn(null);

    // Perform test
    try {
      recipeService.getRecipe(recipe.getId());
    } catch (ResponseStatusException e) {
      assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
    }
  }
}
