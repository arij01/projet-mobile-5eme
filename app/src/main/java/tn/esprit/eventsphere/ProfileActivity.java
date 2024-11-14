package tn.esprit.eventsphere;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tn.esprit.eventsphere.BitmapHelper;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;


import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final int PERMISSION_CODE = 1001;



    private Button uploadImageButton;
    private TextView userNameText, userEmailText, firstNameView, lastNameView, emailView, roleView;
    //private ImageView profileImageView;
    private DatabaseHelper databaseHelper;
    private String userEmail;
    private int userId;
    private Bitmap profileImage;
    private static final int MAX_IMAGE_SIZE = 800; // Maximum width/height for the image
    private ShapeableImageView profileImageView;
    private ImagePickerHelper imagePickerHelper;



    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission result: " + isGranted);
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Storage permission is required to change profile picture", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Log.d(TAG, "Image picker result code: " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Log.d(TAG, "Selected image URI: " + imageUri);
                    handleImageSelection(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views

        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        firstNameView = findViewById(R.id.firstNameView);
        lastNameView = findViewById(R.id.lastNameView);
        emailView = findViewById(R.id.emailView);
        roleView = findViewById(R.id.roleView);
        profileImageView = findViewById(R.id.profileImageView);

        databaseHelper = new DatabaseHelper(this);

        // Set click listener for profile image
       // profileImageView.setOnClickListener(v -> checkPermissionAndPickImage());

        // Initialize the upload button
        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(v -> checkPermissionAndPickImage());


        // Get userId from SharedPreferences
        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        // Initialize ImagePickerHelper
        imagePickerHelper = new ImagePickerHelper(this, profileImageView, databaseHelper, userId);

        // Set click listener for profile image
      //  profileImageView.setOnClickListener(v -> checkPermissionAndPickImage());

        // Load existing profile image if any
        loadExistingProfileImage();

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

            // Retrieve and display the profile image (if available)
            byte[] imageBytes = databaseHelper.getProfileImage(userId);
            if (imageBytes != null) {
                profileImage = BitmapHelper.decodeBytesToBitmap(imageBytes);
                profileImageView.setImageBitmap(profileImage);
            }
        } else {
            Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up click listener for profile image
      //  profileImageView.setOnClickListener(v -> showImagePickerDialog());

        // Set up logout button click listener
        findViewById(R.id.logoutButton).setOnClickListener(v -> logout(v));

        // Set up delete account button click listener
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> showDeleteConfirmationDialog());
    }


    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            imagePickerHelper.pickImageFromGallery();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerHelper.pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadExistingProfileImage() {
        try {
            byte[] imageBytes = databaseHelper.getProfileImage(userId);
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Log.d(TAG, "Launching image picker");
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching image picker", e);
            Toast.makeText(this, "Error opening image picker", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageSelection(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "Selected image URI is null");
            return;
        }

        try {
            Log.d(TAG, "Processing selected image");
            Bitmap scaledBitmap = BitmapHelper.getScaledBitmap(this, imageUri, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
            if (scaledBitmap == null) {
                Log.e(TAG, "Failed to scale bitmap");
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] imageBytes = stream.toByteArray();
            Log.d(TAG, "Image compressed, size: " + imageBytes.length + " bytes");

            boolean saved = databaseHelper.saveProfileImage(userId, imageBytes);
            Log.d(TAG, "Save to database result: " + saved);

            if (saved) {
                profileImageView.setImageBitmap(scaledBitmap);
                Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save profile picture", Toast.LENGTH_SHORT).show();
            }

            scaledBitmap.recycle();
            stream.close();
        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "Error updating profile picture", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProfileImage() {
        try {
            byte[] imageBytes = databaseHelper.getProfileImage(userId);
            Log.d(TAG, "Loading profile image, bytes: " + (imageBytes != null ? imageBytes.length : 0));

            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapHelper.decodeBytesToBitmap(imageBytes);
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Profile image loaded successfully");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile image", e);
        }
    }

    // Implement this based on how you store the user ID
    private int getUserId() {
        // Example implementation using SharedPreferences
        return getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("userId", -1);
    }



    private void showImagePickerDialog() {
        // Check for necessary permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return;
        }

        // Show the image picker dialog
        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        // Gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                    } else {
                        // Camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                // Gallery image selected
                Uri selectedImageUri = data.getData();
                profileImage = BitmapHelper.getScaledBitmap(this, selectedImageUri, 300, 300);
                profileImageView.setImageBitmap(profileImage);
                saveProfileImage();
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                // Camera image captured
                profileImage = (Bitmap) data.getExtras().get("data");
                profileImageView.setImageBitmap(profileImage);
                saveProfileImage();
            }
        }
    }

    private void saveProfileImage() {
        // Convert the Bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        // Save the profile image to the database
        boolean saved = databaseHelper.saveProfileImage(userId, imageBytes);
        if (saved) {
            Toast.makeText(this, "Profile image saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save profile image", Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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