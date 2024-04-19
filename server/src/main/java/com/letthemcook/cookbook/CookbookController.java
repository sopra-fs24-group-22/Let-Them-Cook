package com.letthemcook.cookbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CookbookController {
  private final CookbookService cookbookService;

  @Autowired
  public CookbookController(CookbookService cookbookService) {
    this.cookbookService = cookbookService;
  }

  @PostMapping("/cookbook/recipe/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ResponseEntity<Void> addRecipeToCookbook(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    cookbookService.addRecipeToCookbook(id, accessToken);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/cookbook/recipe/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public ResponseEntity<Void> removeRecipeFromCookbook(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    cookbookService.removeRecipeFromCookbook(id, accessToken);

    return ResponseEntity.noContent().build();
  }
}
