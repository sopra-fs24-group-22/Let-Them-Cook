package com.letthemcook.sessionrequest.dto;

import com.letthemcook.sessionrequest.QueueStatus;

import java.util.HashMap;

public class SessionRequestGetSingleDTO {
  private HashMap<Long, QueueStatus> sessionRequests = new HashMap<Long, QueueStatus>();

  public HashMap<Long, QueueStatus> getSessionRequests() {
    return sessionRequests;
  }

  public void setSessionRequests(HashMap<Long, QueueStatus> sessionRequests) {
    this.sessionRequests = sessionRequests;
  }
}
