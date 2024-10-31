package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class BeachActivity extends AppCompatActivity {

    private ImageView beachImage;
    private TextView beachName, beachBlurb, beachHours, weatherInfo, reviewInfo, exampleTag, exampleReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        // Retrieve the Beach object from the intent
        Beach selectedBeach = getIntent().getParcelableExtra("selectedBeach");

        // Initialize views
        beachImage = findViewById(R.id.beachImage);
        beachName = findViewById(R.id.beachName);
        beachBlurb = findViewById(R.id.beachBlurb);
        beachHours = findViewById(R.id.beachTimes);
        weatherInfo = findViewById(R.id.weatherInfo);
        reviewInfo = findViewById(R.id.review);
        exampleTag = findViewById(R.id.exampleTag);
        exampleReview = findViewById(R.id.exampleReview);

        // Populate UI with Beach data
        if (selectedBeach != null) {
            beachName.setText(selectedBeach.getName());
            beachBlurb.setText(selectedBeach.getBlurb());
            beachHours.setText("Hours: " + selectedBeach.getHours());
            reviewInfo.setText("Average Rating: " + (selectedBeach.getAvgRating() != null ? selectedBeach.getAvgRating() : "N/A"));
        }
    }

    public void displayBeachInfo() {

    }

    public void displayReviews() {

    }

    public void writeReview(String beachName, Double rating, Date date, User author, String comment) {
        Review newReview = new Review(beachName, rating, date, author, comment);
        //beach.updateAvgRating(rating);
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
