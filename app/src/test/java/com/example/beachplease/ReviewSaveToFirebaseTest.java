package com.example.beachplease;

import static android.os.Looper.getMainLooper;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.firebase.database.DataSnapshot;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class ReviewSaveToFirebaseTest {
    private static final String TEST_DB_URL = "https://beachplease-d3daa-default-rtdb.firebaseio.com/";
    private BeachActivity activity;
    private FirebaseDatabase mockDatabase;
    private DatabaseReference mockDatabaseRef;
    private DatabaseReference mockReviewsRef;
    private DatabaseReference mockPushRef;
    private Task<Void> mockTask;
    private OnCompleteListener<Void> saveCallback;
    private MockedStatic<FirebaseDatabase> mockedStaticDatabase;
    private MockedStatic<FirebaseApp> mockedStaticApp;
    private MockedStatic<FirebaseStorage> mockedStaticStorage;
    private MockedStatic<FirebaseAuth> mockedStaticAuth;
    private FirebaseStorage mockStorage;
    private StorageReference mockStorageRef;
    private ActivityController<BeachActivity> activityController;
    private Task<DataSnapshot> mockGetTask;
    private DataSnapshot mockDataSnapshot;

    @Before
    public void setUp() {
        // Initialize Mockito
        MockitoAnnotations.openMocks(this);

        // Set up basic mocks
        mockDatabase = mock(FirebaseDatabase.class);
        mockDatabaseRef = mock(DatabaseReference.class);
        mockReviewsRef = mock(DatabaseReference.class);
        mockPushRef = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        mockGetTask = mock(Task.class); // Mock for get() operations
        mockDataSnapshot = mock(DataSnapshot.class); // Mock for DataSnapshot
        mockStorage = mock(FirebaseStorage.class);
        mockStorageRef = mock(StorageReference.class);

        // Mock FirebaseAuth first
        mockedStaticAuth = mockStatic(FirebaseAuth.class);
        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(FirebaseAuth.getInstance()).thenReturn(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-user-id");

        // Mock FirebaseApp initialization
        mockedStaticApp = mockStatic(FirebaseApp.class);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("test-app-id")
                .setProjectId("test-project")
                .setApiKey("test-api-key")
                .setDatabaseUrl(TEST_DB_URL)
                .setStorageBucket("test-bucket")
                .build();

        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(context, options);
        mockedStaticApp.when(FirebaseApp::getInstance).thenReturn(firebaseApp);

        // Mock FirebaseStorage
        mockedStaticStorage = mockStatic(FirebaseStorage.class);
        when(FirebaseStorage.getInstance()).thenReturn(mockStorage);
        when(mockStorage.getReference()).thenReturn(mockStorageRef);
        when(mockStorageRef.child(anyString())).thenReturn(mockStorageRef);

        // Set up database reference chain - UPDATED VERSION
        mockedStaticDatabase = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance(TEST_DB_URL)).thenReturn(mockDatabase);
        when(mockDatabase.getReference()).thenReturn(mockDatabaseRef);
        when(mockDatabaseRef.child(anyString())).thenReturn(mockReviewsRef);
        when(mockReviewsRef.child(anyString())).thenReturn(mockReviewsRef);
        when(mockReviewsRef.push()).thenReturn(mockPushRef);
        when(mockPushRef.getKey()).thenReturn("mock-review-id");
        when(mockPushRef.setValue(any())).thenReturn(mockTask);

        // Mock get() operations
        when(mockReviewsRef.get()).thenReturn(mockGetTask);
        when(mockDatabaseRef.get()).thenReturn(mockGetTask);
        when(mockGetTask.addOnCompleteListener(any())).thenReturn(mockGetTask);
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDataSnapshot);

        // Mock setValue operations
        when(mockReviewsRef.setValue(any())).thenReturn(mockTask);
        when(mockTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            saveCallback = invocation.getArgument(0);
            return mockTask;
        });

        // Capture the save callback when it's added
        doAnswer(invocation -> {
            saveCallback = (OnCompleteListener<Void>) invocation.getArgument(0);
            return mockTask;
        }).when(mockTask).addOnCompleteListener(any());

        // Set up successful save condition
        when(mockTask.isSuccessful()).thenReturn(true);

        // Initialize BeachActivity with a mock beach
        Intent intent = new Intent();
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");
        intent.putExtra("selectedBeach", mockBeach);

        activityController = Robolectric.buildActivity(BeachActivity.class, intent);
        activity = activityController
                .create()
                .start()
                .resume()
                .get();

        shadowOf(getMainLooper()).idle();
    }

    @After
    public void tearDown() {
        if (mockedStaticApp != null) {
            mockedStaticApp.close();
        }
        if (mockedStaticDatabase != null) {
            mockedStaticDatabase.close();
        }
        if (mockedStaticStorage != null) {
            mockedStaticStorage.close();
        }
        if (mockedStaticAuth != null) {
            mockedStaticAuth.close();
        }
        if (activityController != null) {
            activityController.pause().stop().destroy();
        }
        FirebaseApp.clearInstancesForTest();
    }

    @Test
    public void testReviewSave_Success() {
        // Arrange
        String mockUserId = "test-user-id";
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");

        ArrayList<String> tags = new ArrayList<>(Arrays.asList("Pet-Friendly", "Hiking Trails Nearby"));
        Review mockReview = new Review(
                mockBeach.getName(),
                4.5,
                Calendar.getInstance().getTime(),
                mockUserId,
                "Great beach experience!",
                tags
        );

        // Act
        AtomicBoolean result = activity.saveReviewToFirebase(mockBeach, mockUserId, mockReview);

        // Simulate successful save completion
        saveCallback.onComplete(mockTask);
        shadowOf(getMainLooper()).idle();

        // Assert
        assertTrue(result.get());
        verify(mockDatabaseRef).child("reviews");
        verify(mockReviewsRef.child(anyString())).setValue(mockReview);
    }

    @Test
    public void testReviewSave_VerifyReviewData() {
        // Arrange
        String mockUserId = "test-user-id";
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");

        ArrayList<String> expectedTags = new ArrayList<>(Arrays.asList("Pet-Friendly", "Hiking Trails Nearby"));
        Review expectedReview = new Review(
                mockBeach.getName(),
                4.5,
                Calendar.getInstance().getTime(),
                mockUserId,
                "Great beach experience!",
                expectedTags
        );

        // Capture the review data being saved
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

        // Act
        activity.saveReviewToFirebase(mockBeach, mockUserId, expectedReview);

        // Trigger the callback
        verify(mockTask).addOnCompleteListener(any());
        saveCallback.onComplete(mockTask);
        shadowOf(getMainLooper()).idle();

        // Assert
        // We need to verify the setValue call on the correct reference (child of reviews)
        verify(mockReviewsRef.child(anyString())).setValue(reviewCaptor.capture());
        Review capturedReview = reviewCaptor.getValue();

        assertEquals(expectedReview.getBeachName(), capturedReview.getBeachName());
        assertEquals(expectedReview.getRating(), capturedReview.getRating(), 0.01);
        assertEquals(expectedReview.getAuthor(), capturedReview.getAuthor());
        assertEquals(expectedReview.getComment(), capturedReview.getComment());
        assertEquals(expectedReview.getTags(), capturedReview.getTags());
    }

    @Test
    public void testReviewSave_Failure() {
        // Arrange
        String mockUserId = "test-user-id";
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");

        Review mockReview = new Review(
                mockBeach.getName(),
                4.5,
                Calendar.getInstance().getTime(),
                mockUserId,
                "Great beach experience!",
                new ArrayList<>(Arrays.asList("Pet-Friendly"))
        );

        // Mock failure condition
        when(mockTask.isSuccessful()).thenReturn(false);
        Exception mockException = new Exception("Failed to save review");
        when(mockTask.getException()).thenReturn(mockException);

        // Act
        AtomicBoolean result = activity.saveReviewToFirebase(mockBeach, mockUserId, mockReview);
        saveCallback.onComplete(mockTask);
        shadowOf(getMainLooper()).idle();

        // Assert
        assertFalse(result.get());
        verify(mockDatabaseRef).child("reviews");
        verify(mockReviewsRef.child(anyString())).setValue(mockReview);
    }
}
