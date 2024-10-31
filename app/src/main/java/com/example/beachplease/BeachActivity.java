package com.example.beachplease;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
}
