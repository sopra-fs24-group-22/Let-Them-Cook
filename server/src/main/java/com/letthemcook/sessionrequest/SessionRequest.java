package com.letthemcook.sessionrequest;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document
public class SessionRequest {

  @Id
  private Long userId;
  private HashMap<Long, QueueStatus> userSessions;

  public Long getUserId() {
    return userId;
  }

  public HashMap<Long, QueueStatus> getUserSessions() {
    return userSessions;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUserSessions(HashMap<Long, QueueStatus> userSessions) {
    this.userSessions = userSessions;
  }
}
