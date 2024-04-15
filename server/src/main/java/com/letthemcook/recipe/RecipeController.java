package com.letthemcook.recipe;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class RecipeController {

  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @PostMapping("/api/recipe")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> createRecipe(@RequestBody Recipe recipe) {
    recipeService.createRecipe(recipe);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}