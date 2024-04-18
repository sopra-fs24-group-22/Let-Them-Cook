package com.letthemcook.session;

public enum QueryParams{
  SESSION_NAME("sessionName"),
  DATE("date"),
  RECIPE("recipe"),
  HOST("host"),
  MAX_PARTICIPANT_COUNT("maxParticipantCount"),
  MAX_PARTICIPANTS("max_participants"),
  MIN_PARTICIPANTS("min_participants");

  private final String value;

  QueryParams(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
