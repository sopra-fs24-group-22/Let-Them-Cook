package com.letthemcook.user.dto;

public class LogoutRequestDTO {
  private String username;
  private String email;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() { return email; }

  public void setEmail(String email) { this.email = email; }
}
