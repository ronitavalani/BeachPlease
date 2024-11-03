package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Date;

public class BeachActivity extends AppCompatActivity {

    private ImageView beachImage;
    private TextView beachName, beachBlurb, beachHours, weatherInfo, reviewInfo, exampleReview;
    private LinearLayout tagLayout;

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
        exampleReview = findViewById(R.id.exampleReview);
        tagLayout = findViewById(R.id.tagLayout);

        // Populate UI with Beach data
        if (selectedBeach != null) {
            beachName.setText(selectedBeach.getName());
            beachBlurb.setText(selectedBeach.getBlurb());
            beachHours.setText("Hours: " + selectedBeach.getHours());
            reviewInfo.setText("Average Rating: " + (selectedBeach.getAvgRating() != null ? selectedBeach.getAvgRating() : "N/A"));
            Glide.with(this).load(selectedBeach.getPicture()).into(beachImage);

            for (String tag : selectedBeach.getTags()) {
                TextView tagView = new TextView(this);
                tagView.setText(tag);
                tagView.setPadding(8, 4, 8, 4);
                tagView.setTextSize(15);
                tagView.setTextColor(getResources().getColor(R.color.black)); // Customize as needed
                tagLayout.addView(tagView);
            }
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
