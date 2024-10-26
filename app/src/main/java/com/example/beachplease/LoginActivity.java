package com.example.beachplease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        Intent intent = getIntent();
    }

    private boolean authenticateUser(String email, String password) {
        //connect with database
        return false;
    }

    public void registerClick(android.view.View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void logInClick(android.view.View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        //get email and password from frontend
        if (authenticateUser(email, password)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            //tell user it was invalid
        }
    }
}
