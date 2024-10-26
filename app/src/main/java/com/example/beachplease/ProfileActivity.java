package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup navigation buttons
        ImageButton mapTab = findViewById(R.id.mapTab);
        ImageButton profileTab = findViewById(R.id.profileTab);

        // Handle Map Tab click - navigate back to MainActivity
        mapTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Handle Profile Tab click (do nothing as we are already on the Profile page)
        profileTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No action required, already on Profile
            }
        });
    }
}