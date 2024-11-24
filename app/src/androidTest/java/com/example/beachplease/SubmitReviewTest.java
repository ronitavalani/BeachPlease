package com.example.beachplease;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubmitReviewTest {

    //w/o firebase
    private List<Review> reviewList;

    @Before
    public void setUp() {
        // Arrange
        reviewList = new ArrayList<>();
    }

    @Test
    public void testSubmitReview() {
        // Act
        Review newReview = new Review("Laguna Beach", 5.0, new Date(), "BeachPleaseUser", "Perfect!", new ArrayList<>());
        reviewList.add(newReview);

        // Assert
        assertEquals(1, reviewList.size()); // Verify the review list contains 1 review
        assertTrue(reviewList.stream().anyMatch(
                r -> r.getBeachName().equals("Laguna Beach") && r.getRating().equals(5.0))
        );
    }
}
