package com.letthemcook.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

public class SessionUserStateTest {
  private SessionUserState sessionUserState;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setup() {
    sessionUserState = new SessionUserState();
    sessionUserState.setSessionId(1L);
    sessionUserState.setRecipeSteps(5);
    sessionUserState.setCurrentStepValues(new HashMap<>());
    sessionUserState.setLastActiveUsers(new HashMap<>());
  }

  // ######################################### addUserToStepCount Tests #########################################

  @Test
  public void testAddUserToStepCountSuccess() {
    sessionUserState.addUserToStepCount(1L);

    assert(sessionUserState.getCurrentStepValues().containsKey(1L));
    assert(sessionUserState.getLastActiveUsers().containsKey(1L));
    assert(sessionUserState.getCurrentStepValues().get(1L).length == 5);
  }

  @Test
  public void testAddUserToStepCountDuplicateUser() {
    sessionUserState.addUserToStepCount(1L);
    sessionUserState.addUserToStepCount(1L);

    assert(sessionUserState.getCurrentStepValues().containsKey(1L));
    assert(sessionUserState.getLastActiveUsers().containsKey(1L));
    assert(sessionUserState.getCurrentStepValues().get(1L).length == 5);

    assert(sessionUserState.getCurrentStepValues().size() == 1);
    assert(sessionUserState.getLastActiveUsers().size() == 1);
  }

  @Test
  public void testAddUserToStepCountDuplicateUserSameStepCount() {
    sessionUserState.addUserToStepCount(1L);

    // Setup checked Steps
    HashMap<Long, Boolean[]> currentStepValues = sessionUserState.getCurrentStepValues();
    Boolean[] userSteps = currentStepValues.get(1L);
    userSteps[0] = true;
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    sessionUserState.addUserToStepCount(1L);

    assert(sessionUserState.getCurrentStepValues().containsKey(1L));
    assert(sessionUserState.getLastActiveUsers().containsKey(1L));
    assert(sessionUserState.getCurrentStepValues().get(1L).length == 5);

    assertEquals(userSteps, sessionUserState.getCurrentStepValues().get(1L));
  }

  // ######################################### removeUserToStepCount Tests #########################################

  @Test
  public void testRemoveUserFromStepCountSuccess() {
    // Setup currentStepValues
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    Boolean[] userSteps = new Boolean[5];
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    // Perform test
    sessionUserState.removeUserFromStepCount(1L);

    assert(!sessionUserState.getCurrentStepValues().containsKey(1L));
    assert(!sessionUserState.getLastActiveUsers().containsKey(1L));
  }

  @Test
  public void testRemoveUserFromStepCountNonExistentUser() {
    sessionUserState.removeUserFromStepCount(1L);

    assert(!sessionUserState.getCurrentStepValues().containsKey(1L));
    assert(!sessionUserState.getLastActiveUsers().containsKey(1L));
  }

  // ######################################### updateCheckPoint Tests #########################################

  @Test
  public void testUpdateCheckpointCheckedSuccess() {
    // Setup currentStepValues
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    Boolean[] userSteps = new Boolean[5];
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    // Perform test
    sessionUserState.updateCheckpoint(1L, 0, true);
    userSteps[0] = true;

    assertEquals(sessionUserState.getCurrentStepValues().get(1L), userSteps);
  }

  @Test
  public void testUpdateCheckpointUncheckedSuccess() {
    // Setup currentStepValues
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    Boolean[] userSteps = new Boolean[5];
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    // Perform test
    sessionUserState.updateCheckpoint(1L, 0, true);
    sessionUserState.updateCheckpoint(1L, 0, false);

    assertEquals(sessionUserState.getCurrentStepValues().get(1L), userSteps);
  }

  @Test
  public void testUpdateCheckpointOutOfBounds() {
    // Setup currentStepValues
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    Boolean[] userSteps = new Boolean[5];
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    // Perform test
    try {
      sessionUserState.updateCheckpoint(1L, 5, true);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "Step index is out of bounds");
    }
  }

  @Test
  public void testUpdateCheckpointStepIndexNegative() {
    // Setup currentStepValues
    HashMap<Long, Boolean[]> currentStepValues = new HashMap<>();
    Boolean[] userSteps = new Boolean[5];
    currentStepValues.put(1L, userSteps);
    sessionUserState.setCurrentStepValues(currentStepValues);

    // Perform test
    try {
      sessionUserState.updateCheckpoint(1L, -1, true);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "Step index is out of bounds");
    }
  }


}
