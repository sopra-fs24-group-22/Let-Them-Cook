package com.letthemcook.user;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Long> {
    User getById(Long id);
    User getByEmail(String email);
    User getByUsername(String username);
}
