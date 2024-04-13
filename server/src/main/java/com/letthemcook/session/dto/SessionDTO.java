package com.letthemcook.session.dto;

public class SessionDTO {
  private Long id;
  private Long host;
  //private Recipe recipe;
  private String sessionName;
  private Integer participantCount;
  private String Date;

  public Long getId() {
    return id;
  }

  public Long getHost() {
    return host;
  }

  /*public Recipe getRecipe() {
    return recipe;
  }
*/
  public String getSessionName() {
    return sessionName;
  }

  public Integer getParticipantCount() {
    return participantCount;
  }

  public String getDate() {
    return Date;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setHost(Long host) {
    this.host = host;
  }

  /*public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }
*/
  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public void setParticipantCount(Integer participantCount) {
    this.participantCount = participantCount;
  }

  public void setDate(String date) {
    Date = date;
  }
}
