package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.beachplease.ViewActions.waitFor;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterDuplicateEmailTestEspresso {

    @Test
    public void testRegistrationWithDuplicateEmail() {
        //launch the RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);

        //enter name
        onView(withId(R.id.name)).perform(typeText("New User"), closeSoftKeyboard());

        //enter an email that is already registered
        onView(withId(R.id.email)).perform(typeText("newuser@example.com"), closeSoftKeyboard());

        //enter password
        onView(withId(R.id.password)).perform(typeText("duplicatePass"), closeSoftKeyboard());

        //click the register button
        onView(withId(R.id.register)).perform(click());

        onView(isRoot()).perform(waitFor(3000));

        //verify correct error message displayed
        onView(withText("This email is already registered. Please use another email."))
                .check(matches(isDisplayed()));
    }
}