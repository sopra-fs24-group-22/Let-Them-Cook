package com.letthemcook.session.dto;

import java.util.ArrayList;

public class SessionPostDTO {
  private Long id;
  private Long recipe;
  private String sessionName;
  private Integer maxParticipantCount;
  private String date;

  public Long getId() {
    return id;
  }

  public Long getRecipe() {
    return recipe;
  }

  public String getSessionName() {
    return sessionName;
  }

  public Integer getMaxParticipantCount() {
    return maxParticipantCount;
  }


  public String getDate() {
    return date;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRecipe(Long recipe) {
    this.recipe = recipe;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public void setMaxParticipantCount(Integer maxParticipantCount) {
    this.maxParticipantCount = maxParticipantCount;
  }


  public void setDate(String date) {
    this.date = date;
  }
}
