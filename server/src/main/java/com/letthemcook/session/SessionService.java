package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.user.User;
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
import java.util.stream.Stream;

@Service
@Transactional
public class SessionService {
  private final SessionRepository sessionRepository;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final MongoTemplate mongoTemplate;
  private final VideoSDKService videoSDKService;

  @Autowired
  public SessionService(@Qualifier("sessionRepository") SessionRepository sessionRepository, SequenceGeneratorService sequenceGeneratorService, UserRepository userRepository, JwtService jwtService, RecipeRepository recipeRepository, MongoTemplate mongoTemplate, VideoSDKService videoSDKService) {
    this.sessionRepository = sessionRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.recipeRepository = recipeRepository;
    this.mongoTemplate = mongoTemplate;
    this.videoSDKService = videoSDKService;
  }

  public Session createSession(Session session, String accessToken) throws IOException {
    String username = jwtService.extractUsername(accessToken);

    session.setId(sequenceGeneratorService.getSequenceNumber(Session.SEQUENCE_NAME));
    session.setHostId(userRepository.getByUsername(username).getId());
    session.setParticipants(new ArrayList<>());
    session.setCurrentParticipantCount(0);

    String roomID = videoSDKService.fetchRoomId();
    session.setRoomId(roomID);

    // Initialize checklistCount
    HashMap<Long, Integer> checklistCount = new HashMap<>();
    Recipe recipe = recipeRepository.getById(session.getRecipeId());

    for (long i = 0; i < recipe.getChecklist().size(); i++) {
      checklistCount.put(i, 0);
    }

    session.setChecklistCount(checklistCount);

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
    String username = jwtService.extractUsername(accessToken);

    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    if (Objects.equals(session.getHostId(), userRepository.getByUsername(username).getId())) {
      sessionRepository.deleteById(sessionId);
      return;
    }

    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to delete this session");
  }

  public List<Session> getSessions(Integer limit, Integer offset, Map<String, String> allParams) {
    Query query = new Query();
    query.limit(limit);
    query.skip(offset);

    // Iterate over all query params and add them to the query depending on the type of param (ID, NAME, DATE, etc.)
    // TODO: Fix host and recipe names, dates, min / max.
    for (Map.Entry<String, String> param : allParams.entrySet()) {
      if (Stream.of(QueryParams.values()).anyMatch(e -> e.getValue().equals(param.getKey()))) {
        if (param.getKey().toUpperCase().contains("NAME")) {
          query.addCriteria(Criteria.where(param.getKey()).regex(".*" + param.getValue() + ".*", "i"));
        }

        else if (param.getKey().toUpperCase().contains("ID")) {
          query.addCriteria(Criteria.where(param.getKey()).is(Long.parseLong(param.getValue())));
        }

        else if (param.getKey().toUpperCase().contains("DATE") || param.getKey().toUpperCase().contains("MAX")) {
          query.addCriteria(Criteria.where(param.getKey()).lte(param.getValue()));
        }

        else if (param.getKey().toUpperCase().contains("MIN")) {
          query.addCriteria(Criteria.where(param.getKey()).gte(Integer.parseInt(param.getValue())));
        }
      }
    }

      return mongoTemplate.find(query, Session.class);

  }

  public Session getSessionCredentials(Long sessionId, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    User user = userRepository.getByUsername(username);
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    if (Objects.equals(session.getCurrentParticipantCount(), session.getMaxParticipantCount())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This session is full");
    }

    // Add user to participants
    ArrayList<Long> participants = session.getParticipants();
    participants.add(user.getId());
    session.setParticipants(participants);

    // Increment currentParticipantCount
    session.setCurrentParticipantCount(session.getCurrentParticipantCount() + 1);

//    When implementing Session Join requests
//    if (!checkIfUserIsParticipant(sessionId, username)) {
//      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to get the credentials of this session");
//    }

    sessionRepository.save(session);

    return sessionRepository.getById(sessionId);
  }

  // TODO: Implement a Leave session endpoint to decrement currentParticipantCount

  public void checkStep(Long sessionId, Long stepIndex, Boolean checked, String accessToken) {
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    // Authorize user against session
    if (!checkIfUserIsParticipant(sessionId, jwtService.extractUsername(accessToken))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed in this session");
    }

    HashMap<Long, Integer> checklistCount = session.getChecklistCount();

    // Update checklist count according to state
    if (checked) {
      checklistCount.put(stepIndex, checklistCount.get(stepIndex) + 1);
    } else {
      checklistCount.put(stepIndex, checklistCount.get(stepIndex) - 1);
    }

    session.setChecklistCount(checklistCount);
    sessionRepository.save(session);
  }

  public HashMap<Long, Integer> getChecklistCount(Long sessionId, String accessToken) {
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    // Authorize user against session
    if (!checkIfUserIsParticipant(sessionId, jwtService.extractUsername(accessToken))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed in this session");
    }

    return session.getChecklistCount();
  }

  // ######################################### Util #########################################

  private Boolean checkIfUserIsParticipant(Long sessionId, String username) {
    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    ArrayList<Long> participants = session.getParticipants();

    if (participants == null) {
      return false;
    }

    return participants.contains(userRepository.getByUsername(username).getId());
  }
}
