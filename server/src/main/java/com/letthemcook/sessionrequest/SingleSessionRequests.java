package com.letthemcook.sessionrequest;

import java.util.HashMap;

public class SingleSessionRequests {
  private HashMap<Long, QueueStatus> sessionRequests = new HashMap<Long, QueueStatus>();

  public HashMap<Long, QueueStatus> getSessionRequests() {
    return sessionRequests;
  }

  public void setSessionRequests(HashMap<Long, QueueStatus> sessionRequests) {
    this.sessionRequests = sessionRequests;
  }
}
