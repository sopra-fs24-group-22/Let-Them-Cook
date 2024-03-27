package com.letthemcook.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {
    List<User> findById(long id);
}
