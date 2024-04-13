package com.letthemcook.session;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, Long> {
  Session getById(Long id);
}
