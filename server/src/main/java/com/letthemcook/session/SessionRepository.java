package com.letthemcook.session;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends MongoRepository<Session, Long> {
  Session getById(Long id);
  void deleteById(Long id);
  List<Session> findAllByDateAfter(LocalDateTime date);
}
