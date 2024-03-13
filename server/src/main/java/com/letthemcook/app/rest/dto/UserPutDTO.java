package com.letthemcook.app.rest.dto;

public class UserPutDTO {

  private Long birthday;

  private String username;

  public Long getBirthday() {
    return birthday;
  }

  public void setBirthday(Long birthday) {
    this.birthday = birthday;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
