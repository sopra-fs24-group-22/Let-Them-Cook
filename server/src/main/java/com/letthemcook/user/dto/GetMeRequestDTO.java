package com.letthemcook.user.dto;

public class GetMeRequestDTO {
  private String email;
  private String firstname;
  private String lastname;
  private String username;

  public String getEmail() {
    return email;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public String getUsername() {
    return username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
