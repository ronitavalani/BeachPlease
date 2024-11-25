package com.example.beachplease;

import static android.os.Looper.getMainLooper;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

@RunWith(RobolectricTestRunner.class)
public class DeleteReviewTest {
    private static final String TEST_DB_URL = "https://beachplease-d3daa-default-rtdb.firebaseio.com/";
    private ProfileActivity activity;
    private FirebaseDatabase mockDatabase;
    private DatabaseReference mockDatabaseRef;
    private Task<DataSnapshot> mockGetTask;
    private Task<Void> mockDeleteTask;
    private MockedStatic<FirebaseDatabase> mockedStaticDatabase;
    private MockedStatic<FirebaseApp> mockedStaticApp;
    private MockedStatic<FirebaseAuth> mockedStaticAuth;
    private ActivityController<ProfileActivity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock FirebaseAuth and user
        mockedStaticAuth = mockStatic(FirebaseAuth.class);
        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("test-user-id");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        mockedStaticAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

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

        // Mock FirebaseDatabase
        mockDatabase = mock(FirebaseDatabase.class);
        mockDatabaseRef = mock(DatabaseReference.class);
        mockGetTask = mock(Task.class);
        mockDeleteTask = mock(Task.class);

        mockedStaticDatabase = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance(TEST_DB_URL)).thenReturn(mockDatabase);
        when(mockDatabase.getReference()).thenReturn(mockDatabaseRef);

        // Set up database reference chain
        DatabaseReference usersRef = mock(DatabaseReference.class);
        DatabaseReference userIdRef = mock(DatabaseReference.class);
        DatabaseReference reviewsRef = mock(DatabaseReference.class);
        DatabaseReference userReviewsRef = mock(DatabaseReference.class);

        // Create the chain
        when(mockDatabaseRef.child("users")).thenReturn(usersRef);
        when(usersRef.child("test-user-id")).thenReturn(userIdRef);
        when(userIdRef.child("reviews")).thenReturn(userReviewsRef);

        // Mock the reviews reference
        when(mockDatabaseRef.child("reviews")).thenReturn(reviewsRef);
        when(reviewsRef.child(anyString())).thenReturn(reviewsRef);
        when(reviewsRef.get()).thenReturn(mockGetTask);
        when(reviewsRef.removeValue()).thenReturn(mockDeleteTask);

        // Mock success conditions
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockDeleteTask.isSuccessful()).thenReturn(true);

        // Mock user reviews data
        DataSnapshot userReviewsSnapshot = mock(DataSnapshot.class);
        when(userReviewsSnapshot.getChildren()).thenReturn(new ArrayList<>());

        // Mock the ValueEventListener for user reviews
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(userReviewsSnapshot);
            return null;
        }).when(userReviewsRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Initialize activity
        activityController = Robolectric.buildActivity(ProfileActivity.class);
        activity = activityController
                .create()
                .start()
                .resume()
                .get();

        shadowOf(getMainLooper()).idle();
    }

    @Test
    public void testDeleteReview_Success() {
        // Act
        activity.deleteReview("test-review-id", "TestBeach");
        shadowOf(getMainLooper()).idle();

        // Assert
        assertTrue("Delete task should be successful", mockDeleteTask.isSuccessful());
    }

    @After
    public void tearDown() {
        if (mockedStaticApp != null) {
            mockedStaticApp.close();
        }
        if (mockedStaticDatabase != null) {
            mockedStaticDatabase.close();
        }
        if (mockedStaticAuth != null) {
            mockedStaticAuth.close();
        }
        if (activityController != null) {
            activityController.pause().stop().destroy();
        }
        FirebaseApp.clearInstancesForTest();
    }
}