package com.example.beachplease;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;



public class EditReviewTest {
    private Review review;

    //w/o firebase
    @Before
    public void setUp() {
        // Arrange
        review = new Review("Laguna Beach", 4.0, new Date(), "BeachPleaseUser", "Good beach!", new ArrayList<>());
    }

    @Test
    public void testEditReview() {
        // Act
        review.setRating(5.0);
        review.setComment("Perfect beach!");

        // Assert
        assertEquals(5.0, review.getRating(), 0.0);
        assertEquals("Perfect beach!", review.getComment());
    }
}
