package com.letthemcook.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:test_application.properties")
public class UserServiceIntegrationTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    // Setup users
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setPassword("testPassword");
    user.setEmail("testEmail");
    userRepository.save(user);

    user = new User();
    user.setId(2L);
    user.setUsername("User2");
    user.setPassword("testPassword2");
    user.setEmail("testEmail2");
    userRepository.save(user);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  // ######################################### Get Users Tests #########################################

  @Test
  public void testGetUsersNoParams() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();

    // Perform test
    List<User> users = userService.getUsers(limit, offset, queryParams);
    assertEquals(2, users.size());
  }

  @Test
  public void testGetUsersByUsername() {
    // Setup
    Integer limit = 10;
    Integer offset = 0;
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put("username", "testUser");

    // Perform test
    List<User> users = userService.getUsers(limit, offset, queryParams);
    assertEquals(1, users.size());
    assertEquals("testUser", users.get(0).getUsername());
  }
}
