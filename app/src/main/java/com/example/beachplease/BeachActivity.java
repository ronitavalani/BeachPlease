package com.example.beachplease;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Date;
import java.util.Locale;

public class BeachActivity extends AppCompatActivity {
    private Beach beach;
    private User user;
    private TextView liveWeatherInfo;
    private ImageView beachImage;
    private TextView beachName, beachBlurb, beachHours, weatherInfo, reviewInfo, exampleReview;
    private LinearLayout forecastLayout;
    private LinearLayout tagLayout;
    private static final String API_KEY = "60656159d401dedb2ab28b487e8bd931";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);
        Beach selectedBeach = getIntent().getParcelableExtra("selectedBeach");

        ImageButton mapTab = findViewById(R.id.mapTab);
        ImageButton profileTab = findViewById(R.id.profileTab);

        // Handle Map Tab click
        mapTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(BeachActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: close BeachActivity if returning to MainActivity
            }
        });

        // Handle Profile Tab click
        profileTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(BeachActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Initialize views
        beachImage = findViewById(R.id.beachImage);
        beachName = findViewById(R.id.beachName);
        beachBlurb = findViewById(R.id.beachBlurb);
        beachHours = findViewById(R.id.beachTimes);
        weatherInfo = findViewById(R.id.weatherInfo);
        reviewInfo = findViewById(R.id.review);
        exampleReview = findViewById(R.id.exampleReview);
        forecastLayout = findViewById(R.id.forecastLayout);
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
      
        liveWeatherInfo = findViewById(R.id.weatherInfo);

        fetchWeatherData(selectedBeach.getLatitude(), selectedBeach.getLongitude());

        Button addReviewButton = findViewById(R.id.addReview);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReviewDialog(selectedBeach);
            }
        });
    }

    public void fetchWeatherData (double latitude, double longitude){
        new Thread(() ->{
            try{
                //bc this is  long can make a separate function
                String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                        latitude +"&lon="+longitude+"&appid="+API_KEY+"&units=imperial";
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection =( HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String input;
                while  ((input = in.readLine()) !=null){
                    content.append(input);
                }
                in.close();
                urlConnection.disconnect();


                JSONObject response = new JSONObject(content.toString());
                JSONObject main = response.getJSONObject("main");
                double temp = main.getDouble("temp");
                int humidity = main.getInt("humidity");
                JSONArray weatherInfoArray = response.getJSONArray("weather");
                String weatherCondition = weatherInfoArray.getJSONObject(0).getString("description");

                String formattedWeatherInfo = "Temperature: " + temp + "°F\n" + "Humidity: " + humidity + "%\n" + "Conditions: " + weatherCondition;

                //wave height
                String waveURL = "https://marine-api.open-meteo.com/v1/marine?latitude=" +
                        latitude + "&longitude=" + longitude + "&hourly=wave_height";
                url = new URL(waveURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                in = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                content = new StringBuilder();
                while ((input = in.readLine()) != null) {
                    content.append(input);
                }
                in.close();
                urlConnection.disconnect();

                JSONObject waveResponse = new JSONObject(content.toString());
                JSONArray waveHeights = waveResponse.getJSONObject("hourly").getJSONArray("wave_height");
                double waveHeight = waveHeights.getDouble(0);
                final String finalInfo = formattedWeatherInfo + "\nWave Height: " + waveHeight + " meters";
                runOnUiThread(() -> liveWeatherInfo.setText(finalInfo));

                //FOR WEATHER FORECAST
                String forecastURL = "https://api.openweathermap.org/data/2.5/forecast?lat=" +
                        latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=imperial";
                url = new URL(forecastURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                content = new StringBuilder();
                while ((input = in.readLine()) != null) {
                    content.append(input);
                }
                in.close();
                urlConnection.disconnect();

                JSONObject forecastResponse = new JSONObject(content.toString());
                JSONArray forecastList = forecastResponse.getJSONArray("list");
                runOnUiThread(() -> forecastLayout.removeAllViews());

                //display next 8 increments
                SimpleDateFormat dateFormat = new SimpleDateFormat("h a", Locale.getDefault());
                for (int i = 0; i < 8 && i < forecastList.length(); i++) {
                    JSONObject forecast = forecastList.getJSONObject(i);
                    JSONObject mainData = forecast.getJSONObject("main");
                    double forecastTemp = mainData.getDouble("temp");

                    long forecastTime = forecast.getLong("dt") * 1000;
                    String formatTime = dateFormat.format(new Date(forecastTime));
                    runOnUiThread(() -> {
                        //container
                        LinearLayout forecastItem = new LinearLayout(this);
                        forecastItem.setOrientation(LinearLayout.VERTICAL);
                        forecastItem.setPadding(16, 8, 16,8);

                        //display time
                        TextView timeTextView = new TextView(this);
                        timeTextView.setText(formatTime);
                        timeTextView.setTextSize(14);
                        timeTextView.setTextColor(getResources().getColor(android.R.color.black));
                        forecastItem.addView(timeTextView);

                        //display temperature
                        TextView tempTextView = new TextView(this);
                        tempTextView.setText(String.format(Locale.getDefault(), "%.0f°F", forecastTemp));
                        tempTextView.setTextSize(16);
                        tempTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                        forecastItem.addView(tempTextView);

                        //add forecast item to the horizontal layout
                        forecastLayout.addView(forecastItem);
                    });
                }
            }
            catch (Exception e){
             Log.e("WeatherAPI", "Error displaying weather data", e);
            }
        } ).start();
    }

    public void displayBeachInfo() {

    }

    public void displayReviews() {

    }

    private void showAddReviewDialog(Beach selectedBeach) {
        // Tags list
        final String[] tags = {
                "Surfing", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
                "Beach Sports", "Shaded Areas", "Hiking Trails Nearby", "Nearby Food Vendors", "Bonfire-Friendly",
                "Scenic Views"
        };
        final ArrayList<String> selectedTags = new ArrayList<>();

        // Placeholder for author
        final User author = new User("reneepan", "reneepan", "reneepan", "reneepan"); // Assuming a User class constructor

        // Current date
        final Date date = Calendar.getInstance().getTime();

        // Create a dialog with input fields
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Review for " + selectedBeach.getName());

        // Create a ScrollView to hold the form content
        ScrollView scrollView = new ScrollView(this);

        // Create a LinearLayout to hold the input fields within the ScrollView
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Input for review text
        final EditText reviewInput = new EditText(this);
        reviewInput.setHint("Write your review...");
        layout.addView(reviewInput);

        // Rating input
        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        layout.addView(ratingBar);

        // Create a GridLayout for the tags to display them in two columns
        GridLayout tagLayout = new GridLayout(this);
        tagLayout.setColumnCount(2);
        tagLayout.setPadding(10, 10, 10, 10);

        // Add each tag as a CheckBox to the GridLayout
        for (String tag : tags) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tag);
            checkBox.setTextSize(12); // Smaller text size for tags
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
            });
            tagLayout.addView(checkBox);
        }

        // Add the GridLayout to the main layout
        layout.addView(tagLayout);

        // Add the layout to the ScrollView
        scrollView.addView(layout);

        // Set the ScrollView as the dialog view
        builder.setView(scrollView);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reviewText = reviewInput.getText().toString();
                Double rating = (double) ratingBar.getRating();

                if (!reviewText.isEmpty() && rating > 0) {
                    // Create a new review object
                    Review newReview = new Review(
                            selectedBeach.getName(),
                            rating,
                            date,
                            author,
                            reviewText,
                            new ArrayList<>(selectedTags)
                    );

                    // Display confirmation and handle saving/displaying the review
                    Toast.makeText(BeachActivity.this, "Review added!", Toast.LENGTH_SHORT).show();

                    // Optionally update UI to reflect new review
                    // updateReviewsUI(newReview);
                } else {
                    Toast.makeText(BeachActivity.this, "Please complete all review fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
