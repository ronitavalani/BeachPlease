package com.example.beachplease;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SelectDeselectTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testFilterSelectionAndDeselection() {
        // Step 1: Open the filter popup
        onView(withId(R.id.filterButton)).perform(ViewActions.click());

        // Step 2: Select "Surfing" filter
        onView(withText("Surfing")).perform(ViewActions.click());
        // Verify the "Surfing" checkbox is selected
        onView(withText("Surfing")).check(matches(isChecked()));

        // Step 3: Deselect "Surfing" filter
        onView(withText("Surfing")).perform(ViewActions.click());
        // Verify the "Surfing" checkbox is deselected
        onView(withText("Surfing")).check(matches(isNotChecked()));

        // Step 4: Select "Family-Friendly" filter
        onView(withText("Family-Friendly")).perform(ViewActions.click());
        // Verify the "Family-Friendly" checkbox is selected
        onView(withText("Family-Friendly")).check(matches(isChecked()));

        // Step 5: Deselect "Family-Friendly" filter
        onView(withText("Family-Friendly")).perform(ViewActions.click());
        // Verify the "Family-Friendly" checkbox is deselected
        onView(withText("Family-Friendly")).check(matches(isNotChecked()));
    }
}
