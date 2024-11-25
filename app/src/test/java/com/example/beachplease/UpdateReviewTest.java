package com.example.beachplease;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.os.Looper;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class UpdateReviewTest {

    private ProfileActivity profileActivity;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private DatabaseReference mockDatabaseReference;
    @Mock
    private DatabaseReference mockReviewsReference;
    @Mock
    private DatabaseReference mockReviewIdReference;
    @Mock
    private DatabaseReference mockBeachesReference;
    @Mock
    private DatabaseReference mockTagsReference;
    @Mock
    private DatabaseReference mockBeachTagsReference;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("test-app-id")
                    .setProjectId("test-project")
                    .setApiKey("test-api-key")
                    .setDatabaseUrl("https://test-database-url.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext(), options);
        }

        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        DatabaseReference mockUsersReference = mock(DatabaseReference.class);
        DatabaseReference mockUserReviewsReference = mock(DatabaseReference.class);

        when(mockDatabaseReference.child("users")).thenReturn(mockUsersReference);
        when(mockUsersReference.child("testUserId")).thenReturn(mockUserReviewsReference);
        when(mockUserReviewsReference.child("reviews")).thenReturn(mockReviewsReference);
        when(mockDatabaseReference.child("reviews")).thenReturn(mockReviewsReference);
        when(mockReviewsReference.child(anyString())).thenReturn(mockReviewIdReference);
        when(mockDatabaseReference.child("beaches")).thenReturn(mockBeachesReference);
        when(mockBeachesReference.child(anyString())).thenReturn(mockBeachTagsReference);
        when(mockBeachTagsReference.child("tags")).thenReturn(mockTagsReference);

        profileActivity = Robolectric.buildActivity(ProfileActivity.class)
                .create()
                .start()
                .resume()
                .get();

        profileActivity.databaseRef = mockDatabaseReference;
        profileActivity.auth = mockAuth;
    }

    @After
    public void tearDown() {
        FirebaseApp.clearInstancesForTest();
    }

    @Test
    public void testUpdateReviewInFirebaseSuccess() {
        // Setup mock Review object and parameters
        String reviewId = "testReviewId";
        Review review = new Review();
        review.setBeachName("Test Beach");
        review.setComment("Updated Comment");
        review.setRating(4.5);

        Set<String> deselectedTags = new HashSet<>();
        deselectedTags.add("Tag1");

        Set<String> newTags = new HashSet<>();
        newTags.add("Tag2");

        when(mockReviewIdReference.setValue(any())).thenReturn(Tasks.forResult(null));

        for (String tag : deselectedTags) {
            DatabaseReference tagRef = mock(DatabaseReference.class);
            when(mockTagsReference.child(tag)).thenReturn(tagRef);

            DataSnapshot mockSnapshot = mock(DataSnapshot.class);
            when(mockSnapshot.getValue(Integer.class)).thenReturn(1);

            doAnswer(invocation -> {
                ValueEventListener listener = invocation.getArgument(0);
                listener.onDataChange(mockSnapshot);
                return null;
            }).when(tagRef).addListenerForSingleValueEvent(any());
        }

        for (String tag : newTags) {
            DatabaseReference tagRef = mock(DatabaseReference.class);
            when(mockTagsReference.child(tag)).thenReturn(tagRef);

            DataSnapshot mockSnapshot = mock(DataSnapshot.class);
            when(mockSnapshot.getValue(Integer.class)).thenReturn(null);

            doAnswer(invocation -> {
                ValueEventListener listener = invocation.getArgument(0);
                listener.onDataChange(mockSnapshot);
                return null;
            }).when(tagRef).addListenerForSingleValueEvent(any());
        }

        profileActivity.updateReviewInFirebase(reviewId, review, deselectedTags, newTags, "Test Beach");

        shadowOf(Looper.getMainLooper()).idle();

        verify(mockReviewIdReference).setValue(review);

        for (String tag : deselectedTags) {
            verify(mockTagsReference.child(tag)).addListenerForSingleValueEvent(any());
        }

        for (String tag : newTags) {
            verify(mockTagsReference.child(tag)).addListenerForSingleValueEvent(any());
        }
    }

    @Test
    public void testUpdateReviewInFirebaseFailure() {
        String reviewId = "testReviewId";
        Review review = new Review();
        review.setBeachName("Test Beach");
        review.setComment("Updated Comment");
        review.setRating(4.5);

        Set<String> deselectedTags = new HashSet<>();
        deselectedTags.add("Tag1");

        Set<String> newTags = new HashSet<>();
        newTags.add("Tag2");

        Task<Void> failedTask = Tasks.forException(new Exception("Update failed"));
        when(mockReviewIdReference.setValue(any())).thenReturn(failedTask);
        profileActivity.updateReviewInFirebase(reviewId, review, deselectedTags, newTags, "Test Beach");
        shadowOf(Looper.getMainLooper()).idle();
        verify(mockReviewIdReference).setValue(review);
        verify(mockTagsReference, never()).addListenerForSingleValueEvent(any());
    }
}