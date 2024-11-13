package com.example.eventsphere;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // Link to your user layout

        // Get user data passed from login if needed
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        int userId = getIntent().getIntExtra("USER_ID", -1);
        String userRole = getIntent().getStringExtra("USER_ROLE");

        // Implement your user functionalities here
    }
}