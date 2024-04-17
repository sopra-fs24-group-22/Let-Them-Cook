package com.letthemcook.cookbook;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CookbookRepository extends MongoRepository<Cookbook, Long> {
  Cookbook getByOwnerId(Long ownerId);

  List<Cookbook> findCookbookByRecipeIdsContaining(Long recipeId);
}