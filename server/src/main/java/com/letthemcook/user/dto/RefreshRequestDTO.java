package com.letthemcook.user.dto;

public class RefreshRequestDTO {
  private String refreshToken;
  public String getRefreshToken() {
    return refreshToken;
  }
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
