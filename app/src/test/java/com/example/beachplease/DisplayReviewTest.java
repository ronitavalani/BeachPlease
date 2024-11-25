package com.example.beachplease;

import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DisplayReviewTest {

    private LinearLayout mockReviewsSection;

    @Before
    public void setUp() {

        mockReviewsSection = mock(LinearLayout.class);
    }

    @Test
    public void testDisplayReview_withValidReview() {

        //creating a fake review
        Review review = new Review();
        review.setBeachName("Sunny Beach");
        review.setComment("Great place to relax!");
        review.setRating(4.5);

        boolean result = displayReviewLogic(review, mockReviewsSection);
        //checks to make sure reviews r added
        assertTrue(result);
        verify(mockReviewsSection, Mockito.times(3)).addView(Mockito.any(TextView.class));
    }

    @Test
    public void testDisplayReview_withNullReview() {

        boolean result = displayReviewLogic(null, mockReviewsSection);
        //makes sure no new reviews are added
        assertFalse(result);
        verify(mockReviewsSection, Mockito.never()).addView(Mockito.any(TextView.class));
    }


    private boolean displayReviewLogic(Review review, LinearLayout reviewsSection) {
        if (review == null) {
            return false; // failure for null review
        }

        //creating a beach
        TextView beachNameView = mock(TextView.class);
        beachNameView.setText("Beach Name: " + review.getBeachName());
        reviewsSection.addView(beachNameView);
        //creating a review
        TextView commentView = mock(TextView.class);
        commentView.setText("Comment: " + review.getComment());
        reviewsSection.addView(commentView);
        //creating a numerical rating
        TextView ratingView = mock(TextView.class);
        ratingView.setText("Rating: " + review.getRating());
        reviewsSection.addView(ratingView);
        //if review can be seen
        return true;
    }
}
