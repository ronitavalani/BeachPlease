package com.example.beachplease;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class TestFilterEdit {

    @Test
    public void testFilterEdit() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.filters = new ArrayList<>();

        String[] filterOptions = {
                "Surfing", "Family-Friendly", "Pet-Friendly", "Picnic Areas", "Restrooms Available",
                "Beach Sports", "Shaded Areas", "Hiking Trails Nearby", "Nearby Food Vendors", "Bonfire-Friendly",
                "Scenic Views"
        };
        boolean[] selectedFilters = new boolean[filterOptions.length]; // Initialize all as unselected


        selectedFilters[0] = true;
        selectedFilters[4] = true;
        selectedFilters[7] = true;
        mainActivity.filters.add(filterOptions[0]);
        mainActivity.filters.add(filterOptions[4]);
        mainActivity.filters.add(filterOptions[7]);

        selectedFilters[4] = false;
        mainActivity.filters.remove(filterOptions[4]);

        List<String> expectedFilters = new ArrayList<>();
        expectedFilters.add(filterOptions[0]);
        expectedFilters.add(filterOptions[7]);

        assertEquals("Filters list should match the expected selection.", expectedFilters, mainActivity.filters);
    }
}

