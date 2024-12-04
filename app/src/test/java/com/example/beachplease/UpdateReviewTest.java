package com.example.beachplease;

import static android.os.Looper.getMainLooper;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(RobolectricTestRunner.class)
public class UpdateReviewTest {

    private static final String TEST_DB_URL = "https://beachplease-d3daa-default-rtdb.firebaseio.com/";
    private ProfileActivity activity;
    private FirebaseDatabase mockDatabase;
    private DatabaseReference mockDatabaseRef;
    private DatabaseReference mockUsersRef;
    private Task<Void> mockUpdateTask;
    private MockedStatic<FirebaseDatabase> mockedStaticDatabase;
    private MockedStatic<FirebaseApp> mockedStaticApp;
    private MockedStatic<FirebaseAuth> mockedStaticAuth;
    private ActivityController<ProfileActivity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        //mock FirebaseAuth and FirebaseUser
        mockedStaticAuth = mockStatic(FirebaseAuth.class);
        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-user-id");
        mockedStaticAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

        //mock FirebaseApp initialization
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

        //mock FirebaseDatabase and references
        mockedStaticDatabase = mockStatic(FirebaseDatabase.class);
        mockDatabase = mock(FirebaseDatabase.class);
        mockDatabaseRef = mock(DatabaseReference.class);
        mockUsersRef = mock(DatabaseReference.class);

        when(FirebaseDatabase.getInstance(TEST_DB_URL)).thenReturn(mockDatabase);
        when(mockDatabase.getReference()).thenReturn(mockDatabaseRef);
        when(mockDatabaseRef.child("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.child(anyString())).thenReturn(mockUsersRef);

        //set up database reference
        DatabaseReference reviewsRef = mock(DatabaseReference.class);
        DatabaseReference reviewIdRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child("reviews")).thenReturn(reviewsRef);
        when(reviewsRef.child("test-review-id")).thenReturn(reviewIdRef);

        //mock setvalue
        mockUpdateTask = mock(Task.class);
        when(reviewIdRef.setValue(any())).thenReturn(mockUpdateTask);
        when(mockUpdateTask.isSuccessful()).thenReturn(true);

        activityController = Robolectric.buildActivity(ProfileActivity.class);
        activity = activityController
                .create()
                .start()
                .resume()
                .get();

        shadowOf(getMainLooper()).idle();
    }

    @Test
    public void testUpdateReviewInFirebase_Success() {
        Review mockReview = new Review();
        mockReview.setBeachName("Test Beach");
        mockReview.setRating(4.5);
        mockReview.setComment("Great beach!");
        mockReview.setTags(Arrays.asList("Family-Friendly", "Scenic Views"));

        activity.updateReviewInFirebase(
                "test-review-id",
                mockReview,
                new HashSet<>(Arrays.asList("Pet-Friendly")),
                new HashSet<>(Arrays.asList("Family-Friendly")),
                "Test Beach"
        );

        shadowOf(getMainLooper()).idle();
        verify(mockDatabaseRef.child("reviews").child("test-review-id")).setValue(mockReview);
    }

    @Test
    public void testUpdateReviewInFirebase_Failure() {
        Review mockReview = new Review();
        mockReview.setBeachName("Test Beach");
        mockReview.setRating(4.5);
        mockReview.setComment("Great beach!");
        mockReview.setTags(Arrays.asList("Family-Friendly", "Scenic Views"));

        //mock failed task
        when(mockUpdateTask.isSuccessful()).thenReturn(false);

        activity.updateReviewInFirebase(
                "test-review-id",
                mockReview,
                new HashSet<>(Arrays.asList("Pet-Friendly")),
                new HashSet<>(Arrays.asList("Family-Friendly")),
                "Test Beach"
        );

        shadowOf(getMainLooper()).idle();

        verify(mockDatabaseRef.child("reviews").child("test-review-id")).setValue(mockReview);
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


