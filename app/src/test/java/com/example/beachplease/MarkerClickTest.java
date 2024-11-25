package com.example.beachplease;

import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.Marker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class MarkerClickTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        mainActivity = activityRule.getActivity();
    }

    @Test
    public void testOnMarkerClick() {
        // Arrange: Create a mock marker and associated Beach object
        Marker mockMarker = Mockito.mock(Marker.class);
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");
        mockBeach.addTag("Surfing");

        Mockito.when(mockMarker.getTag()).thenReturn(mockBeach);

        // Act: Call onMarkerClick with the mock marker
        boolean result = mainActivity.onMarkerClick(mockMarker);

        // Assert: Verify the behavior
        assertTrue(result); // Method should return true
    }
}
