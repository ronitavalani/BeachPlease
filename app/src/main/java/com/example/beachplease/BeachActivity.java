package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;

import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class BeachActivity extends AppCompatActivity {
    private Beach beach;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        Intent intent = getIntent();
    }

    public void displayBeachInfo() {

    }

    public void displayReviews() {

    }

    public void writeReview (String beachName, Double rating, Date date, User author, String comment) {
        Review newReview = new Review(beachName, rating, date, author, comment);
        beach.updateAvgRating(rating);
        //beach.getReviews().add(newReview);
    }

    //should this even be an option or should it just refresh the map?
    public void mapClick(android.view.View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileClick(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
