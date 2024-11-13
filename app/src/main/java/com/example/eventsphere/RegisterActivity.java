// RegisterActivity.java
package com.example.eventsphere;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        databaseHelper = new DatabaseHelper(this);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String role = "user"; // Default role is user, or get it from input

            // Check if email already exists
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.isEmailExists(email)) {
                Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
            } else {
                // In RegisterActivity.java
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                if (databaseHelper.insertUser(firstName, lastName, email, hashedPassword, role)) {
                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    // Optionally redirect to login activity
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Optionally, create a method to generate a user ID
    private String generateUserId() {
        // Example: generate user ID based on current timestamp or use UUID
        return String.valueOf(System.currentTimeMillis());
    }
}
