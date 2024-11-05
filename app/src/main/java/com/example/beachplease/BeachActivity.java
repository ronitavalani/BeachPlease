package com.example.beachplease;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import android.widget.RatingBar;

public class BeachActivity extends AppCompatActivity {
    private ImageView beachImage;
    private TextView beachName, beachBlurb, beachHours, liveWeatherInfo, reviewInfo, exampleReview;
    private LinearLayout tagLayout, reviewContainer;
    private LinearLayout forecastLayout;
    private RatingBar avgRatingBar; // RatingBar for average rating
    private static final String API_KEY = "60656159d401dedb2ab28b487e8bd931";
    private DatabaseReference databaseRef;
    private Set<String> loadedReviewIds = new HashSet<>(); // Track loaded reviews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        Beach selectedBeach = getIntent().getParcelableExtra("selectedBeach");
        databaseRef = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/").getReference();

        // Initialize views
        ImageButton mapTab = findViewById(R.id.mapTab);
        ImageButton profileTab = findViewById(R.id.profileTab);
        beachImage = findViewById(R.id.beachImage);
        beachName = findViewById(R.id.beachName);
        beachBlurb = findViewById(R.id.beachBlurb);
        beachHours = findViewById(R.id.beachTimes);
        liveWeatherInfo = findViewById(R.id.weatherInfo);
        reviewInfo = findViewById(R.id.reviewInfo);
        forecastLayout = findViewById(R.id.forecastLayout);
        tagLayout = findViewById(R.id.tagLayout);
        reviewContainer = findViewById(R.id.reviewContainer);
        avgRatingBar = findViewById(R.id.avgRatingBar); // Initialize RatingBar

        // Set up navigation
        mapTab.setOnClickListener(v -> navigateTo(MainActivity.class));
        profileTab.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        // Populate UI with Beach data
        if (selectedBeach != null) {
            populateBeachData(selectedBeach);
            displayReviews(selectedBeach.getName()); // Load initial reviews
            addReviewListener(selectedBeach.getName()); // Listen for new reviews
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("CurrentUser", currentUser.toString());
        String userId = currentUser != null ? currentUser.getUid() : "";
        Log.d("CurrentUser", userId);
        fetchWeatherData(selectedBeach.getLatitude(), selectedBeach.getLongitude());

        Button addReviewButton = findViewById(R.id.addReview);
        addReviewButton.setOnClickListener(v -> showAddReviewDialog(selectedBeach, userId));
    }

    private void populateBeachData(Beach selectedBeach) {
        beachName.setText(selectedBeach.getName());
        beachBlurb.setText(selectedBeach.getBlurb());
        beachHours.setText("Hours: " + selectedBeach.getHours());
        reviewInfo.setText("Average Rating: ");
        if(selectedBeach.getAvgRating() != null)
            avgRatingBar.setRating(selectedBeach.getAvgRating().floatValue());
        Glide.with(this).load(selectedBeach.getPicture()).into(beachImage);

        displayTags(selectedBeach);
    }

    private void displayTags(Beach selectedBeach) {
        DatabaseReference tagsRef = databaseRef.child("beaches").child(selectedBeach.getName()).child("tags");

        tagsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Integer> tagCounts = new HashMap<>();

                // Populate tagCounts with the latest data from Firebase
                for (DataSnapshot tagSnapshot : task.getResult().getChildren()) {
                    String tag = tagSnapshot.getKey();
                    Integer count = tagSnapshot.getValue(Integer.class);
                    if (tag != null && count != null) {
                        tagCounts.put(tag, count);
                    }
                }

                // Sort tags by frequency and alphabetically as a tiebreaker
                List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagCounts.entrySet());
                sortedTags.sort((entry1, entry2) -> {
                    int frequencyComparison = entry2.getValue().compareTo(entry1.getValue()); // Descending frequency
                    return frequencyComparison != 0 ? frequencyComparison : entry1.getKey().compareTo(entry2.getKey()); // Alphabetical tie-breaker
                });

                // Select the top two tags
                List<String> topTags = new ArrayList<>();
                for (int i = 0; i < Math.min(2, sortedTags.size()); i++) {
                    topTags.add(sortedTags.get(i).getKey());
                }

