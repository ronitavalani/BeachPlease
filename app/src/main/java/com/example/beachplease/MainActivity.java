package com.example.beachplease;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private boolean[] selectedFilters;
    private String[] filterOptions = {
            "Surf-Friendly", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
            "Accessible Parking", "Lifeguard on Duty", "Calm Waters", "Water Sports", "Snorkeling",
            "Fishing Allowed", "Shaded Areas", "BBQ/Picnic Grills", "Hiking Trails Nearby",
            "Dunes and Nature Views", "Public Showers", "Nearby Food Vendors", "Bonfire-Friendly",
            "Rocky Terrain", "Shell Collecting", "Tide Pools", "Secluded/Low Traffic", "Night Access",
            "Scenic Views", "Wildlife Spotting", "Accessibility-Friendly"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedFilters = new boolean[filterOptions.length]; // Initialize the selection state array

        // Initialize the SupportMapFragment and set the callback when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Setup navigation buttons
        ImageButton mapTab = findViewById(R.id.mapTab);
        ImageButton profileTab = findViewById(R.id.profileTab);
        ImageButton filterButton = findViewById(R.id.filterButton);

        // Filter Button Click Listener
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup();
            }
        });

        // Handle Map Tab click
        mapTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No action required, we are already on the map page
            }
        });

        // Handle Profile Tab click
        profileTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showFilterPopup() {
        // Create an AlertDialog with a scrollable list of filters
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Beaches")
                .setMultiChoiceItems(filterOptions, selectedFilters, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selectedFilters[which] = isChecked; // Update the selected filters
                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyFilters();
                    }
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog without manually adding a ScrollView
        builder.create().show();
    }

    private void applyFilters() {
        // Placeholder action to show selected filters
        StringBuilder selected = new StringBuilder("Selected filters:\n");
        for (int i = 0; i < filterOptions.length; i++) {
            if (selectedFilters[i]) {
                selected.append(filterOptions[i]).append("\n");
            }
        }
        Toast.makeText(this, selected.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        LatLng defaultLocation = new LatLng(34.0522, -118.2437); // Los Angeles coordinates
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Los Angeles"));
        googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        googleMap.setOnMarkerClickListener(this);
        addBeaches();
    }

    public void addBeaches() {
        LatLng beachLocation = new LatLng(34.0083, -118.4988);
        Marker beachMarker = googleMap.addMarker(new MarkerOptions().position(beachLocation).title("Santa Monica"));
        beachMarker.setTag("beach");
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if ("beach".equals(marker.getTag())) {
            new AlertDialog.Builder(this)
                    .setTitle(marker.getTitle())
                    .setMessage("Beach information for: " + marker.getTitle())
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return false;
    }
}

