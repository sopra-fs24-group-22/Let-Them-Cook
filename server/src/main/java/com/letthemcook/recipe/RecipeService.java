package com.letthemcook.recipe;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.cookbook.CookbookRepository;
import com.letthemcook.cookbook.CookbookService;
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

import java.util.List;
import java.util.Map;
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
  private final MongoTemplate mongoTemplate;
  //Logger logger = LoggerFactory.getLogger(RecipeService.class);

  @Autowired
  public RecipeService(@Qualifier("recipeRepository") RecipeRepository recipeRepository, SequenceGeneratorService sequenceGeneratorService, JwtService jwtService , UserRepository userRepository, CookbookService cookbookService, CookbookRepository cookbookRepository, MongoTemplate mongoTemplate) {
    this.recipeRepository = recipeRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.cookbookService = cookbookService;
    this.cookbookRepository = cookbookRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public Recipe createRecipe(Recipe recipe, String accessToken) {
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);

    // Set recipe data
    recipe.setId(sequenceGeneratorService.getSequenceNumber(Recipe.SEQUENCE_NAME));
    recipe.setCreatorId(userRepository.getByUsername(username).getId());
    recipe.setCreatorName(username);

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

  public List<Recipe> getRecipes(Integer limit, Integer offset, Map<String, String> allParams) {
    Query query = new Query();
    query.limit(limit);
    query.skip(offset);

    if (allParams.containsKey(QueryParams.TITLE)) {
      query.addCriteria(Criteria.where(QueryParams.TITLE).regex(".*" + allParams.get(QueryParams.TITLE) + ".*", "i"));
    }
    if (allParams.containsKey(QueryParams.COOKING_TIME_MIN)) {
      query.addCriteria(Criteria.where(QueryParams.COOKING_TIME_MIN).lte(Integer.parseInt(allParams.get(QueryParams.COOKING_TIME_MIN))));
    }
    if (allParams.containsKey(QueryParams.CREATOR_NAME)) {
      query.addCriteria(Criteria.where(QueryParams.CREATOR_NAME).regex(".*" + allParams.get(QueryParams.CREATOR_NAME) + ".*", "i"));
    }

    return mongoTemplate.find(query, Recipe.class);
  }
}