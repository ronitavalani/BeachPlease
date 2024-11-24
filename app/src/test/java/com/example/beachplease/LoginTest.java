package com.example.beachplease;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class LoginTest {

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private Task<AuthResult> mockAuthTask;
    @Mock
    private AuthResult mockAuthResult;

    private LoginActivity loginActivity;
    private EditText emailField;
    private EditText passwordField;
    private MockedStatic<FirebaseAuth> mockedStatic;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("test-app-id")
                    .setProjectId("test-project")
                    .setApiKey("test-api-key")
                    .build();
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext(), options);
        }

        mockedStatic = mockStatic(FirebaseAuth.class);
        mockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(mockAuthTask);
        when(mockAuthTask.isSuccessful()).thenReturn(true);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-uid");

        loginActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();

        emailField = loginActivity.emailField;
        passwordField = loginActivity.passwordField;
    }

    @After
    public void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
        FirebaseApp.clearInstancesForTest();
        shadowOf(Looper.getMainLooper()).idle();
    }

    @Test
    public void testSuccessfulLogin() {
        String testEmail = "test@example.com";
        String testPassword = "password123";
        emailField.setText(testEmail);
        passwordField.setText(testPassword);

        Task<AuthResult> successTask = Tasks.forResult(mockAuthResult);
        when(mockAuth.signInWithEmailAndPassword(testEmail, testPassword))
                .thenReturn(successTask);
        when(mockAuthResult.getUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-uid");

        loginActivity.logInClick(null);

        shadowOf(Looper.getMainLooper()).idle();

        verify(mockAuth).signInWithEmailAndPassword(testEmail, testPassword);

        SharedPreferences prefs = loginActivity.getSharedPreferences("UserSession", loginActivity.MODE_PRIVATE);
        assertEquals("test-uid", prefs.getString("userId", null));

        ShadowActivity shadowActivity = shadowOf(loginActivity);
        Intent nextActivity = shadowActivity.getNextStartedActivity();
        assertEquals(MainActivity.class.getName(), nextActivity.getComponent().getClassName());
        assertTrue(loginActivity.isFinishing());
    }


    @Test
    public void testFailedLogin() {
        // Setup
        String testEmail = "test@example.com";
        String testPassword = "wrongpassword";
        emailField.setText(testEmail);
        passwordField.setText(testPassword);
        when(mockAuthTask.isSuccessful()).thenReturn(false);

        loginActivity.logInClick(null);

        verify(mockAuth).signInWithEmailAndPassword(testEmail, testPassword);
    }
}