package com.example.eventsphere;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eventsphere.entity.DatabaseHelper;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private DatabaseHelper databaseHelper;
    private EditText editTextName, editTextLocation, editTextDescription;
    private TextView editTextDate;
    private Button buttonAdd, buttonSelectImage;
    private ImageView imageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        imageView = findViewById(R.id.imageView);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Button to select image
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        // Set OnClickListener for date TextView to show DatePickerDialog
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Add event button click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editTextDate.setText(date);
        }, year, month, day).show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEvent() {
        String name = editTextName.getText().toString();
        String date = editTextDate.getText().toString();
        String location = editTextLocation.getText().toString();
        String description = editTextDescription.getText().toString();

        if (name.isEmpty() || date.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("location", location);
        values.put("description", description);
        if (selectedImageUri != null) {
            values.put("image", selectedImageUri.toString()); // Store the image URI as a string
        }

        // Insert event into the database and get the generated event ID
        long eventId = db.insert("events", null, values);
        db.close();

        if (eventId != -1) {
            Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();

            // Clear input fields
            editTextName.setText("");
            editTextDate.setText("");
            editTextLocation.setText("");
            editTextDescription.setText("");
            imageView.setImageResource(0); // Clear the image view

            // Navigate to EventListActivity
            Intent intent = new Intent(MainActivity.this, EventListActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
        }
    }
}