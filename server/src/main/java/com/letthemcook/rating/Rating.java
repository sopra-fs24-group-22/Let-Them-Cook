package com.letthemcook.rating;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

public class Rating {
  private Float avgTotalRating = 0F;
  private Integer nrRatings = 0;
  private HashMap<Long, Integer> userRatings = new HashMap<>();

  public Float getAvgTotalRating() {
    return this.avgTotalRating;
  }

  public Integer getNrRatings() {
    return this.nrRatings;
  }

  public HashMap<Long, Integer> getUserRatings() {
    return this.userRatings;
  }

  public void addRating(Integer newRating, Long userId) {
    // Only allow ratings from 0 to 5 stars
    if(newRating < 0 || newRating > 5) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use a rating between 0 and 5");
    }

    // If user has already submitted a rating, replace with new rating
    if(userRatings.containsKey(userId)) {
      Integer oldUserRating = userRatings.get(userId);

      avgTotalRating = (avgTotalRating * nrRatings - oldUserRating + newRating) / nrRatings;
      userRatings.replace(userId, newRating);
    } else {
      avgTotalRating = (avgTotalRating * nrRatings + newRating) / (nrRatings + 1);
      nrRatings = this.nrRatings + 1;
      userRatings.put(userId, newRating);
    }
  }
}
