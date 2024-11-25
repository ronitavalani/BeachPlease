package com.example.beachplease;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AddTagTest {

    private Review review;

    @Before
    public void setUp() {
        review = new Review(
                "Test Beach",
                4.5,
                new java.util.Date(),
                "testUser",
                "Great place to visit!",
                new ArrayList<>()
        );
    }

    @Test
    public void testAddTagToReview() {
        String newTag = "Family Friendly";
        review.getTags().add(newTag);

        assertTrue("Tag list should contain the added tag", review.getTags().contains(newTag));
    }
}