                // Clear the existing tags in the layout and display the top two
                tagLayout.removeAllViews();
                for (String tag : topTags) {
                    TextView tagView = new TextView(this);
                    tagView.setText("\u2022 " + tag);
                    tagView.setTextSize(15);
                    tagView.setTextColor(getResources().getColor(R.color.blue_hint));
                    tagView.setPadding(0, 4, 0, 4); // Adds spacing between each tag item
                    tagLayout.addView(tagView);
                }
            } else {
                TextView noTagsView = new TextView(this);
                noTagsView.setText("There are no tags.");
                noTagsView.setTextSize(15);
                noTagsView.setTextColor(getResources().getColor(R.color.blue_hint));
                tagLayout.addView(noTagsView);
            }
        });
    }

    private void fetchWeatherData (double latitude, double longitude){
        new Thread(() ->{
            try{
                String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                        latitude +"&lon="+longitude+"&appid="+API_KEY+"&units=imperial";
                URL url = new URL(apiURL);
                HttpURLConnection urlConnection =( HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String input;
                while((input = in.readLine()) !=null){
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

    private void displayReviews(String beachId) {
        reviewContainer.removeAllViews(); // Clear any existing reviews in the container

        databaseRef.child("beaches").child(beachId).child("reviews").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot reviewIdSnapshot : task.getResult().getChildren()) {
                    String reviewId = reviewIdSnapshot.getKey();
                    if (!loadedReviewIds.contains(reviewId)) { // Check if already loaded
                        loadedReviewIds.add(reviewId); // Mark as loaded
                        fetchAndDisplayReview(reviewId);
                    }
                }
            } else {
                Toast.makeText(BeachActivity.this, "Failed to load reviews.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addReviewListener(String beachId) {
        databaseRef.child("beaches").child(beachId).child("reviews").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String reviewId = snapshot.getKey();
                if (!loadedReviewIds.contains(reviewId)) { // Only load new reviews
                    loadedReviewIds.add(reviewId);
                    fetchAndDisplayReview(reviewId);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchAndDisplayReview(String reviewId) {
        databaseRef.child("reviews").child(reviewId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Review review = task.getResult().getValue(Review.class);
                if (review != null) {
                    fetchAuthorDetails(review.getAuthor(), review);
                }
            } else {
                Toast.makeText(BeachActivity.this, "Failed to retrieve review details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAuthorDetails(String authorId, Review review) {
        databaseRef.child("users").child(authorId).child("name").get().addOnCompleteListener(task -> {
            String username = task.isSuccessful() && task.getResult().exists() ? task.getResult().getValue(String.class) : "Unknown User";
            addReviewView(review, username);
        });
    }

    private void showAddReviewDialog(Beach selectedBeach, String user) {
        final String[] tags = {
                "Surfing", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
                "Beach Sports", "Shaded Areas", "Hiking Trails Nearby", "Nearby Food Vendors", "Bonfire-Friendly",
                "Scenic Views"
        };
        final ArrayList<String> selectedTags = new ArrayList<>();
        final Date date = Calendar.getInstance().getTime();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Review for " + selectedBeach.getName());

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText reviewInput = new EditText(this);
        reviewInput.setHint("Write your review...");
        layout.addView(reviewInput);

        final RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleIndicator);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setMax(5);
        ratingBar.setIsIndicator(false); // Make it clickable since indicator style is read-only by default
        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(ratingBar);

        GridLayout tagLayout = new GridLayout(this);
        tagLayout.setColumnCount(2);
        tagLayout.setPadding(10, 10, 10, 10);

        for (String tag : tags) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tag);
            checkBox.setTextSize(12);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
            });
            tagLayout.addView(checkBox);
        }
        layout.addView(tagLayout);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reviewText = reviewInput.getText().toString();
            Double rating = (double) ratingBar.getRating();

            if (!reviewText.isEmpty() && rating > 0) {
                Review newReview = new Review(selectedBeach.getName(), rating, date, user, reviewText, new ArrayList<>(selectedTags));
                saveReviewToFirebase(selectedBeach, user, newReview);
            } else {
                Toast.makeText(BeachActivity.this, "Please complete all review fields.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveReviewToFirebase(Beach selectedBeach, String user, Review review) {
        String reviewId = databaseRef.child("reviews").push().getKey();

        if (reviewId != null) {
            databaseRef.child("reviews").child(reviewId).setValue(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateBeachReviewReference(selectedBeach, reviewId);
                    updateRating(selectedBeach, review.getRating());
                    updateBeachTags(selectedBeach, review.getTags());
                    updateUserReviewReference(user, reviewId);
                    Toast.makeText(BeachActivity.this, "Review added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BeachActivity.this, "Failed to save review.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateRating(Beach beach, double newRating) {
        DatabaseReference beachRef = databaseRef.child("beaches").child(beach.getName());
        beachRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                double currAvgRating = task.getResult().child("avgRating").getValue(Double.class);
                long reviewCount = task.getResult().child("reviews").getChildrenCount();

                double totalRating = currAvgRating * (reviewCount - 1) + newRating;
                double avgRating = totalRating / reviewCount;
                beachRef.child("avgRating").setValue(avgRating);

                avgRatingBar.setStepSize(0.5f); // Set step size
                avgRatingBar.setRating((float) avgRating); // Explicitly cast to float
                reviewInfo.setText("Average Rating:");
            } else {
                Toast.makeText(BeachActivity.this, "Failed to retrieve review rating.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void addReviewView(Review review, String username) {
        LinearLayout reviewLayout = new LinearLayout(this);
        reviewLayout.setOrientation(LinearLayout.VERTICAL);
        reviewLayout.setPadding(16, 16, 16, 16);

        TextView reviewAuthor = new TextView(this);
        reviewAuthor.setText("Author: " + username);

        TextView reviewDate = new TextView(this);
        reviewDate.setText("Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(review.getDate()));

        TextView reviewText = new TextView(this);
        reviewText.setText(review.getComment());

        final RatingBar reviewRatingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleIndicator);
        reviewRatingBar.setNumStars(5);
        reviewRatingBar.setStepSize(0.5f);
        reviewRatingBar.setMax(5);
        reviewRatingBar.setIsIndicator(false); // Make it clickable since indicator style is read-only by default
        reviewRatingBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        reviewRatingBar.setRating(review.getRating().floatValue());
        reviewRatingBar.setIsIndicator(true); // Set to non-clickable for review display

        TextView reviewTags = new TextView(this);
        List<String> tags = review.getTags() != null ? review.getTags() : new ArrayList<>();
        reviewTags.setText("Tags: " + String.join(", ", tags));

        reviewLayout.addView(reviewAuthor);
        reviewLayout.addView(reviewDate);
        reviewLayout.addView(reviewText);
        reviewLayout.addView(reviewRatingBar); // Add RatingBar for individual review
        reviewLayout.addView(reviewTags);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(android.R.color.darker_gray);

        reviewContainer.addView(reviewLayout);
        reviewContainer.addView(divider);
    }

    private void updateBeachTags(Beach selectedBeach, List<String> newTags) {
        DatabaseReference beachTagsRef = databaseRef.child("beaches").child(selectedBeach.getName()).child("tags");

        beachTagsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Create a map to hold updated tag counts
                Map<String, Integer> tagsMap = new HashMap<>();

                // Populate the map with existing tag counts from Firebase
                if (snapshot.exists()) {
                    for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                        String tag = tagSnapshot.getKey();
                        Integer count = tagSnapshot.getValue(Integer.class);
                        tagsMap.put(tag, count != null ? count : 0);
                    }
                }

                // Increment the count for each tag in newTags
                for (String tag : newTags) {
                    tagsMap.put(tag, tagsMap.getOrDefault(tag, 0) + 1);
                }

                // Push the updated tag count map back to Firebase
                beachTagsRef.setValue(tagsMap);
                displayTags(selectedBeach);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BeachActivity.this, "Failed to update beach tags.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBeachReviewReference(Beach beach, String reviewId) {
        databaseRef.child("beaches").child(beach.getName()).child("reviews").child(reviewId).setValue(true);
    }

    private void updateUserReviewReference(String user, String reviewId) {
        databaseRef.child("users").child(user).child("reviews").child(reviewId).setValue(true);
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }
}