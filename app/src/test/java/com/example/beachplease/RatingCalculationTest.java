package com.example.beachplease;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class RatingCalculationTest {

    @Mock
    FirebaseDatabase mockFirebaseDatabase;

    @Mock
    DatabaseReference mockDatabaseReference;

    @Mock
    DatabaseReference mockChildDatabaseReference;

    @Mock
    Task<DataSnapshot> mockTask;

    @Mock
    DataSnapshot mockDataSnapshot;

    private BeachActivity beachActivity;
    private Beach testBeach;


    // variables to simulate Firebase data
    private AtomicReference<Double> avgRating = new AtomicReference<>(0.0);
    private AtomicInteger reviewCount = new AtomicInteger(0);

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);


        when(mockFirebaseDatabase.getReference()).thenReturn(mockDatabaseReference);


        when(mockDatabaseReference.child(anyString())).thenReturn(mockChildDatabaseReference);
        when(mockChildDatabaseReference.child(anyString())).thenReturn(mockChildDatabaseReference);


        when(mockChildDatabaseReference.get()).thenReturn(mockTask);


        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDataSnapshot);

        // dynamically simulate the rating updating
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if ("avgRating".equals(key)) {
                return mockSnapshot(avgRating.get());
            } else if ("reviews".equals(key)) {
                return mockSnapshot((long) reviewCount.get());
            }
            return null;
        }).when(mockDataSnapshot).child(anyString());


        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if ("avgRating".equals(key)) {
                avgRating.set((Double) invocation.getArgument(1)); // Update avgRating dynamically
            }
            return null;
        }).when(mockChildDatabaseReference).setValue(any());

        beachActivity = new BeachActivity(mockDatabaseReference);


        //creating a beach object to be used
        testBeach = new Beach("Test Beach", -118.2437, 34.0522, "8AM - 6PM", 0.0, "", "A test beach");
    }

    //using android snapshot feature
    private DataSnapshot mockSnapshot(Object value) {
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        if (value instanceof Double) {
            when(mockSnapshot.getValue(Double.class)).thenReturn((Double) value);
        } else if (value instanceof Long) {
            when(mockSnapshot.getChildrenCount()).thenReturn((Long) value);
        }
        return mockSnapshot;
    }

    @Test
    public void testRatingCalculation() {

        //add three reviews and make sure they update correctly

        beachActivity.updateRating(testBeach, 4.0);
        reviewCount.incrementAndGet();
        avgRating.set(4.0);
        assertEquals(4.0, avgRating.get(), 0.01);

      //calculate avg w new rating
        beachActivity.updateRating(testBeach, 5.0);
        reviewCount.incrementAndGet();
        avgRating.set((avgRating.get() * (reviewCount.get() - 1) + 5.0) / reviewCount.get());
        assertEquals(4.5, avgRating.get(), 0.01);


        beachActivity.updateRating(testBeach, 3.0);
        reviewCount.incrementAndGet();
        avgRating.set((avgRating.get() * (reviewCount.get() - 1) + 3.0) / reviewCount.get());
        assertEquals(4.0, avgRating.get(), 0.01);
    }
}
