package com.letthemcook.session;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@Document
public class Session {
  @Transient
  public static final String SEQUENCE_NAME = "sessions_sequence";

  @Id
  private Long id;
  private Long hostId;
  private String hostName;
  private Long recipeId;
  private String sessionName;
  private Integer maxParticipantCount;
  private Integer currentParticipantCount;
  private ArrayList<Long> participants;
  private SessionUserState sessionUserState;
  private LocalDateTime date;
  private String roomId;
  private Integer duration;

  public Long getId() {
    return id;
  }

  public Long getHostId() {
    return hostId;
  }

  public Long getRecipeId() {
    return recipeId;
  }

  public String getSessionName() {
    return sessionName;
  }

  public Integer getCurrentParticipantCount() {
    return currentParticipantCount;
  }

  public Integer getMaxParticipantCount() {
    return maxParticipantCount;
  }

  public ArrayList<Long> getParticipants() {
    return participants;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setHostId(Long hostId) {
    this.hostId = hostId;
  }

  public void setRecipeId(Long recipeId) {
    this.recipeId = recipeId;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public void setCurrentParticipantCount(Integer currentParticipantCount) {
    this.currentParticipantCount = currentParticipantCount;
  }

  public void setMaxParticipantCount(Integer maxParticipantCount) {
    this.maxParticipantCount = maxParticipantCount;
  }

  public void setParticipants(ArrayList<Long> participants) {
    this.participants = participants;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setDate(Date date) {
    this.date = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date));
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public SessionUserState getSessionUserState() {
    return sessionUserState;
  }

  public void setSessionUserState(SessionUserState sessionUserState) {
    this.sessionUserState = sessionUserState;
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
}
