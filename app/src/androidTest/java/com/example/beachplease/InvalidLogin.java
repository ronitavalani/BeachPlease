package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static com.example.beachplease.ViewActions.waitFor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InvalidLogin {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginWithInvalidCredentials() {
        // Input invalid credentials and click login
        onView(withId(R.id.email)).perform(replaceText("invalid@example.com"));
        onView(withId(R.id.password)).perform(replaceText("wrongPass"));
        onView(withId(R.id.login_button)).perform(click());

        onView(isRoot()).perform(waitFor(3000));

        // Verify that the Snackbar message is displayed
        String expectedMessage = "Login failed. Please try again.";
        onView(withText(expectedMessage)).check(matches(isDisplayed()));
    }
}
