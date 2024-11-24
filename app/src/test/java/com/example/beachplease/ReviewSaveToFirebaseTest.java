package com.example.beachplease;

import static junit.framework.TestCase.assertTrue;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReviewSaveToFirebaseTest {
    @Test
    public void testFirebaseReviewSave() {
        BeachActivity beachActivity = Mockito.spy(new BeachActivity());
        DatabaseReference mockDatabaseRef = Mockito.mock(DatabaseReference.class);
        DatabaseReference mockReviewsRef = Mockito.mock(DatabaseReference.class);
        Task<Void> mockTask = Mockito.mock(Task.class);

        String mockUser = "qRN6NhMeUSdUz5pdeW5tKbDbK6w2";
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "https://i.guim.co.uk/img/media/9e82374a96f4b7f1a8203c5b1c04585798328772/0_344_5184_3110/master/5184.jpg?width=1200&quality=85&auto=format&fit=max&s=7f8add4ee6f3eeb5af5fdef1489d4269",
                "A picturesque coastal area known for its scenic beauty and celebrity homes.");

        final Date date = Calendar.getInstance().getTime();
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Pet-Friendly");
        tags.add("Hiking Trails Nearby");
        Review mockReview = new Review("Malibu Beach", 3.0, date, "qRN6NhMeUSdUz5pdeW5tKbDbK6w2", "i had an ok time", tags);

        Mockito.when(mockDatabaseRef.child("reviews")).thenReturn(mockReviewsRef);
        Mockito.when(mockReviewsRef.push()).thenReturn(mockReviewsRef);
        Mockito.when(mockReviewsRef.getKey()).thenReturn("mockReviewId");
        Mockito.when(mockReviewsRef.child(Mockito.anyString())).thenReturn(mockReviewsRef);
        Mockito.when(mockReviewsRef.setValue(Mockito.any(Review.class))).thenReturn(mockTask);

        Mockito.doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(Mockito.any());

        // Act
        AtomicBoolean result = beachActivity.saveReviewToFirebase(mockBeach, mockUser, mockReview);

        // Assert
        assertTrue(result.get());
        Mockito.verify(mockDatabaseRef).child("reviews");
        Mockito.verify(mockReviewsRef).setValue(mockReview);
    }
}
