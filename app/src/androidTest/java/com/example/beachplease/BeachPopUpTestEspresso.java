package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import com.google.android.gms.maps.model.Marker;

import org.junit.Test;
import org.mockito.Mockito;

public class BeachPopUpTestEspresso {

    @Test
    public void testBeachPopUp() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Mock a marker and its associated Beach tag
        Marker mockMarker = Mockito.mock(Marker.class);
        Beach mockBeach = new Beach("Santa Monica Beach", 118.501812, 34.0195,
                "Sunrise - Sunset", 3.6666666666666665,
                "https://images.squarespace-cdn.com/content/v1/5e0e65adcd39ed279a0402fd/1627422658456-7QKPXTNQ34W2OMBTESCJ/1.jpg?format=2500w",
                "Santa Monica Beach has parks, picnic areas, playgrounds, restrooms, as well as staffed lifeguard stations, the Muscle Beach, bike rentals, concessions, a few hotels, a bike path, and wooden pathways for beachgoers with disabilities.");
        Mockito.when(mockMarker.getTag()).thenReturn(mockBeach);

        // Simulate a marker click in the MainActivity
        scenario.onActivity(activity -> activity.onMarkerClick(mockMarker));

        // Verify the AlertDialog title displays the beach name
        onView(withText("Santa Monica Beach")).check(matches(isDisplayed()));

        // Verify the dialog message contains the correct top tags and hours
        String expectedMessage = "Top Tags: Family-Friendly, Nearby Food Vendors\n" +
                "Hours: Sunrise - Sunset\n\n" +
                "Would you like to view more details about Santa Monica Beach?";
        onView(withText(expectedMessage)).check(matches(isDisplayed()));

        // Verify the "View" button is displayed
        onView(withText("View")).check(matches(isDisplayed()));

        // Verify the "Cancel" button is displayed
        onView(withText("Cancel")).check(matches(isDisplayed()));
    }
}
