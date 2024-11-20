package com.example.beachplease;
import androidx.test.core.app.ActivityScenario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EditReviewTest {
    @Test
    public void testEditReview() {
        // Firebase setup
        String databaseUrl = "https://<your-database-url>.firebaseio.com/"; // Replace with your actual Firebase URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference reviewsRef = firebaseDatabase.getReference("reviews");

        // Step 1: Create a review
        String beachName = "Laguna Beach";
        Double originalRating = 4.0;
        Date date = new Date();
        String author = "Test Author";
        String originalComment = "Great beach!";
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Family-Friendly");

        Review originalReview = new Review(beachName, originalRating, date, author, originalComment, tags);

        // Push the original review to Firebase
        String reviewId = reviewsRef.push().getKey(); // Generate a unique ID
        assertNotNull("Review ID should not be null", reviewId);

        reviewsRef.child(reviewId).setValue(originalReview).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Original review added successfully to Firebase.");

                // Step 2: Update the review
                Double updatedRating = 5.0;
                String updatedComment = "Absolutely amazing beach!";

                // Update the local review object
                originalReview.setRating(updatedRating);
                originalReview.setComment(updatedComment);

                // Push the updated review to Firebase
                reviewsRef.child(reviewId).setValue(originalReview).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        System.out.println("Review updated successfully in Firebase.");

                        // Step 3: Retrieve the updated review and verify the changes
                        reviewsRef.child(reviewId).get().addOnCompleteListener(retrieveTask -> {
                            if (retrieveTask.isSuccessful() && retrieveTask.getResult().exists()) {
                                Review updatedReview = retrieveTask.getResult().getValue(Review.class);

                                // Assert the updated values
                                assertNotNull("Updated review should not be null", updatedReview);
                                assertEquals("Rating should match the updated value", updatedRating, updatedReview.getRating());
                                assertEquals("Comment should match the updated value", updatedComment, updatedReview.getComment());

                                System.out.println("Test passed: Updated review reflects the new values.");
                            } else {
                                System.err.println("Failed to retrieve the updated review from Firebase.");
                            }
                        });
                    } else {
                        System.err.println("Failed to update the review in Firebase.");
                    }
                });
            } else {
                System.err.println("Failed to add the original review to Firebase.");
            }
        });
    }


    //w/o firebase
    @Before
    public void setUp() {
        // Arrange
        review = new Review("Laguna Beach", 4.0, new Date(), "Author", "Good beach!", new ArrayList<>());
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
