package com.letthemcook.cookbook;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CookbookRepository extends MongoRepository<Cookbook, Long> {
  Cookbook getByOwnerId(Long ownerId);
}
