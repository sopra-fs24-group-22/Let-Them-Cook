package com.letthemcook.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {
    User findById(long id);
    User findByEmail(String email);
}
