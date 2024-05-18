package com.letthemcook.session.dto;

import java.util.ArrayList;
import java.util.Date;

public class SessionPutDTO {
  private Long id;
  private Long host;
  private String hostName;
  private Long recipe;
  private String recipeName;
  private String sessionName;
  private Integer maxParticipantCount;
  private Integer currentParticipantCount;
  private ArrayList<Long> participants;
  private Date date;
  private Integer duration;

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

  public Integer getCurrentParticipantCount() {
    return currentParticipantCount;
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

  public void setCurrentParticipantCount(Integer currentParticipantCount) {
    this.currentParticipantCount = currentParticipantCount;
  }

  public void setParticipants(ArrayList<Long> participants) {
    this.participants = participants;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public String getRecipeName() {
    return recipeName;
  }

  public void setRecipeName(String recipeName) {
    this.recipeName = recipeName;
  }
}

