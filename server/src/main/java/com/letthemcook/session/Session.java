package com.letthemcook.session;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.Date;

@Document("sessions")
public class Session {
  @Transient
  public static final String SEQUENCE_NAME = "sessions_sequence";

  @Id
  private Long id;
  private Long host;
  private Long recipe;
  private String sessionName;
  private Integer maxParticipantCount;
  private ArrayList<Long> participants;
  private Date date;

  public Long getId() {
    return id;
  }

  public Long getHost() {
    return host;
  }

  public Long getRecipe() {
    return recipe;
  }

  public String getSessionName() {
    return sessionName;
  }

  public Integer getMaxParticipantCount() {
    return maxParticipantCount;
  }

  public ArrayList<Long> getParticipants() {
    return participants;
  }

  public Date getDate() {
    return date;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setHost(Long host) {
    this.host = host;
  }

  public void setRecipe(Long recipe) {
    this.recipe = recipe;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public void setMaxParticipantCount(Integer maxParticipantCount) {
    this.maxParticipantCount = maxParticipantCount;
  }

  public void setParticipants(ArrayList<Long> participants) {
    this.participants = participants;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
