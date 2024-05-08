package com.letthemcook.cookbook;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Objects;

@Service
@Transactional
public class CookbookService {
  private final CookbookRepository cookbookRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;

  @Autowired
  public CookbookService(CookbookRepository cookbookRepository, SequenceGeneratorService sequenceGeneratorService, JwtService jwtService, UserRepository userRepository, RecipeRepository recipeRepository) {
    this.cookbookRepository = cookbookRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.recipeRepository = recipeRepository;
  }

  public Cookbook createCookbook(Long ownerId) {
    Cookbook cookbook = new Cookbook(ownerId);
    cookbook.setId(sequenceGeneratorService.getSequenceNumber(Cookbook.SEQUENCE_NAME));
    cookbookRepository.save(cookbook);

    return cookbook;
  }

  /**
   * Adds recipe to the cookbook of the user when a new recipe is created
   *
   * @param ownerId: The id of the user who owns the cookbook
   * @param recipeId: The id of the recipe to be added to the cookbook
   */
  public void addRecipeToCookbook(Long ownerId, Long recipeId) {
    Cookbook cookbook = cookbookRepository.getByOwnerId(ownerId);
    cookbook.addRecipe(recipeId);
    cookbookRepository.save(cookbook);
  }

  /**
   * Adds recipe to the cookbook when the recipe does not belong to the user
   *
   * @param recipeId: The id of the recipe to be added to the cookbook
   * @param accessToken: The access token of the user
   */
  public Cookbook addRecipeToCookbook(Long recipeId, String accessToken) {
    // Extract user data
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);
    Long ownerId = userRepository.getByUsername(username).getId();
    Cookbook cookbook = cookbookRepository.getByOwnerId(ownerId);
    Recipe recipe = recipeRepository.getById(recipeId);

    // Add recipe to cookbook
    if(recipe == null || (recipe.getPrivacyStatus() == 0 && !Objects.equals(recipe.getCreatorId(), ownerId))) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }
    if (cookbook.getRecipeIds().contains(recipeId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Recipe already in cookbook");
    }
    cookbook.addRecipe(recipeId);
    cookbookRepository.save(cookbook);

    return cookbook;
  }

  public Cookbook removeRecipeFromCookbook(Long recipeId, String accessToken) {
    // Extract user data
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);
    Cookbook cookbook = cookbookRepository.getByOwnerId(userRepository.getByUsername(username).getId());

    // Remove recipe from cookbook
    if(recipeRepository.getById(recipeId) == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }
    if (!cookbook.getRecipeIds().contains(recipeId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not in cookbook");
    }
    cookbook.removeRecipe(recipeId);
    cookbookRepository.save(cookbook);

    return cookbook;
  }

  public ArrayList<Recipe> getCookbook(Long ownerId, String accessToken) {
    // Extract user data
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);
    Long userId = userRepository.getByUsername(username).getId();
    Cookbook cookbook = cookbookRepository.getByOwnerId(ownerId);

    if(cookbook == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cookbook not found");
    }
    if(!Objects.equals(cookbook.getOwnerId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden access");
    }

    return (ArrayList<Recipe>) recipeRepository.findAllById(cookbook.getRecipeIds());
  }

  public void deleteCookbook(Long ownerId) {
    Cookbook cookbook = cookbookRepository.getByOwnerId(ownerId);

    if(cookbook == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cookbook not found");
    }
    if(!Objects.equals(cookbook.getOwnerId(), ownerId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden access");
    }

    cookbookRepository.delete(cookbook);
  }
}
