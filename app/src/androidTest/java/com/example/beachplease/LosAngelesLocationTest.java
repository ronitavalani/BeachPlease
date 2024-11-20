package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Handler;
import android.os.Looper;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LosAngelesLocationTest {
    private static final LatLng LOS_ANGELES_COORDINATES = new LatLng(34.0522, -118.2437);
    private GoogleMap googleMap;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.mapView);
            if (mapFragment != null) {
                mapFragment.getMapAsync(map -> googleMap = map);
            }
        });
    }

    @Test
    public void mapLoadsAtLosAngelesCenter() {
        // Wait for the map to initialize (use IdlingResource for better testing practice)
        try {
            Thread.sleep(3000); // Simulate waiting for map to load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ensure access to GoogleMap is performed on the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            if (googleMap != null) {
                LatLng mapCenter = googleMap.getCameraPosition().target;

                // Verify that the map's center is close to Los Angeles coordinates
                double delta = 0.01; // Allow for slight deviations due to map rendering
                MatcherAssert.assertThat(
                        "Map center latitude is incorrect",
                        Math.abs(mapCenter.latitude - LOS_ANGELES_COORDINATES.latitude) < delta
                );
                MatcherAssert.assertThat(
                        "Map center longitude is incorrect",
                        Math.abs(mapCenter.longitude - LOS_ANGELES_COORDINATES.longitude) < delta
                );
            }
        });
    }
}
