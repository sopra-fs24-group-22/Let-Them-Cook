package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.cookbook.CookbookRepository;
import com.letthemcook.cookbook.CookbookService;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Transactional
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private final UserRepository userRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final JwtService jwtService;
  private final CookbookService cookbookService;
  private final CookbookRepository cookbookRepository;
  //Logger logger = LoggerFactory.getLogger(RecipeService.class);

  @Autowired
  public RecipeService(@Qualifier("recipeRepository") RecipeRepository recipeRepository, SequenceGeneratorService sequenceGeneratorService, JwtService jwtService , UserRepository userRepository, CookbookService cookbookService, CookbookRepository cookbookRepository) {
    this.recipeRepository = recipeRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.cookbookService = cookbookService;
    this.cookbookRepository = cookbookRepository;
  }

  public Recipe createRecipe(Recipe recipe, String accessToken) {
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);

    // Set recipe data
    recipe.setId(sequenceGeneratorService.getSequenceNumber(Recipe.SEQUENCE_NAME));
    recipe.setCreatorId(userRepository.getByUsername(username).getId());

    recipeRepository.save(recipe);
    cookbookService.addRecipeToCookbook(recipe.getCreatorId(), recipe.getId());
    return recipe;
  }

  public void deleteRecipe(Long id, String accessToken) {
    // Remove Bearer from string
    accessToken = accessToken.substring(7);

    // Check if recipe exists
    Recipe recipe = recipeRepository.getById(id);
    if (recipe == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    // Check if user is authorized to delete recipe
    if(Objects.equals(recipe.getCreatorId(), userRepository.getByUsername(jwtService.extractUsername(accessToken)).getId())) {
      recipeRepository.deleteById(id);

      // Delete recipe from all cookbooks
      cookbookRepository.findCookbookByRecipeIdsContaining(id).forEach(cookbook -> {
        cookbook.removeRecipe(id);
        cookbookRepository.save(cookbook);
      });
      return;
    }

    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to delete this recipe");
  }

  public Recipe getRecipe(Long id) {
    Recipe recipe = recipeRepository.getById(id);
    if (recipe == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }
    return recipe;
  }
}
