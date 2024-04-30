package com.letthemcook.session.dto;

import java.util.HashMap;

public class SessionUserStateDTO {
  private Long sessionId;
  private Integer recipeSteps;
  private HashMap<Long, Boolean[]> currentStepValues;

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Integer getRecipeSteps() {
    return recipeSteps;
  }

  public void setRecipeSteps(Integer recipeSteps) {
    this.recipeSteps = recipeSteps;
  }

  public HashMap<Long, Boolean[]> getCurrentStepValues() {
    return currentStepValues;
  }

  public void setCurrentStepValues(HashMap<Long, Boolean[]> currentStepValues) {
    this.currentStepValues = currentStepValues;
  }
}
