package com.example.beachplease;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReviewGetterSetterTest {

    private Review review;

    @Before
    public void setUp() {

        review = new Review();
    }

    @Test
    public void testGetterSetter_beachName() {

        //create a beach name
        String expectedBeachName = "Malibu Beach";
        review.setBeachName(expectedBeachName);


        //use getter to make sure it works
        String actualBeachName = review.getBeachName();


        //check to make sure the two r the same
        assertEquals("Getter or Setter for beachName failed", expectedBeachName, actualBeachName);
    }

    @Test
    public void testGetterSetter_rating() {

        //set value for a rating
        double expectedRating = 4.5;
        review.setRating(expectedRating);


        //use getter to get it
        double actualRating = review.getRating();


        //make sure it is the same value
        assertEquals("Getter or Setter for rating failed", expectedRating, actualRating, 0.0);
    }

    //function tests the same for comment
    @Test
    public void testGetterSetter_comment() {

        String expectedComment = "Beautiful beach with great waves!";
        review.setComment(expectedComment);


        String actualComment = review.getComment();


        assertEquals("Getter or Setter for comment failed", expectedComment, actualComment);
    }
}

