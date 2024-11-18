package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class RegisterDuplicateEmailTestEspresso {
    @Rule
    public ActivityTestRule<RegisterActivity> activityRule =
            new ActivityTestRule<>(RegisterActivity.class);
    // Helper method to get decorView from the current ActivityScenario
    private Object getDecorView(ActivityScenario<RegisterActivity> scenario) {
        final Object[] decorView = new Object[1];
        scenario.onActivity(activity -> decorView[0] = activity.getWindow().getDecorView());
        return decorView[0];
    }

    @Test
    public void testRegistrationWithDuplicateEmail() {

        // Launch the RegisterActivity
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.name)).perform(typeText("New User"), closeSoftKeyboard());

        // Enter a new email in the email field
        onView(withId(R.id.email)).perform(typeText("newuser@example.com"), closeSoftKeyboard());

        // Enter a password in the password field
        onView(withId(R.id.password)).perform(typeText("newPass123"), closeSoftKeyboard());

        // Click the register button
        onView(withId(R.id.register)).perform(click());

        // Check that the success message is displayed
        onView(withText("This email is already registered. Please use another email."))
                .inRoot(withDecorView(not(is((View) getDecorView(scenario)))))
                .check(matches(isDisplayed()));
    }
}