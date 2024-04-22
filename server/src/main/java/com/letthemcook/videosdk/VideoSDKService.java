package com.letthemcook.videosdk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class VideoSDKService {

  @Value("${videosdk.api.token}")
  String token;

  public String fetchRoomId() throws IOException {

    URL url = new URL("https://api.videosdk.live/v2/rooms");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", token);
    con.setRequestMethod("POST");

    int status = con.getResponseCode();
    BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();

    return content.toString().split("\"roomId\":\"")[1].split("\"")[0];
  }
}
