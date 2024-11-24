package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static com.example.beachplease.ViewActions.waitFor;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MapVisibilityTest {

    @Test
    public void testMapAndMarkersLoadSuccessfully() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] areMarkersLoaded = {false};

        // Launch MainActivity
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.mapView);

            if (mapFragment != null) {
                mapFragment.getMapAsync(googleMap -> {
                    googleMap.setOnMapLoadedCallback(() -> {
                        // Wait for Firebase markers to load
                        activity.runOnUiThread(() -> {
                            List<Marker> markers = activity.getMarkerList();
                            if (markers != null && !markers.isEmpty()) {
                                areMarkersLoaded[0] = true; // Markers have been loaded
                                latch.countDown(); // Signal that the test can proceed
                            }
                        });
                    });
                });
            } else {
                throw new AssertionError("SupportMapFragment is null. Check your layout file.");
            }
        });

        // Wait for markers to be loaded (up to 15 seconds)
        boolean markersLoaded = latch.await(15, TimeUnit.SECONDS);
        assertTrue("Map or markers did not load in time", markersLoaded && areMarkersLoaded[0]);
    }
}


