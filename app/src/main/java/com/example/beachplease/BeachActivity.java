package com.example.beachplease;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

public class BeachActivity extends AppCompatActivity {
    private Beach beach;
    private ImageView beachImage;
    private TextView beachName, beachBlurb, beachHours, weatherInfo, reviewInfo, exampleReview;
    private LinearLayout tagLayout, reviewContainer;
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
        weatherInfo = findViewById(R.id.weatherInfo);
        reviewInfo = findViewById(R.id.review);
        exampleReview = findViewById(R.id.exampleReview);
        tagLayout = findViewById(R.id.tagLayout);
        reviewContainer = findViewById(R.id.reviewContainer);

        // Set up navigation
        mapTab.setOnClickListener(v -> navigateTo(MainActivity.class));
        profileTab.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        // Populate UI with Beach data
        if (selectedBeach != null) {
            populateBeachData(selectedBeach);
            displayReviews(selectedBeach.getName()); // Load initial reviews
            addReviewListener(selectedBeach.getName()); // Listen for new reviews
        }

        fetchWeatherData(34.0129, -118.5017);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("CurrentUser", currentUser.toString());
        String userId = currentUser != null ? currentUser.getUid() : "";
        Log.d("CurrentUser", userId);

        Button addReviewButton = findViewById(R.id.addReview);
        addReviewButton.setOnClickListener(v -> showAddReviewDialog(selectedBeach, userId));
    }

    private void populateBeachData(Beach selectedBeach) {
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
            tagLayout.addView(tagView);
        }
    }

    public void fetchWeatherData(double latitude, double longitude) {
        new Thread(() -> {
            try {
                String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                        latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=imperial";
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(apiURL).openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = in.readLine()) != null) {
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

                final String formattedWeatherInfo = "Temperature: " + temp + "°F\n" + "Humidity: " + humidity + "%\n" + "Conditions: " + weatherCondition;
                runOnUiThread(() -> weatherInfo.setText(formattedWeatherInfo));
            } catch (Exception e) {
                Log.e("WeatherAPI", "Error displaying weather data", e);
            }
        }).start();
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

    @SuppressLint("ResourceAsColor")
    private void addReviewView(Review review, String username) {
        LinearLayout reviewLayout = new LinearLayout(this);
        reviewLayout.setOrientation(LinearLayout.VERTICAL);
        reviewLayout.setPadding(16, 16, 16, 16);

        TextView reviewAuthor = new TextView(this);
        reviewAuthor.setText("Author: " + username);

        TextView reviewDate = new TextView(this);
        reviewDate.setText("Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(review.getDate()));

        TextView reviewRating = new TextView(this);
        reviewRating.setText("Rating: " + review.getRating());

        TextView reviewText = new TextView(this);
        reviewText.setText(review.getComment());

        TextView reviewTags = new TextView(this);
        reviewTags.setText("Tags: " + String.join(", ", review.getTags()));

        reviewLayout.addView(reviewAuthor);
        reviewLayout.addView(reviewDate);
        reviewLayout.addView(reviewRating);
        reviewLayout.addView(reviewText);
        reviewLayout.addView(reviewTags);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(android.R.color.darker_gray);

        reviewContainer.addView(reviewLayout);
        reviewContainer.addView(divider);
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

        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
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
                    updateUserReviewReference(user, reviewId);
                    Toast.makeText(BeachActivity.this, "Review added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BeachActivity.this, "Failed to save review.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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