package com.letthemcook.user.dto;

public class GetMeRequestDTO {
  private String email;
  private String firstname;
  private String lastname;
  private String username;
  private Long id;
  private Float avgTotalRating;
  private Integer nrRatings;

  public String getEmail() { return email; }

  public String getFirstname() { return firstname; }

  public String getLastname() { return lastname; }

  public String getUsername() {
    return username;
  }

  public Long getId() { return id; }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public void setUsername(String username) { this.username = username; }

  public void setId(Long id) { this.id = id; }

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
