package com.letthemcook.recipe;

import com.letthemcook.util.SequenceGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  //Logger logger = LoggerFactory.getLogger(RecipeService.class);

  @Autowired
  public RecipeService(@Qualifier("recipeRepository") RecipeRepository recipeRepository, SequenceGeneratorService sequenceGeneratorService) {
    this.recipeRepository = recipeRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
  }

  public Recipe createRecipe(Recipe recipe) {
    // Set recipe data
    recipe.setId(sequenceGeneratorService.getSequenceNumber(Recipe.SEQUENCE_NAME));
    recipeRepository.save(recipe);
    return recipe;
  }
}
