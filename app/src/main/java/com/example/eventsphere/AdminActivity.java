package com.example.eventsphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AdminActivity extends AppCompatActivity {
    private TextView userNameText, userEmailText, firstNameView, lastNameView, emailView, roleView;
    private DatabaseHelper databaseHelper;
    private String userEmail;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        firstNameView = findViewById(R.id.firstNameView);
        lastNameView = findViewById(R.id.lastNameView);
        emailView = findViewById(R.id.emailView);
        roleView = findViewById(R.id.roleView);

        databaseHelper = new DatabaseHelper(this);

        // Get user data from intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        String firstName = getIntent().getStringExtra("USER_FIRST_NAME");
        String lastName = getIntent().getStringExtra("USER_LAST_NAME");
        String userRole = getIntent().getStringExtra("USER_ROLE");
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userEmail != null) {
            // Display user details
            String fullName = firstName + " " + lastName;
            userNameText.setText(fullName);
            userEmailText.setText(userEmail);

            // Set additional details
            firstNameView.setText("First Name: " + firstName);
            lastNameView.setText("Last Name: " + lastName);
            emailView.setText("Email: " + userEmail);
            roleView.setText("Role: " + userRole);
        } else {
            Toast.makeText(AdminActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up logout button click listener
        findViewById(R.id.adminlogoutButton).setOnClickListener(v -> logout(v));

        // Set up delete account button click listener
        findViewById(R.id.deleteAccountAdminButton).setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount() {
        if (userId != -1) {
            boolean deleted = databaseHelper.deleteUser(userId);
            if (deleted) {
                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                // Return to login screen
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view) {
        try {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error logging out", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please use the logout button to exit", Toast.LENGTH_SHORT).show();
    }
}