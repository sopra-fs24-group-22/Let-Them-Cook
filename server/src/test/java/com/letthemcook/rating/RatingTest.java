package com.letthemcook.rating;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingTest {
  private Rating rating;

  // ######################################### Setup & Teardown #########################################

  @BeforeEach
  public void setUp() {
   rating = new Rating();
  }

  // ######################################### addRating Tests #########################################

  @Test
  public void testAddValidRatingOnEmptyRating() {
    rating.addRating(3, 1L);

    HashMap<Long, Integer> expectedUserRatings = new HashMap<>();
    expectedUserRatings.put(1L, 3);

    assertEquals(3, rating.getAvgTotalRating());
    assertEquals(1, rating.getNrRatings());
    assertEquals(expectedUserRatings, rating.getUserRatings());
  }

  @Test
  public void testAddMultipleValidRatings() {
    rating.addRating(3, 1L);
    rating.addRating(4, 2L);
    rating.addRating(5, 3L);

    HashMap<Long, Integer> expectedUserRatings = new HashMap<>();
    expectedUserRatings.put(1L, 3);
    expectedUserRatings.put(2L, 4);
    expectedUserRatings.put(3L, 5);

    assertEquals(4, rating.getAvgTotalRating());
    assertEquals(3, rating.getNrRatings());
    assertEquals(expectedUserRatings, rating.getUserRatings());
  }

  @Test
  public void testAddInvalidRatingNumber() {
    try {
      rating.addRating(6, 1L);
    } catch (ResponseStatusException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
  }

  @Test
  public void testAddInvalidRatingNumberNegative() {
    try {
      rating.addRating(-1, 1L);
    } catch (ResponseStatusException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }
  }

  @Test
  public void testAddRatingSameUser() {
rating.addRating(3, 1L);
    rating.addRating(4, 1L);

    HashMap<Long, Integer> expectedUserRatings = new HashMap<>();
    expectedUserRatings.put(1L, 4);

    assertEquals(4, rating.getAvgTotalRating());
    assertEquals(1, rating.getNrRatings());
    assertEquals(expectedUserRatings, rating.getUserRatings());
  }
}
