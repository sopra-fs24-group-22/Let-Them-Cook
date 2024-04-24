package com.letthemcook.session.dto;

public class SessionCredentialsDTO {
  private Long hostId;
  private String roomId;
  private Long sessionId;

  public Long getHostId() {
    return hostId;
  }

  public String getRoomId() {
    return roomId;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setHostId(Long hostId) {
    this.hostId = hostId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }
}
