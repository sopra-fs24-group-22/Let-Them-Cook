package com.letthemcook.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:test_application.properties")
public class SessionServiceIntegrationTest {
  @Autowired
  private SessionRepository sessionRepository;
  @Autowired
  private SessionService sessionService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    // Setup sessions
    Session session = new Session();
    session.setId(1L);
    session.setHostId(1L);
    session.setRecipeId(1L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("sessionName");
    session.setDate(LocalDateTime.now().plusDays(1));
    sessionRepository.save(session);

    session = new Session();
    session.setId(2L);
    session.setHostId(2L);
    session.setRecipeId(2L);
    session.setRoomId("roomId");
    session.setCurrentParticipantCount(0);
    session.setParticipants(new ArrayList<>());
    session.setMaxParticipantCount(2);
    session.setSessionName("testName");
    session.setDate(LocalDateTime.now().plusDays(5));
    sessionRepository.save(session);
  }

  @AfterEach
  public void tearDown() {
    sessionRepository.deleteAll();
  }

  // ######################################### Get Sessions Tests #########################################

  @Test
  public void testGetSessionsNoParams() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();

    // Perform test
    assertEquals(2, sessionService.getSessions(limit, offset, queryParams).size());
  }

  @Test
  public void testGetSessionsByDate() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put(QueryParams.DATE.getValue(), LocalDateTime.now().plusDays(2).toString());

    // Perform test
    assertEquals(1, sessionService.getSessions(limit, offset, queryParams).size());
  }

  @Test
  public void testGetSessionsByName() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put(QueryParams.SESSION_NAME.getValue(), "testName");

    // Perform test
    assertEquals(1, sessionService.getSessions(limit, offset, queryParams).size());
  }

  @Test
  public void testGetSessionsByDateAndName() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put(QueryParams.DATE.getValue(), LocalDateTime.now().plusDays(2).toString());
    queryParams.put(QueryParams.SESSION_NAME.getValue(), "testName");

    // Perform test
    assertEquals(1, sessionService.getSessions(limit, offset, queryParams).size());
  }

  @Test
  public void testGetSessionsNoMatchingSessionsReturnsEmpty() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put(QueryParams.DATE.getValue(), LocalDateTime.now().plusDays(10).toString());

    // Perform test
    assertEquals(0, sessionService.getSessions(limit, offset, queryParams).size());
  }
}
