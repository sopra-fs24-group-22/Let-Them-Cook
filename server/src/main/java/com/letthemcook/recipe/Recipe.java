package com.letthemcook.recipe;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
public class Recipe {
  @Transient
  public static final String SEQUENCE_NAME = "recipe_sequence";

  @Id
  private Long id;
  private Long creatorId;
  private String creatorName;
  private String title;
  private ArrayList<String> checklist;
  private ArrayList<String> ingredients;
  private int cookingTimeMin;
  private int privacyStatus;

  public ArrayList<String> getChecklist() {
    return checklist;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setChecklist(ArrayList<String> checklist) {
    this.checklist = checklist;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public int getPrivacyStatus() {
    return privacyStatus;
  }

  public void setPrivacyStatus(int privacyStatus) {
    this.privacyStatus = privacyStatus;
  }

  public ArrayList<String> getIngredients() {
    return ingredients;
  }

  public void setIngredients(ArrayList<String> ingredients) {
    this.ingredients = ingredients;
  }

  public int getCookingTimeMin() {
    return cookingTimeMin;
  }

  public void setCookingTimeMin(int cookingTimeMin) {
    this.cookingTimeMin = cookingTimeMin;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }
}
