package com.example.beachplease;
import androidx.test.core.app.ActivityScenario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubmitReviewTest {
    @Test
    public void testSubmitReview() {
        // Firebase setup
        String databaseUrl = "https://<your-database-url>.firebaseio.com/"; // Replace with your actual Firebase URL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference reviewsRef = firebaseDatabase.getReference("reviews");

        // Input values for the review
        String beachName = "Laguna Beach";
        Double rating = 5.0;
        Date date = new Date();
        String author = "Test Author";
        String comment = "Perfect!";
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Family-Friendly");
        tags.add("Scenic Views");

        // Create a new review
        Review review = new Review(beachName, rating, date, author, comment, tags);

        // Push the review to Firebase
        String reviewId = reviewsRef.push().getKey(); // Generate a unique ID
        assertNotNull("Review ID should not be null", reviewId);

        reviewsRef.child(reviewId).setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Firebase submission was successful
                System.out.println("Review submitted successfully!");
            } else {
                // Handle Firebase submission failure
                System.err.println("Failed to submit review: " + task.getException());
            }
        });

        // Verify the fields of the review
        assertEquals("Laguna Beach", review.getBeachName());
        assertEquals(Double.valueOf(5.0), review.getRating());
        assertEquals("Test Author", review.getAuthor());
        assertEquals("Perfect!", review.getComment());
        assertNotNull("Date should not be null", review.getDate());
        assertEquals(2, review.getTags().size());
        assertEquals("Family-Friendly", review.getTags().get(0));
        assertEquals("Scenic Views", review.getTags().get(1));
    }

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
        Review newReview = new Review("Laguna Beach", 5.0, new Date(), "Author", "Perfect!", new ArrayList<>());
        reviewList.add(newReview);

        // Assert
        assertEquals(1, reviewList.size());
        assertTrue(reviewList.stream().anyMatch(r -> r.getBeachName().equals("Laguna Beach") && r.getRating() == 5.0));
    }
}
