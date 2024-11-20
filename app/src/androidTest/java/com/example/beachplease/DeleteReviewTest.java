package com.example.beachplease;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DeleteReviewTest {

    //without fb
    private List<Review> reviewList;

    @Before
    public void setUp() {
        // Initialize an in-memory list to simulate a database
        reviewList = new ArrayList<>();

        // Add some initial reviews to the list
        reviewList.add(new Review("Venice Beach", 3.0, new Date(), "BeachPleaseUser1", "Great beach!", new ArrayList<>()));
        reviewList.add(new Review("Santa Monica Beach", 4.0, new Date(), "BeachPleaseUser2", "Loved it!", new ArrayList<>()));
    }

    @Test
    public void testDeleteReview() {
        // Initial size of the list
        int initialSize = reviewList.size();

        // Find and delete a review (by beachName)
        String beachNameToDelete = "Venice Beach";
        Review reviewToDelete = null;

        for (Review review : reviewList) {
            if (review.getBeachName().equals(beachNameToDelete)) {
                reviewToDelete = review;
                break;
            }
        }

        // Ensure the review to delete is found
        assertTrue("Review to delete should exist", reviewToDelete != null);

        // Delete the review
        reviewList.remove(reviewToDelete);

        // Verify the review is deleted
        assertEquals("List size should decrease by 1", initialSize - 1, reviewList.size());
        assertTrue("Deleted review should not exist in the list",
                reviewList.stream().noneMatch(review -> review.getBeachName().equals(beachNameToDelete)));
    }
}
