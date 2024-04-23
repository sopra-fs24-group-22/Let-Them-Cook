package com.letthemcook.recipe;

import com.letthemcook.recipe.dto.RecipeGetDTO;
import com.letthemcook.recipe.dto.RecipePostDTO;
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

  @PostMapping("/recipe")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createRecipe(@RequestBody RecipePostDTO recipePostDTO, @RequestHeader("Authorization") String accessToken) {
    Recipe recipe = DTORecipeMapper.INSTANCE.convertRecipePostDTOToRecipe(recipePostDTO);
    recipeService.createRecipe(recipe, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/recipe/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> deleteRecipe(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    recipeService.deleteRecipe(id, accessToken);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/recipe/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<RecipeGetDTO> getRecipe(@PathVariable Long id) {
    Recipe recipe = recipeService.getRecipe(id);

    return ResponseEntity.status(HttpStatus.OK).body(DTORecipeMapper.INSTANCE.convertRecipeToRecipeGetDTO(recipe));
  }

  @GetMapping("/recipes")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<RecipeGetDTO>> getRecipes(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(required = false) Map<String,String> allParams) {
    List<Recipe> queriedRecipes = recipeService.getRecipes(limit, offset, allParams);

    // Convert each recipe to the API representation
    ArrayList<RecipeGetDTO> recipesGetDTOS = new ArrayList<>();
    for (Recipe recipe : queriedRecipes) {
      recipesGetDTOS.add(DTORecipeMapper.INSTANCE.convertRecipeToRecipeGetDTO(recipe));
    }

    return ResponseEntity.status(HttpStatus.OK).body(recipesGetDTOS);
  }
}