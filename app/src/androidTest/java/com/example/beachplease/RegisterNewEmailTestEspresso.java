package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.beachplease.ViewActions.waitFor;

import androidx.test.core.app.ActivityScenario;
import org.junit.Test;

public class RegisterNewEmailTestEspresso {
    @Test
    public void testRegistrationWithNewEmail() {
        // Launch the RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.name)).perform(typeText("New User"), closeSoftKeyboard());

        // Enter a new email in the email field
        onView(withId(R.id.email)).perform(typeText("newuser@example.com"), closeSoftKeyboard());

        // Enter a password in the password field
        onView(withId(R.id.password)).perform(typeText("newPass123"), closeSoftKeyboard());

        // Click the register button
        onView(withId(R.id.register)).perform(click());

        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));
    }
}
