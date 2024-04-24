package com.letthemcook.session;

public enum QueryParams{
  SESSION_NAME("sessionName"),
  DATE("date"),
  RECIPE_ID("recipeId"),
  RECIPE_NAME("recipeName"),
  HOST_ID("hostId"),
  HOST_NAME("hostName"),
  MAX_PARTICIPANT_COUNT("maxParticipantCount"),
  MAX_PARTICIPANTS("maxParticipantCount"),
  MIN_PARTICIPANTS("minParticipants");

  private final String value;

  QueryParams(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
