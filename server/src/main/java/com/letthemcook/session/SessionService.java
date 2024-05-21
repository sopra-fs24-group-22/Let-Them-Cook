package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.recipe.Recipe;
import com.letthemcook.recipe.RecipeRepository;
import com.letthemcook.sessionrequest.SessionRequestService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.springframework.util.ClassUtils.getMethod;

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
    session.setHostName(username);
    session.setParticipants(new ArrayList<>());
    session.setCurrentParticipantCount(0);

    String roomID = videoSDKService.fetchRoomId();
    session.setRoomId(roomID);

    // Initialize checklistCount
    SessionUserState sessionUserState = new SessionUserState();

    sessionUserState.setSessionId(session.getId());
    Recipe recipe = recipeRepository.getById(session.getRecipeId());
    session.setRecipeName(recipe.getTitle());
    sessionUserState.setRecipeSteps(recipe.getChecklist().size());
    sessionUserState.setCurrentStepValues(new HashMap<>());
    sessionUserState.setLastActiveUsers(new HashMap<>());

    session.setSessionUserState(sessionUserState);

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

  public void updateSession(Session session, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    Long sessionId = session.getId();

    // Check if session exists
    Session existingSession = sessionRepository.getById(sessionId);
    if (existingSession == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    // Check if user is authorized to update session
    if (!Objects.equals(existingSession.getHostId(), userRepository.getByUsername(username).getId())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to update this session");
    }

    // Set recipe data
    session = updateSessionData(existingSession, session);

    sessionRepository.save(session);
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
    for (Map.Entry<String, String> param : allParams.entrySet()) {
      if (Stream.of(QueryParams.values()).anyMatch(e -> e.getValue().equals(param.getKey()))) {
        if (param.getKey().toUpperCase().contains("NAME")) {
          query.addCriteria(Criteria.where(param.getKey()).regex(".*" + param.getValue() + ".*", "i"));
        }

        else if (param.getKey().toUpperCase().contains("ID")) {
          query.addCriteria(Criteria.where(param.getKey()).is(Long.parseLong(param.getValue())));
        }

        else if ( param.getKey().toUpperCase().contains("MAX")) {
          query.addCriteria(Criteria.where(param.getKey()).lte(param.getValue()));
        }

        else if (param.getKey().toUpperCase().contains("MIN")) {
          query.addCriteria(Criteria.where(param.getKey()).gte(Integer.parseInt(param.getValue())));
        }

        else if (param.getKey().toUpperCase().contains("DATE")) {
          LocalDateTime date = LocalDateTime.parse(param.getValue());

          query.addCriteria(Criteria.where(param.getKey()).gte(date));
        }

      }
    }

    // Filter by current date if no date is provided
    if (!allParams.containsKey(QueryParams.DATE.getValue())) {
      query.addCriteria(Criteria.where(QueryParams.DATE.getValue()).gte(new Date()));
    }

    // Sort by date
    query.with(org.springframework.data.domain.Sort.by(QueryParams.DATE.getValue()).ascending());

    return mongoTemplate.find(query, Session.class);
  }

  public Session getSessionCredentials(Long sessionId, String accessToken) {
    String username = jwtService.extractUsername(accessToken);
    User user = userRepository.getByUsername(username);
    Long userId = user.getId();
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    Long hostId = session.getHostId();
    if (!Objects.equals(hostId, userId)) {
      return session;
    }
    ArrayList<Long> participants = session.getParticipants();
    participants.add(userId);
    session.setParticipants(participants);
    sessionRepository.save(session);

    return session;
  }

  // TODO: Implement a Leave session endpoint to decrement currentParticipantCount

  public Session checkStep(Long sessionId, Integer stepIndex, Boolean isChecked, String accessToken) {
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    // Authorize user against session
    if (!checkIfUserIsParticipant(sessionId, jwtService.extractUsername(accessToken))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed in this session");
    }

    // Update sessionUserState with new checklist state
    SessionUserState sessionUserState = session.getSessionUserState();
    sessionUserState.updateCheckpoint(userRepository.getByUsername(jwtService.extractUsername(accessToken)).getId(), stepIndex, isChecked);
    session.setSessionUserState(sessionUserState);

    sessionRepository.save(session);

    return session;
  }

  public SessionUserState getSessionUserState(Long sessionId, String accessToken) {
    Session session = sessionRepository.getById(sessionId);

    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    // Authorize user against session
    String username = jwtService.extractUsername(accessToken);
    if (!checkIfUserIsParticipant(sessionId, username)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed in this session");
    }

    SessionUserState sessionUserState = session.getSessionUserState();

    // Add user to step count if they are not already in it
    Long userId = userRepository.getByUsername(username).getId();

    if (sessionUserState.getCurrentStepValues().get(userId) == null) {
      sessionUserState.addUserToStepCount(userId);
    } else {
      sessionUserState.updateUserActivity(userId);
    }

    session.setSessionUserState(sessionUserState);
    sessionRepository.save(session);

    return session.getSessionUserState();
  }

  public List<Session> getSessionsByUser(String accessToken) {
    // TODO: Add pending requests
    // TODO: Write tests
    String username = jwtService.extractUsername(accessToken);

    List<Session> sessions = sessionRepository.findAll();
    List<Session> userSessions = new ArrayList<>();

    for (Session session : sessions) {
      if (Objects.equals(session.getHostId(), userRepository.getByUsername(username).getId()) || session.getParticipants().contains(userRepository.getByUsername(username).getId())) {
        userSessions.add(session);
      }
    }

    return userSessions;
  }

  public List<Session> getOpenSessions() {
    List<Session> sessions = sessionRepository.findAllByDateAfter(LocalDateTime.now());
    List<Session> openSessions = new ArrayList<>();

    // Find open sessions, i.e. sessions that don't have a full participant count
    for (Session session : sessions) {
      if (session.getCurrentParticipantCount() < session.getMaxParticipantCount()) {
        openSessions.add(session);
      }
    }

    return openSessions;
  }

  // ######################################### Util #########################################

  private Boolean checkIfUserIsParticipant(Long sessionId, String username) {
    Session session = sessionRepository.getById(sessionId);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
    }

    ArrayList<Long> participants = session.getParticipants();

    if (participants == null & !Objects.equals(session.getHostId(), userRepository.getByUsername(username).getId())) {
      return false;
    }

    boolean isParticipant = false;
    isParticipant = participants.contains(userRepository.getByUsername(username).getId()) || Objects.equals(session.getHostId(), userRepository.getByUsername(username).getId());

    return isParticipant;
  }

  /**
   * Removes user from step count if they have been inactive for 60 seconds
   */
  @Scheduled(fixedRate = 60000)
  private void userTimeout() {
    // TODO: Write Tests
    List<Session> sessions = sessionRepository.findAll();

    // Go through all sessions, check if participants have been active
    for (Session session : sessions) {
      if (session.getParticipants() != null) {
        SessionUserState sessionUserState = session.getSessionUserState();

        ArrayList<Long> participants = session.getParticipants();
        for (Long participantId : participants) {
          Date lastActive = sessionUserState.getLastActiveUsers().get(participantId);

          if(lastActive != null) {
            if (new Date().getTime() - lastActive.getTime() > 60000) {
              participants.remove(participantId);
              sessionUserState.removeUserFromStepCount(participantId);

              session.setParticipants(participants);
              session.setSessionUserState(sessionUserState);
              session.setCurrentParticipantCount(session.getCurrentParticipantCount() - 1);

              sessionRepository.save(session);
            }
          }
        }
      }
    }
  }

  /**
   * Removes session from repository after its completion
   */
  @Scheduled(fixedRate = 1800000)
  private void deleteSessions() {
    // TODO: Write tests
    List<Session> sessions = sessionRepository.findAll();

    for (Session session : sessions) {
      if (LocalDateTime.now().isAfter(session.getDate().plusMinutes(session.getDuration()).plusHours(12L))) {
        sessionRepository.deleteById(session.getId());
      }
    }
  }

  // ######################################### Util #########################################

  public void deleteSessionByUser(Session session) {
    sessionRepository.delete(session);
  }

  private Session updateSessionData(Session existingSession, Session session) {

    Method[] getters = {
            getMethod(Session.class, "getHostName"),
            getMethod(Session.class, "getRecipeId"),
            getMethod(Session.class, "getRecipeName"),
            getMethod(Session.class, "getSessionName"),
            getMethod(Session.class, "getMaxParticipantCount"),
            getMethod(Session.class, "getParticipants"),
            getMethod(Session.class, "getDate"),
            getMethod(Session.class, "getDuration")

    };

    Method[] setters = {
            getMethod(Session.class, "setHostName", String.class),
            getMethod(Session.class, "setRecipeId", Long.class),
            getMethod(Session.class, "setRecipeName", String.class),
            getMethod(Session.class, "setSessionName", String.class),
            getMethod(Session.class, "setMaxParticipantCount", Integer.class),
            getMethod(Session.class, "setParticipants", ArrayList.class),
            getMethod(Session.class, "setDate", LocalDateTime.class),
            getMethod(Session.class, "setDuration", Integer.class)


    };

    for (int i = 0; i < getters.length; i++) {
      try {
        Object value = getters[i].invoke(session);
        if (value != null) {
          setters[i].invoke(existingSession, value);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return existingSession;
  }

  public void updateSessionHostName(Long userId, String username) {
    for (Session session : sessionRepository.getByHostId(userId)) {
      session.setHostName(username);
      sessionRepository.save(session);
    }
  }
}