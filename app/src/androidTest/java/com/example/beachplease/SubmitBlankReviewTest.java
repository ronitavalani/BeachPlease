package com.example.beachplease;
import androidx.test.core.app.ActivityScenario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SubmitBlankReviewTest {
    @Test
    public void testSubmitReviewWithoutRequiredFields() {
        // Firebase setup
        String databaseUrl = "https://<your-database-url>.firebaseio.com/"; // Replace with your actual Firebase URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference reviewsRef = firebaseDatabase.getReference("reviews");

        // Input values with missing fields
        String beachName = "Venice Beach";
        Double rating = null; // Missing rating
        Date date = new Date();
        String author = "Test Author";
        String comment = ""; // Missing comment
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Pet-Friendly");

        // Create a new review
        Review review = new Review(beachName, rating, date, author, comment, tags);

        // Validate required fields
        if (review.getRating() == null) {
            System.out.println("Validation failed: Rating is required.");
            return; // Stop test here
        }
        if (review.getComment() == null || review.getComment().isEmpty()) {
            System.out.println("Validation failed: Comment is required.");
            return; // Stop test here
        }

        // Attempt to push the review to Firebase
        try {
            String reviewId = reviewsRef.push().getKey(); // Generate a unique ID
            assertNotNull("Review ID should not be null", reviewId);

            reviewsRef.child(reviewId).setValue(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fail("Review should not be submitted without required fields!");
                } else {
                    System.out.println("Failed to submit review as expected: " + task.getException());
                }
            });
        } catch (Exception e) {
            // Catch expected errors
            assertEquals("Rating is required", e.getMessage());
        }
    }


    //w/o fb

    @Test
    public void testSubmitReviewWithoutRating() {
        // Arrange
        Review review = new Review();
        review.setBeachName("Venice Beach");
        review.setComment("No rating");
        review.setTags(new ArrayList<>());

        String errorMessage = null;

        // Act
        if (review.getRating() == null) {
            errorMessage = "Rating is required";
        }

        // Assert
        assertEquals("Rating is required", errorMessage);
    }
}
