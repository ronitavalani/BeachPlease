package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.beachplease.ViewActions.waitFor;

import androidx.test.core.app.ActivityScenario;
import org.junit.Test;

public class UserProfileTestEspresso {

    @Test
    public void testUserLogin() {
        //launch the LoginActivity
        ActivityScenario.launch(LoginActivity.class);
        String name = "Renee Pan";
        String email = "renee@gmail.com";
        String password = "usc310";

        //enter email in the email field
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());

        //enter password in the password field
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard());

        //click the login button
        onView(withId(R.id.login_button)).perform(click());
        onView(isRoot()).perform(waitFor(3000));

        //click profile page
        onView(withId(R.id.profileTab)).perform(click());
        onView(isRoot()).perform(waitFor(3000));

        //check the user info matches
        onView(withId(R.id.user_name)).check(matches(withText(name)));
        onView(withId(R.id.user_email)).check(matches(withText(email)));
    }
}
