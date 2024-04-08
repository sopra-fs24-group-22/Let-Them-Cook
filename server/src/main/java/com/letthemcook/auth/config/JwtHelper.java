package com.letthemcook.auth.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.letthemcook.auth.refreshToken.RefreshToken;
import com.letthemcook.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtHelper {
  static final String issuer = "LetThemCook";
  @Value("${accessTokenExpirationMs}")
  private long accessTokenExpirationMs; // 1 min
  @Value("${refreshTokenExpirationMs}")
  private long refreshTokenExpirationMs; // 1 day

  private Algorithm accessTokenAlgorithm;
  private Algorithm refreshTokenAlgorithm;
  private JWTVerifier accessTokenVerifier;
  private JWTVerifier refreshTokenVerifier;

  public JwtHelper(@Value("${accessTokenSecret}") String accessTokenSecret, @Value("${refreshTokenSecret}") String refreshTokenSecret) {
    accessTokenAlgorithm = Algorithm.HMAC256(accessTokenSecret);
    refreshTokenAlgorithm = Algorithm.HMAC256(refreshTokenSecret);
    accessTokenVerifier = JWT.require(accessTokenAlgorithm)
            .withIssuer(issuer)
            .build();
    refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
            .withIssuer(issuer)
            .build();
  }

  public String generateAccessToken(User user) {
    return JWT.create()
            .withIssuer(issuer)
            .withSubject(String.valueOf(user.getId()))
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
            .sign(accessTokenAlgorithm);
  }

  public String generateRefreshToken(User user, RefreshToken refreshToken) {
    return JWT.create()
            .withIssuer(issuer)
            .withSubject(String.valueOf(user.getId()))
            .withClaim("tokenId", refreshToken.getId())
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + refreshTokenExpirationMs))
            .sign(refreshTokenAlgorithm);
  }

  private Optional<DecodedJWT> decodeAccessToken(String token) {
    try {
      return Optional.of(accessTokenVerifier.verify(token));
    } catch (JWTVerificationException e) {
      return Optional.empty();
    }
  }

  private Optional<DecodedJWT> decodeRefreshToken(String token) {
    try {
      return Optional.of(refreshTokenVerifier.verify(token));
    } catch (JWTVerificationException e) {
    }
    return Optional.empty();
  }

  public boolean validateAccessToken(String token) {
    return decodeAccessToken(token).isPresent();
  }

  public boolean validateRefreshToken(String token) {
    return decodeRefreshToken(token).isPresent();
  }

  public String getUserIdFromAccessToken(String token) {
    return decodeAccessToken(token).get().getSubject();
  }

  public Long getUserIdFromRefreshToken(String token) {
    return Long.valueOf(decodeRefreshToken(token).get().getSubject());
  }

  public String getTokenIdFromRefreshToken(String token) {
    return decodeRefreshToken(token).get().getClaim("tokenId").asString();
  }
}
