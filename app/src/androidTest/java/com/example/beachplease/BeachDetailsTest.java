package com.example.beachplease;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import com.google.android.gms.maps.model.Marker;

import org.junit.Test;
import org.mockito.Mockito;

public class BeachDetailsTest {
    @Test
    public void testBeachPopupDetails() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Mock a marker and its associated Beach tag
        Marker mockMarker = Mockito.mock(Marker.class);
        Beach mockBeach = new Beach(
                "Dockweiler Beach",
                -118.433838,  // Longitude
                33.935628,    // Latitude
                "6am - 10pm", // Hours
                4.5,          // Average Rating
                "https://example.com/image.jpg", // Image URL
                "A beautiful beach for bonfires and shaded areas." // Description
        );

        // Associate the mock marker with the mock beach object
        Mockito.when(mockMarker.getTag()).thenReturn(mockBeach);

        // Simulate a marker click in MainActivity
        scenario.onActivity(activity -> activity.onMarkerClick(mockMarker));

        // Verify the title of the AlertDialog matches the beach name
        onView(withText("Dockweiler Beach")).check(matches(isDisplayed()));

        // Verify the dialog message contains the correct top tags and hours
        String expectedMessage = "Top Tags: Shaded Areas, Bonfire-Friendly\nHours: 6am - 10pm\n\n" +
                "Would you like to view more details about Dockweiler Beach?";
        onView(withText(expectedMessage)).check(matches(isDisplayed()));

        // Verify that the buttons "View" and "Cancel" are displayed
        onView(withText("View")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));
    }

}
