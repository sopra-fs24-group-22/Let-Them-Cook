package com.letthemcook.user;

public enum QueryParams {
  USER_NAME("username");

  private final String value;

  QueryParams(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
