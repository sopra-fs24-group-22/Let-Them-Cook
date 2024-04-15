package com.letthemcook.recipe;

import com.letthemcook.recipe.dto.RecipePostDTO;
import com.letthemcook.rest.mapper.DTORecipeMapper;
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
  public ResponseEntity<Void> createRecipe(@RequestBody RecipePostDTO recipePostDTO) {
    Recipe recipe = DTORecipeMapper.INSTANCE.convertRecipePostDTOToEntity(recipePostDTO);
    recipeService.createRecipe(recipe);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}