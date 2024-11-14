package tn.esprit.eventsphere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import tn.esprit.eventsphere.entity.Event;  // Import Event class
import tn.esprit.eventsphere.database.AppDatabase;  // Import AppDatabase class
import tn.esprit.eventsphere.utils.ImageUtils;  // Import ImageUtils class

public class AddEventActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_REQUEST = 1;  // Request code for image selection
    private static final int REQUEST_PERMISSION_STORAGE = 100;  // Request code for storage permission

    private EditText eventName, eventDate, eventLocation, eventDescription;
    private Button saveButton, selectImageButton;
    private ImageView eventImage;
    private Bitmap selectedImageBitmap;  // To store the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Vérifier les permissions au démarrage de l'activité
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission si elle n'est pas accordée
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }

        // Bind UI elements
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        saveButton = findViewById(R.id.saveButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        eventImage = findViewById(R.id.eventImage);

        // Button to select an image
        selectImageButton.setOnClickListener(v -> {
            // Vérifier à nouveau la permission avant d'ouvrir la galerie
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped Storage pour Android 10 et plus
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Intent pour ouvrir la galerie d'images
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_PICK_REQUEST);
                } else {
                    // Si la permission n'est pas accordée, demandez-la
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
                }
            }
        });

        // Save Button click listener
        saveButton.setOnClickListener(v -> {
            String name = eventName.getText().toString();
            String date = eventDate.getText().toString();
            String location = eventLocation.getText().toString();
            String description = eventDescription.getText().toString();

            if (name.isEmpty() || date.isEmpty() || location.isEmpty() || description.isEmpty()) {
                Toast.makeText(AddEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert Bitmap image to byte array (if an image is selected)
            byte[] imageBytes = null;
            if (selectedImageBitmap != null) {
                imageBytes = ImageUtils.convertBitmapToByteArray(selectedImageBitmap);
            }

            // Create an Event object
            Event event = new Event(name, date, location, description, imageBytes);  // Pass imageBytes

            // Insert event into the database
            new Thread(() -> {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                long eventId = db.eventDao().insertEvent(event);
                runOnUiThread(() -> {
                    if (eventId > 0) {
                        Toast.makeText(AddEventActivity.this, "Event added!", Toast.LENGTH_SHORT).show();

                        // Redirect to EventListActivity after adding the event
                        Intent intent = new Intent(AddEventActivity.this, EventListActivity.class);
                        startActivity(intent);
                        finish();  // Close the current activity
                    } else {
                        Toast.makeText(AddEventActivity.this, "Error adding event", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                eventImage.setImageBitmap(selectedImageBitmap);  // Display the selected image
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission est accordée, vous pouvez accéder aux fichiers
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // La permission est refusée, vous devez informer l'utilisateur
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
