package com.letthemcook.recipe.dto;


import java.util.ArrayList;

public class RecipeRatingGetDTO {
  private Long id;
  private Long creatorId;
  private String creatorName;
  private String title;
  private ArrayList<String> checklist;
  private ArrayList<String> ingredients;
  private int cookingTimeMin;
  private int privacyStatus;
  private Float avgTotalRating;
  private Integer nrRatings;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
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

  public int getPrivacyStatus() {
    return privacyStatus;
  }

  public void setPrivacyStatus(int privacyStatus) {
    this.privacyStatus = privacyStatus;
  }

  public Float getAvgTotalRating() {
    return avgTotalRating;
  }

  public void setAvgTotalRating(Float avgTotalRating) {
    this.avgTotalRating = avgTotalRating;
  }

  public Integer getNrRatings() {
    return nrRatings;
  }

  public void setNrRatings(Integer nrRatings) {
    this.nrRatings = nrRatings;
  }
}
