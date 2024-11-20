package com.example.beachplease;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class BeachDetailsTest {
    @Test
    public void testRetrieveBeachDetails() {
        // Input: Create a Beach object for "Santa Monica Beach"
        String expectedName = "Santa Monica Beach";
        double expectedLongitude = 118.501812;
        double expectedLatitude = 34.0195;

        // Create the Beach object
        Beach beach = new Beach(
                expectedName,
                expectedLongitude,
                expectedLatitude,
                "Sunrise - Sunset",
                3.6666666666666665,
                "https://images.squarespace-cdn.com/content/v1/5e0e65adcd39ed279a0402fd/1627422658456-7QKPXTNQ34W2OMBTESCJ/1.jpg?format=2500w",
                "Santa Monica Beach has parks, picnic areas, playgrounds, restrooms, as well as staffed lifeguard stations, the Muscle Beach, bike rentals, concessions, a few hotels, a bike path, and wooden pathways for beachgoers with disabilities."
        );

        // Expected Output: Validate the details
        assertEquals(expectedName, beach.getName());
        assertEquals(expectedLongitude, beach.getLongitude(), 0.000001); // Use delta for floating-point comparison
        assertEquals(expectedLatitude, beach.getLatitude(), 0.000001);
    }

    //dynamically pulling from firebase
    public void testRetrieveBeachDetailsFromFirebase() throws InterruptedException {
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://your-database-url.firebaseio.com/");
        DatabaseReference beachRef = database.getReference("beaches/SantaMonicaBeach");

        // Use CountDownLatch to wait for asynchronous Firebase response
        CountDownLatch latch = new CountDownLatch(1);

        // Variables to store fetched data
        final String[] beachName = new String[1];
        final double[] beachLongitude = new double[1];
        final double[] beachLatitude = new double[1];

        // Fetch data from Firebase
        beachRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Parse data
                beachName[0] = snapshot.child("name").getValue(String.class);
                beachLongitude[0] = snapshot.child("longitude").getValue(Double.class);
                beachLatitude[0] = snapshot.child("latitude").getValue(Double.class);

                // Signal that the data fetch is complete
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown(); // Ensure test doesn't hang on error
            }
        });

        // Wait for Firebase response
        latch.await();

        // Assert fetched data
        assertEquals("Santa Monica Beach", beachName[0]);
        assertEquals(118.501812, beachLongitude[0], 0.000001);
        assertEquals(34.0195, beachLatitude[0], 0.000001);
    }
}
