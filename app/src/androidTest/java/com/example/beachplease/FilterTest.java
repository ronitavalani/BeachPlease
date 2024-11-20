package com.example.beachplease;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FilterTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testApplyFilterWithTestBeaches() {
        activityRule.getScenario().onActivity(activity -> {
            // Step 1: Add test beaches
            Beach testBeach1 = new Beach("Test Beach 1", -118.2437, 34.0522, "8AM - 6PM", 4.5, "", "Test beach for surfing");
            testBeach1.addTag("Surfing");
            testBeach1.addTag("Family-Friendly");

            Beach testBeach2 = new Beach("Test Beach 2", -118.2437, 34.1522, "9AM - 7PM", 3.8, "", "Test beach for picnics");
            testBeach2.addTag("Picnic Areas");

            activity.runOnUiThread(() -> {
                // Add these beaches as markers on the map
                Marker marker1 = activity.googleMap.addMarker(
                        new MarkerOptions().position(new LatLng(testBeach1.getLatitude(), testBeach1.getLongitude()))
                                .title(testBeach1.getName()));
                marker1.setTag(testBeach1);
                activity.getMarkerList().add(marker1);

                Marker marker2 = activity.googleMap.addMarker(
                        new MarkerOptions().position(new LatLng(testBeach2.getLatitude(), testBeach2.getLongitude()))
                                .title(testBeach2.getName()));
                marker2.setTag(testBeach2);
                activity.getMarkerList().add(marker2);
            });
        });

        // Step 2: Open filter popup
        onView(withId(R.id.filterButton)).perform(ViewActions.click());

        // Step 3: Select "Surfing" filter
        onView(withText("Surfing")).perform(ViewActions.click());

        // Step 4: Apply the filter
        onView(withText("Apply")).perform(ViewActions.click());

        // Verify marker visibility
        activityRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                Marker marker1 = activity.getMarkerList().get(0);
                assertTrue(marker1.isVisible()); // Test Beach 1 should be visible

                Marker marker2 = activity.getMarkerList().get(1);
                assertTrue(!marker2.isVisible()); // Test Beach 2 should not be visible

                // Clean up markers
                activity.googleMap.clear();
                activity.getMarkerList().clear();
            });
        });
    }
}

