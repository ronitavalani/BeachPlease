package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private User user;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private LinearLayout reviewsSection;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();

        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        reviewsSection = findViewById(R.id.reviews_section);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        FirebaseDatabase root = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/");
        databaseRef = root.getReference();

        // Display user information
        if (currentUser != null) {
            String userId = currentUser.getUid();
            displayUserInfo(userId);
            //displayReviews(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        reviewsSection = findViewById(R.id.reviews_section);
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/").getReference();

        loadUserReviews();

        findViewById(R.id.mapTab).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.profileTab).setOnClickListener(v -> navigateTo(ProfileActivity.class));
    }

    private void loadUserReviews() {
        // Get the user ID of the currently authenticated user
        String userId = auth.getCurrentUser().getUid();

        // Retrieve all reviews associated with the user
        databaseRef.child("users").child(userId).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    String reviewId = reviewSnapshot.getKey();
                    loadReviewDetails(reviewId); // Load and display each review by ID
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load reviews.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewDetails(String reviewId) {
        // Fetch each review's details from Firebase
        databaseRef.child("reviews").child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    displayReview(review); // Display the review in the UI
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load review details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayReview(Review review) {
        // Dynamically create a layout for each review
        LinearLayout reviewLayout = new LinearLayout(this);
        reviewLayout.setOrientation(LinearLayout.VERTICAL);
        reviewLayout.setPadding(16, 16, 16, 16);

        // Beach Name
        TextView beachNameView = new TextView(this);
        beachNameView.setText("Beach Name: " + review.getBeachName());
        reviewLayout.addView(beachNameView);

        // Date
        TextView dateView = new TextView(this);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(review.getDate());
        dateView.setText("Date: " + formattedDate);
        reviewLayout.addView(dateView);

        // Rating
        TextView ratingView = new TextView(this);
        ratingView.setText("Rating: " + review.getRating());
        reviewLayout.addView(ratingView);

        // Comment
        TextView commentView = new TextView(this);
        commentView.setText("Comment: " + review.getComment());
        reviewLayout.addView(commentView);

        // Tags
        TextView tagsView = new TextView(this);
        tagsView.setText("Tags: " + String.join(", ", review.getTags()));
        reviewLayout.addView(tagsView);

        // Add the review layout to the main reviews section
        reviewsSection.addView(reviewLayout);
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    private void displayUserInfo(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    if (name != null) {
                        userNameTextView.setText(name);
                    }
                    if (email != null) {
                        userEmailTextView.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayReviews(String userId) {

    }

    public void logOutClick(android.view.View view) {
        auth.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void mapClick(android.view.View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //should this be an option or should it refresh the profile?
    public void profileClick(android.view.View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
