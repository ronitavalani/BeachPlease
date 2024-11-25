package com.example.beachplease;

import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegisterTest {
    private RegisterActivity activity;
    private FirebaseAuth mockAuth;
    private DatabaseReference mockReference;
    private DatabaseReference mockChildReference;
    private FirebaseUser mockUser;
    private Task<AuthResult> mockAuthTask;
    private Task<Void> mockDatabaseTask;
    private OnCompleteListener<AuthResult> authCallback;

    @Before
    public void setUp() {
        //set up mocks
        mockAuth = mock(FirebaseAuth.class);
        mockReference = mock(DatabaseReference.class);
        mockChildReference = mock(DatabaseReference.class);
        mockUser = mock(FirebaseUser.class);
        mockAuthTask = mock(Task.class);
        mockDatabaseTask = mock(Task.class);

        //set up database references
        when(mockReference.child(anyString())).thenReturn(mockChildReference);
        when(mockChildReference.setValue(any())).thenReturn(mockDatabaseTask);

        //auth callback
        doAnswer(invocation -> {
            authCallback = (OnCompleteListener<AuthResult>) invocation.getArgument(0);
            return mockAuthTask;
        }).when(mockAuthTask).addOnCompleteListener(any());

        //set up successful auth connections
        when(mockAuthTask.isSuccessful()).thenReturn(true);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-uid");
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthTask);

        //create register activity with mock auth and references
        activity = new RegisterActivity(mockAuth, mockReference);
    }

    @Test
    public void testRegisterUser() {
        activity.registerUser("test@example.com", "password123", "Test User");

        //successful auth completion
        authCallback.onComplete(mockAuthTask);

        //verify database interactions
        verify(mockReference).child("test-uid");
        verify(mockChildReference).setValue(any()); //verify setValue was called
    }
}
