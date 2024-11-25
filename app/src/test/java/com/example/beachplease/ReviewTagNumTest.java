package com.example.beachplease;

import static android.os.Looper.getMainLooper;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ReviewTagNumTest {
    private static final String TEST_DB_URL = "https://beachplease-d3daa-default-rtdb.firebaseio.com/";
    private String TEST_BEACH_NAME = "Malibu Beach";
    private BeachActivity activity;
    private ActivityController<BeachActivity> activityController;

    // Mocks
    private FirebaseDatabase mockDatabase;
    private DatabaseReference mockDatabaseRef;
    private DatabaseReference mockBeachesRef;
    private DatabaseReference mockBeachRef;
    private DatabaseReference mockReviewsRef;
    private DatabaseReference mockBeachReviewsRef;
    private DatabaseReference mockTagsRef;
    private Task<DataSnapshot> mockTask;
    private Task<DataSnapshot> mockReviewsTask;
    private DataSnapshot mockDataSnapshot;
    private DataSnapshot mockReviewsSnapshot;
    private StorageReference mockStorageRef;

    // Static mocks
    private MockedStatic<FirebaseDatabase> mockedStaticDatabase;
    private MockedStatic<FirebaseApp> mockedStaticApp;
    private MockedStatic<FirebaseStorage> mockedStaticStorage;
    private MockedStatic<FirebaseAuth> mockedStaticAuth;

    // Argument captors
    private ArgumentCaptor<Map<String, Integer>> tagsCaptor;
    private OnCompleteListener<DataSnapshot> tagUpdateCallback;

    @Before
    public void setUp() {
        // Initialize Mockito
        MockitoAnnotations.openMocks(this);
        tagsCaptor = ArgumentCaptor.forClass(Map.class);

        // Set up basic mocks
        mockDatabase = mock(FirebaseDatabase.class);
        mockDatabaseRef = mock(DatabaseReference.class);
        mockBeachesRef = mock(DatabaseReference.class);
        mockBeachRef = mock(DatabaseReference.class);
        mockBeachReviewsRef = mock(DatabaseReference.class);
        mockTagsRef = mock(DatabaseReference.class);
        mockReviewsRef = mock(DatabaseReference.class);
        mockTask = mock(Task.class);
        mockReviewsTask = mock(Task.class);
        mockDataSnapshot = mock(DataSnapshot.class);
        mockReviewsSnapshot = mock(DataSnapshot.class);

        // Mock FirebaseAuth
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
        FirebaseStorage mockStorage = mock(FirebaseStorage.class);
        mockStorageRef = mock(StorageReference.class);
        when(FirebaseStorage.getInstance()).thenReturn(mockStorage);
        when(mockStorage.getReference()).thenReturn(mockStorageRef);
        when(mockStorageRef.child(anyString())).thenReturn(mockStorageRef);

        // Set up database reference chain
        mockedStaticDatabase = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance(TEST_DB_URL)).thenReturn(mockDatabase);
        when(mockDatabase.getReference()).thenReturn(mockDatabaseRef);

        // Mock beaches path
        when(mockDatabaseRef.child("beaches")).thenReturn(mockBeachesRef);
        when(mockBeachesRef.child(TEST_BEACH_NAME)).thenReturn(mockBeachRef);

        // Mock reviews under beach path
        when(mockBeachRef.child("reviews")).thenReturn(mockBeachReviewsRef);
        when(mockBeachReviewsRef.child(anyString())).thenReturn(mockBeachReviewsRef);
        when(mockBeachReviewsRef.get()).thenReturn(mockReviewsTask);

        // Mock tags path
        when(mockBeachRef.child("tags")).thenReturn(mockTagsRef);
        when(mockTagsRef.child(anyString())).thenReturn(mockTagsRef);

        // Mock reviews root path
        when(mockDatabaseRef.child("reviews")).thenReturn(mockReviewsRef);
        when(mockReviewsRef.child(anyString())).thenReturn(mockReviewsRef);
        when(mockReviewsRef.get()).thenReturn(mockReviewsTask);

        // Mock tasks behavior
        when(mockTagsRef.get()).thenReturn(mockTask);
        when(mockTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            tagUpdateCallback = invocation.getArgument(0);
            return mockTask;
        });
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDataSnapshot);

        // Mock reviews task behavior
        when(mockReviewsTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(Tasks.forResult(mockReviewsSnapshot));
            return mockReviewsTask;
        });
        when(mockReviewsSnapshot.exists()).thenReturn(true);
        when(mockReviewsSnapshot.getChildren()).thenReturn(new ArrayList<>());

        // Initialize BeachActivity with a mock beach
        Intent intent = new Intent();
        Beach mockBeach = new Beach(TEST_BEACH_NAME, -118.656937, 34.0381, "8am - Sunset", 5.0,
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
    public void testUpdateBeachTags() {
        // Arrange
        Beach mockBeach = new Beach("Malibu Beach", -118.656937, 34.0381, "8am - Sunset", 5.0,
                "test-image-url", "test-description");

        List<String> newTags = new ArrayList<>(Arrays.asList("Surfing", "Family-Friendly"));

        // Create mock snapshots for existing tags
        DataSnapshot surfingSnapshot = mock(DataSnapshot.class);
        when(surfingSnapshot.getKey()).thenReturn("Surfing");
        when(surfingSnapshot.getValue(Integer.class)).thenReturn(1);

        DataSnapshot picnicSnapshot = mock(DataSnapshot.class);
        when(picnicSnapshot.getKey()).thenReturn("Picnic Areas");
        when(picnicSnapshot.getValue(Integer.class)).thenReturn(3);

        List<DataSnapshot> mockSnapshots = Arrays.asList(surfingSnapshot, picnicSnapshot);

        // Set up mock data snapshot
        when(mockDataSnapshot.exists()).thenReturn(true);
        when(mockDataSnapshot.getChildren()).thenReturn(mockSnapshots);

        // Mock ValueEventListener behavior
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockDataSnapshot);
            return null;
        }).when(mockTagsRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        activity.updateBeachTags(mockBeach, newTags);
        shadowOf(getMainLooper()).idle();

        // Assert
        verify(mockTagsRef).setValue(tagsCaptor.capture());
        Map<String, Integer> updatedTags = tagsCaptor.getValue();

        assertNotNull("Tags map should not be null", updatedTags);
        assertEquals("Should have three tags total", 3, updatedTags.size());
        assertEquals("'Surfing' count should be incremented to 2", Integer.valueOf(2), updatedTags.get("Surfing"));
        assertEquals("'Family-Friendly' should be added with count 1", Integer.valueOf(1), updatedTags.get("Family-Friendly"));
        assertEquals("'Picnic Areas' count should remain unchanged", Integer.valueOf(3), updatedTags.get("Picnic Areas"));
    }
}