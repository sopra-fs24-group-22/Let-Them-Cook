package com.letthemcook.recipe;

import com.letthemcook.recipe.dto.RecipeDTO;
import com.letthemcook.rest.mapper.DTORecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<Void> createRecipe(@RequestBody RecipeDTO recipeDTO, @RequestHeader("Authorization") String accessToken) {
    Recipe recipe = DTORecipeMapper.INSTANCE.convertRecipePostDTOToRecipe(recipeDTO);
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
  public ResponseEntity<RecipeDTO> getRecipe(@PathVariable Long id) {
    Recipe recipe = recipeService.getRecipe(id);

    return ResponseEntity.status(HttpStatus.OK).body(DTORecipeMapper.INSTANCE.convertRecipeToRecipePostDTO(recipe));
  }
}