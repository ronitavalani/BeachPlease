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
