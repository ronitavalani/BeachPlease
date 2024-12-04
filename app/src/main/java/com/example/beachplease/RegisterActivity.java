package com.example.beachplease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private EditText nameField;
    DatabaseReference reference;
    FirebaseAuth auth;

    public RegisterActivity(FirebaseAuth auth, DatabaseReference reference) {
        this.auth = auth;
        this.reference = reference;
    }

    public RegisterActivity() {
        this(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance().getReference("users"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        nameField = findViewById(R.id.name);

        if (reference == null) {
            FirebaseDatabase root = FirebaseDatabase.getInstance("https://beachplease-d3daa-default-rtdb.firebaseio.com/");
            reference = root.getReference("users");
        }

        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

    }

    public void registerUser(String email, String password, String name) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               FirebaseUser fbUser = auth.getCurrentUser();
               if (fbUser != null) {
                   String userId = fbUser.getUid();
                   User user = new User(userId, name, email, password);

                   reference.child(userId).setValue(user).addOnCompleteListener(dbTask ->{
                      if (dbTask.isSuccessful()) {

                          Intent intent = new Intent(this, MainActivity.class);
                          startActivity(intent);
                          finish();
                      }
                      else {
                          Snackbar.make(findViewById(android.R.id.content), "Registration failed: " + dbTask.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                      }
                   });
               }
           }
           else {
               String errorMessage;
               if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                   errorMessage = "This email is already registered. Please use another email.";
               }
               else {
                   errorMessage = "Authentication failed: " + task.getException().getMessage();
               }
               Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
           }
        });
    }

    public void registerClick(android.view.View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String name = nameField.getText().toString();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Incomplete user information ", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(email, password, name);
    }

    public void loginClick(android.view.View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
