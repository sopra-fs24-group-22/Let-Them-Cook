package com.letthemcook.session.dto;

public class SessionCredentialsDTO {
  private Long hostId;
  private String roomId;

  public Long getHostId() {
    return hostId;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setHostId(Long hostId) {
    this.hostId = hostId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
