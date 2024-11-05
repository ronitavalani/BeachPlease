package com.example.beachplease;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private LinearLayout reviewsSection;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        reviewsSection = findViewById(R.id.reviews_section);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/").getReference();

        // Display user information
        if (currentUser != null) {
            String userId = currentUser.getUid();
            displayUserInfo(userId);
            loadUserReviews(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.mapTab).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.profileTab).setOnClickListener(v -> navigateTo(ProfileActivity.class));
    }

    private void loadUserReviews(String userId) {
        databaseRef.child("users").child(userId).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    String reviewId = reviewSnapshot.getKey();
                    loadReviewDetails(reviewId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load reviews.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviewDetails(String reviewId) {
        databaseRef.child("reviews").child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    displayReview(review, reviewId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load review details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayReview(Review review, String reviewId) {
        LinearLayout reviewLayout = new LinearLayout(this);
        reviewLayout.setOrientation(LinearLayout.VERTICAL);
        reviewLayout.setPadding(16, 16, 16, 16);

        TextView beachNameView = new TextView(this);
        beachNameView.setText("Beach Name: " + review.getBeachName());
        reviewLayout.addView(beachNameView);

        TextView dateView = new TextView(this);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(review.getDate());
        dateView.setText("Date: " + formattedDate);
        reviewLayout.addView(dateView);

        TextView ratingView = new TextView(this);
        ratingView.setText("Rating: " + review.getRating());
        reviewLayout.addView(ratingView);

        TextView commentView = new TextView(this);
        commentView.setText("Comment: " + review.getComment());
        reviewLayout.addView(commentView);

        TextView tagsView = new TextView(this);
        List<String> tags = review.getTags() != null ? review.getTags() : new ArrayList<>();
        tagsView.setText("Tags: " + String.join(", ", tags));
        reviewLayout.addView(tagsView);

        // Buttons layout for Edit and Delete buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Edit button
        Button editButton = new Button(this);
        editButton.setText("Edit Review");
        editButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editButton.setOnClickListener(v -> showEditReviewDialog(review, reviewId));
        buttonLayout.addView(editButton);

        // Delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete Review");
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        deleteButton.setOnClickListener(v -> deleteReview(reviewId, review.getBeachName()));
        buttonLayout.addView(deleteButton);

        reviewLayout.addView(buttonLayout);
        reviewsSection.addView(reviewLayout);
    }

    private void showEditReviewDialog(Review review, String reviewId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Review for " + review.getBeachName());

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText reviewInput = new EditText(this);
        reviewInput.setText(review.getComment());
        layout.addView(reviewInput);

        LinearLayout ratingLayout = new LinearLayout(this);
        ratingLayout.setOrientation(LinearLayout.HORIZONTAL);

        int starWidth = 48;
        int totalWidth = starWidth * 5;

        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setMax(5);

        float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (totalWidth * scale + 0.5f);

        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(widthInPx, LinearLayout.LayoutParams.WRAP_CONTENT));
        ratingLayout.addView(ratingBar);

        layout.addView(ratingLayout);

        final String[] tags = {
                "Surfing", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
                "Beach Sports", "Shaded Areas", "Hiking Trails Nearby", "Nearby Food Vendors", "Bonfire-Friendly",
                "Scenic Views"
        };

        // Ensure tags are not null before initializing selectedTags and originalTags
        final List<String> existingTags = review.getTags() != null ? review.getTags() : new ArrayList<>();
        final ArrayList<String> selectedTags = new ArrayList<>(existingTags);
        final Set<String> originalTags = new HashSet<>(existingTags);

        GridLayout tagLayout = new GridLayout(this);
        tagLayout.setColumnCount(2);
        tagLayout.setPadding(10, 10, 10, 10);

        for (String tag : tags) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tag);
            checkBox.setTextSize(12);
            checkBox.setChecked(selectedTags.contains(tag));
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

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedComment = reviewInput.getText().toString();
            double updatedRating = (double) ratingBar.getRating();
            double oldRating = review.getRating(); // Store old rating

            // Update review object with new data
            review.setComment(updatedComment);
            review.setRating(updatedRating);
            review.setTags(selectedTags);

            // Calculate tags that were deselected and newly selected
            Set<String> deselectedTags = new HashSet<>(originalTags);
            deselectedTags.removeAll(selectedTags);

            Set<String> newTags = new HashSet<>(selectedTags);
            newTags.removeAll(originalTags);

            // Update Firebase with the edited review, update tags, and rating
            updateReviewInFirebase(reviewId, review, deselectedTags, newTags, review.getBeachName());
            updateEditedRating(review.getBeachName(), oldRating, updatedRating); // Update rating with old and new ratings
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateReviewInFirebase(String reviewId, Review review, Set<String> deselectedTags, Set<String> newTags, String beachName) {
        databaseRef.child("reviews").child(reviewId).setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Review updated successfully!", Toast.LENGTH_SHORT).show();

                // Update tags for the associated beach
                updateBeachTags(beachName, deselectedTags, newTags);

                reviewsSection.removeAllViews(); // Clear current reviews
                loadUserReviews(auth.getCurrentUser().getUid()); // Reload updated reviews
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to update review.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview(String reviewId, String beachName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference reviewRef = databaseRef.child("reviews").child(reviewId);
                    reviewRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            double deletedRating = task.getResult().child("rating").getValue(Double.class);
                            Review review = task.getResult().getValue(Review.class);
                            Set<String> tagsToRemove = new HashSet<>(review.getTags() != null ? review.getTags() : new ArrayList<>());
                            databaseRef.child("reviews").child(reviewId).removeValue().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    String userId = auth.getCurrentUser().getUid();
                                    databaseRef.child("users").child(userId).child("reviews").child(reviewId).removeValue();
                                    databaseRef.child("beaches").child(beachName).child("reviews").child(reviewId).removeValue();

                                    Toast.makeText(ProfileActivity.this, "Review deleted successfully!", Toast.LENGTH_SHORT).show();
                                    reviewsSection.removeAllViews();
                                    loadUserReviews(userId);

                                    updateBeachTags(beachName, tagsToRemove, new HashSet<>());
                                    updateDeletedRating(beachName, deletedRating); // Update rating after delete
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to delete review.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void updateBeachTags(String beachName, Set<String> deselectedTags, Set<String> newTags) {
        DatabaseReference beachTagsRef = databaseRef.child("beaches").child(beachName).child("tags");

        for (String tag : deselectedTags) {
            beachTagsRef.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentCount = snapshot.getValue(Integer.class);
                    if (currentCount != null && currentCount > 1) {
                        beachTagsRef.child(tag).setValue(currentCount - 1);
                    } else {
                        beachTagsRef.child(tag).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to update tag count.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        for (String tag : newTags) {
            beachTagsRef.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentCount = snapshot.getValue(Integer.class);
                    if (currentCount != null) {
                        beachTagsRef.child(tag).setValue(currentCount + 1);
                    } else {
                        beachTagsRef.child(tag).setValue(1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to update tag count.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateEditedRating(String beachName, double oldRating, double newRating) {
        DatabaseReference beachRef = databaseRef.child("beaches").child(beachName);

        beachRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                double currAvgRating = task.getResult().child("avgRating").getValue(Double.class);
                long reviewCount = task.getResult().child("reviews").getChildrenCount();

                // Calculate total rating, adjust for old and new ratings, and update average
                double totalRating = currAvgRating * reviewCount;
                totalRating = totalRating - oldRating + newRating;
                double avgRating = totalRating / reviewCount;

                // Update Firebase with the new average rating
                beachRef.child("avgRating").setValue(avgRating);
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to update rating after edit.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDeletedRating(String beachName, double deletedRating) {
        DatabaseReference beachRef = databaseRef.child("beaches").child(beachName);


        beachRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                double currAvgRating = task.getResult().child("avgRating").getValue(Double.class);
                long reviewCount = task.getResult().child("reviews").getChildrenCount();

                Log.d("AVG RATING", String.valueOf(currAvgRating));
                Log.d("AVG RATING", String.valueOf(reviewCount));

                if (reviewCount > 0) {
                    // Calculate total rating, subtract deleted rating, and update average
                    double totalRating = currAvgRating * (reviewCount + 1);
                    totalRating -= deletedRating;
                    double avgRating = totalRating / reviewCount;

                    // Update Firebase with the new average rating
                    beachRef.child("avgRating").setValue(avgRating);
                } else {
                    // If no reviews remain, reset the rating to 0 or N/A
                    beachRef.child("avgRating").setValue(0.0);
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to update rating after delete.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    private void displayUserInfo(String userId) {
        DatabaseReference usersRef = databaseRef.child("users").child(userId);
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

    public void logOutClick(View view) {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}

