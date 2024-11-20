package com.example.beachplease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static com.example.beachplease.ViewActions.waitFor;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.AlgorithmParameterGenerator;

@RunWith(AndroidJUnit4.class)
public class NavigateProfileBeachesTestEspresso {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testProfile() {
        //launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        //click the profile button
        onView(withId(R.id.profileTab)).perform(click());

        onView(isRoot()).perform(waitFor(3000));

        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testBeaches() {
        //launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        //click the map button
        onView(withId(R.id.mapTab)).perform(click());

        onView(isRoot()).perform(waitFor(3000));

        intended(hasComponent(MainActivity.class.getName()));
    }
}