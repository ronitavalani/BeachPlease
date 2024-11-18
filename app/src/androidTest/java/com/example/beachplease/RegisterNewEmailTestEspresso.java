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

import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

//yay!
//@RunWith(AndroidJUnxit4.class)
public class RegisterNewEmailTestEspresso {
    @Rule
    public ActivityTestRule<RegisterActivity> activityRule =
            new ActivityTestRule<>(RegisterActivity.class);
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

        // Check that the success message is displayed
        onView(withText("User registered successfully!"))
                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
