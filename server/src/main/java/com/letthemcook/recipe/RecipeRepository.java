package com.letthemcook.recipe;

import com.letthemcook.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecipeRepository extends MongoRepository<Recipe, Long> {
  Recipe getById(Long id);
  List<Recipe> getByCreatorId(Long creatorId);
}
