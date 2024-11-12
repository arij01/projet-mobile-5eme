package com.example.eventsphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.eventsphere.entity.Event;
import com.example.eventsphere.entity.EventAdapter;
import com.example.eventsphere.entity.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000; // Code to pick an image
    private DatabaseHelper databaseHelper;
    private EditText editTextName, editTextLocation, editTextDescription;
    private TextView editTextDate; // Change to TextView for date
    private Button buttonAdd, buttonSelectImage;
    private ImageView imageView; // ImageView for displaying selected image
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private Uri selectedImageUri; // URI for the selected image

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
        imageView = findViewById(R.id.imageView);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

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

        // Load events on activity creation
        loadEvents();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Display selected date in the TextView
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
            selectedImageUri = data.getData(); // Get the image URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap); // Set the image in the ImageView
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

        // Assuming eventId is generated automatically, cast it to int
        Event event = new Event((int) eventId, name, date, location, description, selectedImageUri != null ? selectedImageUri.toString() : null);

        // Clear input fields
        editTextName.setText("");
        editTextDate.setText("");
        editTextLocation.setText("");
        editTextDescription.setText("");
        imageView.setImageResource(0); // Clear the image view

        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();

        // Load events
        loadEvents();

        // Navigate to EventListActivity
        Intent intent = new Intent(MainActivity.this, EventListActivity.class);
        intent.putExtra("eventList", (ArrayList<Event>) eventList); // Pass the event list to the new activity
        startActivity(intent);
    }

    // Method to load events from the database
    private void loadEvents() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("events", null, null, null, null, null, null);

        eventList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String imageUri = cursor.getString(cursor.getColumnIndex("image"));

                Event event = new Event(name, date, location, description, imageUri);
                eventList.add(event);
            }
            cursor.close();
        }

        eventAdapter.notifyDataSetChanged(); // Refresh the adapter
    }
}
