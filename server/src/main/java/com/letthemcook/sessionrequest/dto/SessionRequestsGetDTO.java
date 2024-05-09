package com.letthemcook.sessionrequest.dto;

import com.letthemcook.sessionrequest.QueueStatus;

import java.util.HashMap;

public class SessionRequestsGetDTO {
  private HashMap<Long, QueueStatus> userSessions;

  public HashMap<Long, QueueStatus> getUserSessions() {
    return userSessions;
  }

  public void setUserSessions(HashMap<Long, QueueStatus> userSessions) {
    this.userSessions = userSessions;
  }
}
