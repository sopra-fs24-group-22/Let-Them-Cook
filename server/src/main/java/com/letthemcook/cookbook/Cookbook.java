package com.letthemcook.cookbook;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
public class Cookbook {
  @Transient
  public static final String SEQUENCE_NAME = "cookbook_sequence";

  @Id
  private Long id;
  private Long ownerId;
  private ArrayList<Long> recipeIds = new ArrayList<>();

  public Cookbook(Long ownerId) {
    this.ownerId = ownerId;
  }

  public ArrayList<Long> getRecipeIds() {
    return recipeIds;
  }

  public void setRecipeIds(ArrayList<Long> recipeIds) {
    this.recipeIds = recipeIds;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void addRecipe(Long recipeId) {
    recipeIds.add(recipeId);
  }

  public void removeRecipe(Long recipeId) {
    recipeIds.remove(recipeId);
  }
}
