package com.letthemcook.recipe;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipeRepository extends MongoRepository<Recipe, Long> {
}
