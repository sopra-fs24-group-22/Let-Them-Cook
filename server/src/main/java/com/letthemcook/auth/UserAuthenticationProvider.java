package com.letthemcook.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.letthemcook.rest.mapper.DTOMapper;
import com.letthemcook.user.User;
import com.letthemcook.user.UserDTO;
import com.letthemcook.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
public class UserAuthenticationProvider {
  @Value("${security.jwt.token.secret-key:secret-key}")
  private String secretKey;

  private final UserRepository userRepository;
  private final AuthenticationService authenticationService;
  public UserAuthenticationProvider(UserRepository userRepository, AuthenticationService authenticationService) {
    this.userRepository = userRepository;
    this.authenticationService = authenticationService;
  }

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String email) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + 3_600_000); // 1 hour
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    return JWT.create()
            .withIssuer(email)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .sign(algorithm);
  }

  public Authentication validateToken(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    JWTVerifier verifier = JWT.require(algorithm).build();

    DecodedJWT decoded = verifier.verify(token);

    User user = userRepository.findByEmail(decoded.getIssuer());

    return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
  }

  public Authentication validateCredentials(UserDTO userDTO) {
    authenticationService.authenticateUser(DTOMapper.INSTANCE.convertUserLoginDTOToEntity(userDTO));
    return new UsernamePasswordAuthenticationToken(userDTO, null, Collections.emptyList());
  }
}
