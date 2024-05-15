package com.letthemcook.cookbook;

import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.dto.RecipeGetDTO;
import com.letthemcook.recipe.dto.RecipeRatingGetDTO;
import com.letthemcook.rest.mapper.DTORecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class CookbookController {
  private final CookbookService cookbookService;

  @Autowired
  public CookbookController(CookbookService cookbookService) {
    this.cookbookService = cookbookService;
  }

  @PostMapping("/api/cookbook/recipe/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> addRecipeToCookbook(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    cookbookService.addRecipeToCookbook(id, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/api/cookbook/recipe/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> removeRecipeFromCookbook(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    cookbookService.removeRecipeFromCookbook(id, accessToken);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/api/cookbook/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<ArrayList<RecipeRatingGetDTO>> getCookbook(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    ArrayList<Recipe> recipes = cookbookService.getCookbook(id, accessToken);

    // Convert each recipe to the API representation
    ArrayList<RecipeRatingGetDTO> recipesGetDTOS = new ArrayList<>();
    for (Recipe recipe : recipes) {
      recipesGetDTOS.add(DTORecipeMapper.INSTANCE.convertRecipeAndRatingToRecipeRatingGetDTO(recipe, recipe.getRating()));
    }

    return ResponseEntity.ok(recipesGetDTOS);
  }
}