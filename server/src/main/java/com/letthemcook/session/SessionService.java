package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.videosdk.VideoSDKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class SessionService {
  private final SessionRepository sessionRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final MongoTemplate mongoTemplate;
  private final VideoSDKService videoSDKService;

  @Autowired
  public SessionService(@Qualifier("sessionRepository") SessionRepository sessionRepository, SequenceGeneratorService sequenceGeneratorService, UserRepository userRepository, JwtService jwtService, UserRepository user, MongoTemplate mongoTemplate, MongoTemplate mongoTemplate1, VideoSDKService videoSDKService) {
    this.sessionRepository = sessionRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.mongoTemplate = mongoTemplate1;
    this.videoSDKService = videoSDKService;
  }

  public Session createSession(Session session, String accessToken) throws IOException {
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);

    session.setId(sequenceGeneratorService.getSequenceNumber(Session.SEQUENCE_NAME));
    session.setHost(userRepository.getByUsername(username).getId());

    String roomID = videoSDKService.fetchRoomId();
    session.setRoomId(roomID);

    sessionRepository.save(session);
    // TODO: Add to my session
    return session;
  }

  public Session getSession(Long sessionId) {
    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }
    return session;
  }

  public void deleteSession(Long sessionId, String accessToken) {
    // Remove Bearer from string
    String cutAccessToken = accessToken.substring(7);

    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    if (Objects.equals(session.getHost(), userRepository.getByUsername(jwtService.extractUsername(cutAccessToken)).getId())) {
      sessionRepository.deleteById(sessionId);
      return;
    }

    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to delete this session");
  }

  public List<Session> getSessions(Integer limit, Integer offset, Map<String, String> allParams) {
    Query query = new Query();
    query.limit(limit);
    query.skip(offset);

    // Optional query params
    //TODO: Implement other query params
    if (allParams.containsKey(QueryParams.HOST.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.HOST.getValue()).is(Long.parseLong(allParams.get(QueryParams.HOST.getValue()))));
    }
    if (allParams.containsKey(QueryParams.RECIPE.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.RECIPE.getValue()).is(Long.parseLong(allParams.get(QueryParams.RECIPE.getValue()))));
    }

    return mongoTemplate.find(query, Session.class);
  }

  public Session getSessionCredentials(Long sessionId, String accessToken) {
    accessToken = accessToken.substring(7);
    String username = jwtService.extractUsername(accessToken);

//    if (!checkIfUserIsParticipant(sessionId, username)) {
//      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to get the credentials of this session");
//    }

    return sessionRepository.getById(sessionId);
  }

  // ######################################### Util #########################################

  private Boolean checkIfUserIsParticipant(Long sessionId, String username) {
    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }
    return session.getParticipants().contains(userRepository.getByUsername(username).getId());
  }
}
