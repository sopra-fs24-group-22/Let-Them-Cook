package com.letthemcook.recipe.dto;

import java.util.ArrayList;

public class RecipePostDTO {
  private Long creatorId;
  private String title;
  private ArrayList<String> checklist;
  private int privacyStatus;

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ArrayList<String> getChecklist() {
    return checklist;
  }

  public void setChecklist(ArrayList<String> checklist) {
    this.checklist = checklist;
  }

  public int getPrivacyStatus() {
    return privacyStatus;
  }

  public void setPrivacyStatus(int privacyStatus) {
    this.privacyStatus = privacyStatus;
  }
}