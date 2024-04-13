package com.letthemcook.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {
  private final SessionRepository sessionRepository;

  @Autowired
  public SessionService(@Qualifier("sessionRepository") SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  public Session getSession(Long sessionId) {
    return sessionRepository.findById(sessionId).orElse(null);
  }
}
