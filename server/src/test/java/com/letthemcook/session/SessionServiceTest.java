package com.letthemcook.session;

import com.letthemcook.auth.config.JwtService;
import com.letthemcook.user.UserRepository;
import com.letthemcook.util.SequenceGeneratorService;
import com.letthemcook.videosdk.VideoSDKService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

public class SessionServiceTest {
  @Mock
  private SessionRepository sessionRepository;
  @Mock
  private SequenceGeneratorService sequenceGeneratorService;
  @Mock
  private JwtService jwtService;
  @Mock
  private UserRepository userRepository;
  MongoTemplate mongoTemplate = Mockito.spy(
          //Instance the MongoTemplate, use any test framework
          new MongoTemplate(new SimpleMongoClientDbFactory("mongodb://localhost/test"))
  );
  @Mock
  private VideoSDKService videoSDKService;

  @InjectMocks
  private SessionService sessionService;

  // ######################################### Setup & Teardown #########################################
  @BeforeEach
  public void setup() {
    sessionService = new SessionService(sessionRepository, sequenceGeneratorService, userRepository, jwtService, mongoTemplate, videoSDKService);
  }

  @AfterEach
  public void tearDown() {
  }

  // ######################################### Get Sessions Tests #########################################

/*  @Test
  public void testGetSessionsDefaultParamsSuccess() {
    // Setup test sessions
    ArrayList<Session> sessions = new ArrayList<>();

    for(int i = 0; i < 15; i++) {
      Session session = new Session();
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(3);
      session.setRecipeId(2L);
      session.setDate("2025-01-01");
      session.setHostId(1L);
      session.setParticipants(new ArrayList<>());
      session.setId((long) i);
      sessions.add(session);
    }

    // Setup Query
    Query query = new Query();
    query.limit(10);
    query.skip(0);

    // Mock sessionRepository
    when(mongoTemplate.find(query, Session.class)).thenReturn(sessions.subList(0, 10));

    // Test getSession
    List<Session> result = sessionService.getSessions(10, 0, new LinkedHashMap<>());

    // Verify result
    assertEquals(sessions.subList(0, 10), result);
  }

  @Test
  public void testGetSessionsSkip10ParamSuccess() {
    // Setup test sessions
    ArrayList<Session> sessions = new ArrayList<>();

    for(int i = 0; i < 15; i++) {
      Session session = new Session();
      session.setSessionName("Test Session");
      session.setMaxParticipantCount(3);
      session.setRecipeId(2L);
      session.setDate("2025-01-01");
      session.setHostId(1L);
      session.setParticipants(new ArrayList<>());
      session.setId((long) i);
      sessions.add(session);
    }

    // Setup Query
    Query query = new Query();
    query.limit(10);
    query.skip(10);

    // Mock sessionRepository
    when(mongoTemplate.find(query, Session.class)).thenReturn(sessions.subList(10, 15));

    // Test getSession
    List<Session> result = sessionService.getSessions(10, 10, new LinkedHashMap<>());

    // Verify result
    assertEquals(sessions.subList(10, 15), result);
  }*/

}
