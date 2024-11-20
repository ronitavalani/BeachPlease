package com.example.beachplease;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public GoogleMap googleMap;
    private boolean[] selectedFilters;
    private String[] filterOptions = {
            "Surfing", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
            "Beach Sports", "Shaded Areas", "Hiking Trails Nearby","Nearby Food Vendors", "Bonfire-Friendly",
            "Scenic Views"
    };
    private List<Marker> markers = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedFilters = new boolean[filterOptions.length]; // Initialize the selection state array

        //liveWeatherInfo = findViewById(R.id.weatherInfo);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Beaches")
                .setMultiChoiceItems(filterOptions, selectedFilters, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            filters.add(filterOptions[which]);
                        } else {
                            filters.remove(filterOptions[which]);
                            Log.d("Removing Filters", filters.toString());
                        }
                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyFilters();
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    void applyFilters() {
        for (Marker marker : markers) {
            if (marker.getTag() instanceof Beach) {
                Beach beach = (Beach) marker.getTag();
                boolean matchesFilters = false;
                if(filters.isEmpty()) {
                    matchesFilters = true;
                }
                else if (beach.getTags() != null && !beach.getTags().isEmpty()) { // Check if tags are not null
                    Log.d("Beach Tag", beach.getName() + beach.getTags().toString());
                    List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(beach.getTags().entrySet());
                    sortedTags.sort((entry1, entry2) -> {
                        int freqComparison = entry2.getValue().compareTo(entry1.getValue()); // Descending order
                        return freqComparison != 0 ? freqComparison : entry1.getKey().compareTo(entry2.getKey()); // Alphabetical tie-breaker
                    });

                    // Extract the top two tags
                    Set<String> topTags = new HashSet<>();
                    for (int i = 0; i < Math.min(2, sortedTags.size()); i++) {
                        topTags.add(sortedTags.get(i).getKey());
                    }

                    // Check if any filter matches the top two tags
                    for (String filter : filters) {
                        if (topTags.contains(filter)) {
                            matchesFilters = true;
                            break;
                        }
                    }
                }
                marker.setVisible(matchesFilters);
            }
        }
        Toast.makeText(this, "Filters Applied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        LatLng defaultLocation = new LatLng(34.0522, -118.2437); // Los Angeles coordinates
        googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        googleMap.setOnMarkerClickListener(this);
        loadBeaches(map);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag() instanceof Beach) {
            Beach selectedBeach = (Beach) marker.getTag();

            // Retrieve and sort tags by values in descending order, then select the top two
            Map<String, Integer> tagsMap = selectedBeach.getTags();
            String topTags = tagsMap.entrySet().stream()
                    .sorted((e1, e2) -> {
                        int valueComparison = e2.getValue().compareTo(e1.getValue()); // Descending order of values
                        return valueComparison != 0 ? valueComparison : e1.getKey().compareTo(e2.getKey());
                    }) // Sort in descending order of values
                    .limit(2) // Take the top two entries
                    .map(entry -> entry.getKey()) // Format each entry as "Tag (Value)"
                    .collect(Collectors.joining(", ")); // Join with commas

            // Retrieve hours information
            String hours = selectedBeach.getHours();

            // Construct the message with top tags and hours
            String message = "Top Tags: " + topTags + "\n" +
                    "Hours: " + hours + "\n\n" +
                    "Would you like to view more details about " + selectedBeach.getName() + "?";

            new AlertDialog.Builder(this)
                    .setTitle(selectedBeach.getName())
                    .setMessage(message)
                    .setPositiveButton("View", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Navigate to BeachActivity with the selected Beach object
                            Intent intent = new Intent(MainActivity.this, BeachActivity.class);
                            intent.putExtra("selectedBeach", selectedBeach);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true; // Return true to indicate we've handled the click
        }
        return false;
    }

    private void loadBeaches(GoogleMap map) {
        Log.d("BeachData", "Loading all beaches from database");

        FirebaseDatabase database;
        try {
            database = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/");
            Log.d("BeachData", "Firebase instance obtained successfully");
        } catch (Exception e) {
            Log.e("BeachData", "Failed to get Firebase instance", e);
            return;
        }

        DatabaseReference beachesRef = database.getReference("beaches");

        // Add connection state listener
        DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                Log.d("BeachData", "Firebase connection state: " + (connected ? "connected" : "disconnected"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BeachData", "Connection state listener cancelled", error.toException());
            }
        });

        beachesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("BeachData", "Data snapshot received for all beaches");
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot beachSnapshot : snapshot.getChildren()) {
                        try {
                            Beach beach = beachSnapshot.getValue(Beach.class);
                            if (beach != null) {
                                Log.d("BeachData", "Beach object created: " + beach.getName());

                                Log.d("BeachData", "Beach latitude created: " + beach.getLatitude());
                                Log.d("BeachData", "Beach longitude created: " + beach.getLongitude());
                                // Create a marker for the beach
                                LatLng location = new LatLng(beach.getLatitude(), beach.getLongitude());
                                Marker beachMarker = googleMap.addMarker(new MarkerOptions().position(location).title(beach.getName()));
                                beachMarker.setTag(beach);
                                markers.add(beachMarker);
                                // Place the marker on the map
                            } else {
                                Log.d("BeachData", "Beach object is null for snapshot: " + beachSnapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("BeachData", "Error converting snapshot to Beach object", e);
                        }
                    }
                } else {
                    Log.d("BeachData", "No beach data found in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BeachData", "Failed to retrieve beach data");
                Log.e("BeachData", "Error Code: " + error.getCode());
                Log.e("BeachData", "Error Message: " + error.getMessage());
                Log.e("BeachData", "Error Details: " + error.getDetails());
            }
        });
    }

    public List<Marker> getMarkerList() {
        return markers; // Expose the list of markers
    }
}
