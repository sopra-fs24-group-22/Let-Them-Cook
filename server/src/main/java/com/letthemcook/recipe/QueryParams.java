package com.letthemcook.recipe;

public enum QueryParams {
  TITLE("title"),
  COOKING_TIME_MIN("cookingTimeMin"),
  CREATOR_NAME("creatorName");

  private final String value;

  QueryParams(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
