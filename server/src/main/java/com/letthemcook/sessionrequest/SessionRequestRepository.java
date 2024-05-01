package com.letthemcook.sessionrequest;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRequestRepository extends MongoRepository<SessionRequest, Long> {
  SessionRequest getSessionRequestByUserId(Long userId);
}
