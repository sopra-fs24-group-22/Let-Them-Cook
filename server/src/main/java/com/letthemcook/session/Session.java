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
  private Long hostId;
  private Long recipeId;
  private String sessionName;
  private Integer maxParticipantCount;
  private ArrayList<Long> participants;
  private Date date;
  private String roomId;

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

  public Integer getMaxParticipantCount() {
    return maxParticipantCount;
  }

  public ArrayList<Long> getParticipants() {
    return participants;
  }

  public Date getDate() {
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

  public void setMaxParticipantCount(Integer maxParticipantCount) {
    this.maxParticipantCount = maxParticipantCount;
  }

  public void setParticipants(ArrayList<Long> participants) {
    this.participants = participants;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
