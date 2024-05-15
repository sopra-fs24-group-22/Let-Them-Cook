package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.cookbook.CookbookRepository;
import com.letthemcook.cookbook.CookbookService;
import com.letthemcook.rating.Rating;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.ClassUtils.getMethod;

@Service
@Transactional
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private final UserRepository userRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final JwtService jwtService;
  private final CookbookService cookbookService;
  private final CookbookRepository cookbookRepository;
  private final MongoTemplate mongoTemplate;
  //Logger logger = LoggerFactory.getLogger(RecipeService.class);

  @Autowired
  public RecipeService(@Qualifier("recipeRepository") RecipeRepository recipeRepository, SequenceGeneratorService sequenceGeneratorService, JwtService jwtService, UserRepository userRepository, CookbookService cookbookService, CookbookRepository cookbookRepository, MongoTemplate mongoTemplate) {
    this.recipeRepository = recipeRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.cookbookService = cookbookService;
    this.cookbookRepository = cookbookRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public Recipe createRecipe(Recipe recipe, String accessToken) {
    String username = jwtService.extractUsername(accessToken);

    // Set recipe data
    recipe.setId(sequenceGeneratorService.getSequenceNumber(Recipe.SEQUENCE_NAME));
    recipe.setCreatorId(userRepository.getByUsername(username).getId());
    recipe.setCreatorName(username);

    recipeRepository.save(recipe);
    cookbookService.addRecipeToCookbook(recipe.getCreatorId(), recipe.getId());
    return recipe;
  }

  public void updateRecipe(Recipe recipe, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    Long recipeId = recipe.getId();

    // Check if recipe exists
    Recipe existingRecipe = recipeRepository.getById(recipeId);
    if (existingRecipe == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    // Check if user is authorized to update recipe
    if (!Objects.equals(existingRecipe.getCreatorId(), userRepository.getByUsername(username).getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to update this recipe");
    }

    // Set recipe data
    recipe = updateRecipeData(existingRecipe, recipe);

    recipeRepository.save(recipe);
  }

  public void deleteRecipe(Long id, String accessToken) {
    String username = jwtService.extractUsername(accessToken);

    // Check if recipe exists
    Recipe recipe = recipeRepository.getById(id);
    if (recipe == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    // Check if user is authorized to delete recipe
    if (Objects.equals(recipe.getCreatorId(), userRepository.getByUsername(username).getId())) {
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

  public Recipe getRecipe(Long id, String accessToken) {
    Recipe recipe = recipeRepository.getById(id);

    User user = userRepository.getByUsername(jwtService.extractUsername(accessToken));

    if (recipe == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }
    return recipe;
  }

  public List<Recipe> getRecipes(Integer limit, Integer offset, Map<String, String> allParams) {
    Query query = new Query();
    query.limit(limit);
    query.skip(offset);

    // Optional params
    if (allParams.containsKey(QueryParams.TITLE.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.TITLE.getValue()).regex(".*" + allParams.get(QueryParams.TITLE.getValue()) + ".*", "i"));
    }
    if (allParams.containsKey(QueryParams.COOKING_TIME_MIN.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.COOKING_TIME_MIN.getValue()).lte(Integer.parseInt(allParams.get(QueryParams.COOKING_TIME_MIN.getValue()))));
    }
    if (allParams.containsKey(QueryParams.CREATOR_NAME.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.CREATOR_NAME.getValue()).regex(".*" + allParams.get(QueryParams.CREATOR_NAME.getValue()) + ".*", "i"));
    }

    // Only find public recipes
    query.addCriteria(Criteria.where("privacyStatus").is(1L));

    return mongoTemplate.find(query, Recipe.class);
  }

  public void rateRecipe(Long id, String accessToken, Integer rating) {
    Recipe recipe = recipeRepository.getById(id);
    Rating recipeRating = recipe.getRating();
    String username = jwtService.extractUsername(accessToken);

    // Check if rater and ratee are same user
    if(recipe.getCreatorId().equals(userRepository.getByUsername(username).getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot rate your own recipe");
    }

    recipeRating.addRating(rating, userRepository.getByUsername(username).getId());
    recipe.setRating(recipeRating);
    recipeRepository.save(recipe);
  }

  // ######################################### Util #########################################

  private Recipe updateRecipeData(Recipe existingRecipe, Recipe recipe) {

    Method[] getters = {
            getMethod(Recipe.class, "getTitle"),
            getMethod(Recipe.class, "getChecklist"),
            getMethod(Recipe.class, "getIngredients"),
            getMethod(Recipe.class, "getCookingTimeMin"),
            getMethod(Recipe.class, "getPrivacyStatus")
    };

    Method[] setters = {
            getMethod(Recipe.class, "setTitle", String.class),
            getMethod(Recipe.class, "setChecklist", ArrayList.class),
            getMethod(Recipe.class, "setIngredients", ArrayList.class),
            getMethod(Recipe.class, "setCookingTimeMin", int.class),
            getMethod(Recipe.class, "setPrivacyStatus", int.class)
    };

    for (int i = 0; i < getters.length; i++) {
      try {
        Object value = getters[i].invoke(recipe);
        if (value != null) {
          setters[i].invoke(existingRecipe, value);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return existingRecipe;
  }

  public void deleteRecipeByUser(Recipe recipe) {
    recipeRepository.delete(recipe);
  }
}