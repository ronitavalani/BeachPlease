package com.example.beachplease;

import androidx.test.core.app.ActivityScenario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;


public class DeleteReviewTest {
    @Test
    public void testDeleteReview() {
        // Firebase setup
        String databaseUrl = "https://<your-database-url>.firebaseio.com/"; // Replace with your actual Firebase URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference reviewsRef = firebaseDatabase.getReference("reviews");

        // Step 1: Create a review
        String beachName = "Venice Beach";
        Double rating = 4.5;
        Date date = new Date();
        String author = "Test Author";
        String comment = "Wonderful experience!";
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Family-Friendly");
        tags.add("Pet-Friendly");

        Review review = new Review(beachName, rating, date, author, comment, tags);

        // Push the review to Firebase
        String reviewId = reviewsRef.push().getKey(); // Generate a unique ID
        assertNotNull("Review ID should not be null", reviewId);

        reviewsRef.child(reviewId).setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Review added successfully to Firebase.");

                // Step 2: Delete the review
                reviewsRef.child(reviewId).removeValue().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        System.out.println("Review deleted successfully from Firebase.");

                        // Step 3: Verify the review no longer exists
                        reviewsRef.child(reviewId).get().addOnCompleteListener(retrieveTask -> {
                            if (retrieveTask.isSuccessful() && !retrieveTask.getResult().exists()) {
                                System.out.println("Test passed: Review no longer exists in Firebase.");
                                assertNull("Review should no longer exist in Firebase", retrieveTask.getResult().getValue());
                            } else {
                                System.err.println("Test failed: Review still exists in Firebase.");
                            }
                        });
                    } else {
                        System.err.println("Failed to delete the review from Firebase.");
                    }
                });
            } else {
                System.err.println("Failed to add the review to Firebase.");
            }
        });
    }



    //without fb
    private List<Review> reviewList;

    @Before
    public void setUp() {
        // Initialize an in-memory list to simulate a database
        reviewList = new ArrayList<>();

        // Add some initial reviews to the list
        reviewList.add(new Review("Venice Beach", 4.5, new Date(), "Author1", "Great beach!", new ArrayList<>()));
        reviewList.add(new Review("Santa Monica Beach", 4.0, new Date(), "Author2", "Loved it!", new ArrayList<>()));
    }

    @Test
    public void testDeleteReview() {
        // Initial size of the list
        int initialSize = reviewList.size();

        // Find and delete a review (for example, by beachName)
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
