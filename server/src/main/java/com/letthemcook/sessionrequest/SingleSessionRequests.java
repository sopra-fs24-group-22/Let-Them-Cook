package com.letthemcook.sessionrequest;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleSessionRequests {
  private Long userId;
  private String username;
  private QueueStatus queueStatus;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public QueueStatus getQueueStatus() {
    return queueStatus;
  }

  public void setQueueStatus(QueueStatus queueStatus) {
    this.queueStatus = queueStatus;
  }
}
