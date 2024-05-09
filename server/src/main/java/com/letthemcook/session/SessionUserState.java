package com.letthemcook.session;

import java.util.Date;
import java.util.HashMap;

public class SessionUserState {
  private Long sessionId;
  private Integer recipeSteps;
  private HashMap<Long, Boolean[]> currentStepValues;
  private HashMap<Long, Date> lastActiveUsers;

  public long getSessionId() {
    return sessionId;
  }

  public void setSessionId(long sessionId) {
    this.sessionId = sessionId;
  }

  public HashMap<Long, Boolean[]> getCurrentStepValues() {
    return currentStepValues;
  }

  public void setCurrentStepValues(HashMap<Long, Boolean[]> currentStepValues) {
    this.currentStepValues = currentStepValues;
  }

  public void setRecipeSteps(Integer recipeSteps) {
    this.recipeSteps = recipeSteps;
  }

  public Integer getRecipeSteps() {
    return this.recipeSteps;
  }

  public HashMap<Long, Date> getLastActiveUsers() {
    return lastActiveUsers;
  }

  public void setLastActiveUsers(HashMap<Long, Date> lastActiveUsers) {
    this.lastActiveUsers = lastActiveUsers;
  }

  public void addUserToStepCount(Long userId) {
    Boolean[] userSteps = new Boolean[recipeSteps];

    // Initialize last Active Date
    lastActiveUsers.put(userId, new Date());

    // Check if user already exists in StepCount
    if (currentStepValues.containsKey(userId)) {
      return;
    }

    currentStepValues.put(userId, userSteps);
  }

  public void removeUserFromStepCount(Long userId) {
    currentStepValues.remove(userId);
  }

  public void updateCheckpoint(Long userId, Integer stepIndex, Boolean isChecked) {
    if(stepIndex >= recipeSteps || stepIndex < 0) {
      throw new IllegalArgumentException("Step index is out of bounds");
    }

    // Set state of step
    if(isChecked) {
      Boolean[] userSteps = currentStepValues.get(userId);
      userSteps[stepIndex] = true;
      currentStepValues.put(userId, userSteps);
    } else {
      Boolean[] userSteps = currentStepValues.get(userId);
      userSteps[stepIndex] = false;
      currentStepValues.put(userId, userSteps);
    }
  }

  public void updateUserActivity(Long userId) {
    this.lastActiveUsers.put(userId, new Date());
  }
}
