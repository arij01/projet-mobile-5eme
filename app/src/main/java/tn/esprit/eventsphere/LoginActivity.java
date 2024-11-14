package tn.esprit.eventsphere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.eventsphere.entity.User;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        databaseHelper = new DatabaseHelper(this);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this,
                        "Please enter both email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkUser(email, password)) {
                // Get the user object to check their role
                User user = databaseHelper.getUserByEmail(email);
                if (user != null) {
                    Toast.makeText(LoginActivity.this,
                            "Login successful",
                            Toast.LENGTH_SHORT).show();

                    // Navigate based on user role
                    Intent intent;
                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    }

                    // Pass user data to the next activity
                    intent.putExtra("USER_EMAIL", user.getEmail());
                    intent.putExtra("USER_ID", user.getId());
                    intent.putExtra("USER_ROLE", user.getRole());
                    intent.putExtra("USER_FIRST_NAME", user.getFirstName());
                    intent.putExtra("USER_LAST_NAME", user.getLastName());

                    // Start the appropriate activity and clear the back stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Close the login activity
                }
            } else {
                Toast.makeText(LoginActivity.this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}