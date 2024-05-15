package com.letthemcook.recipe;

import com.letthemcook.rating.dto.RecipeRateDTO;
import com.letthemcook.recipe.dto.RecipeGetDTO;
import com.letthemcook.recipe.dto.RecipePostDTO;
import com.letthemcook.recipe.dto.RecipeRatingGetDTO;
import com.letthemcook.rest.mapper.DTORecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {

  private final RecipeService recipeService;

  @Autowired
  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @PostMapping("/api/recipe")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createRecipe(@RequestBody RecipePostDTO recipePostDTO, @RequestHeader("Authorization") String accessToken) {
    Recipe recipe = DTORecipeMapper.INSTANCE.convertRecipePostDTOToRecipe(recipePostDTO);
    recipeService.createRecipe(recipe, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/api/recipe")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> updateRecipe(@RequestBody RecipeGetDTO recipeGetDTO, @RequestHeader("Authorization") String accessToken) {
    Recipe recipe = DTORecipeMapper.INSTANCE.convertRecipePutDTOToEntity(recipeGetDTO);
    recipeService.updateRecipe(recipe, accessToken);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/api/recipe/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> deleteRecipe(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    recipeService.deleteRecipe(id, accessToken);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/api/recipe/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<RecipeRatingGetDTO> getRecipe(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    Recipe recipe = recipeService.getRecipe(id, accessToken);

    return ResponseEntity.status(HttpStatus.OK).body(DTORecipeMapper.INSTANCE.convertRecipeAndRatingToRecipeRatingGetDTO(recipe, recipe.getRating()));
  }

  @GetMapping("/api/recipes")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<RecipeRatingGetDTO>> getRecipes(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(required = false) Map<String,String> allParams) {
    List<Recipe> queriedRecipes = recipeService.getRecipes(limit, offset, allParams);

    // Convert each recipe to the API representation
    ArrayList<RecipeRatingGetDTO> recipesGetDTOS = new ArrayList<>();
    for (Recipe recipe : queriedRecipes) {
      recipesGetDTOS.add(DTORecipeMapper.INSTANCE.convertRecipeAndRatingToRecipeRatingGetDTO(recipe, recipe.getRating()));
    }

    return ResponseEntity.status(HttpStatus.OK).body(recipesGetDTOS);
  }

  @PostMapping("/api/recipe/{id}/rate")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<Void> rateRecipe(@PathVariable Long id, @RequestHeader("Authorization") String accessToken, @RequestBody RecipeRateDTO recipeRateDTO) {
    recipeService.rateRecipe(id, accessToken, recipeRateDTO.getRating());

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}