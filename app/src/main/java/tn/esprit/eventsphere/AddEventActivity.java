package tn.esprit.eventsphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.eventsphere.entity.Event;  // Import Event class
import tn.esprit.eventsphere.database.AppDatabase;  // Import AppDatabase class

public class AddEventActivity extends AppCompatActivity {

    private EditText eventName, eventDate, eventLocation, eventDescription;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Bind UI elements
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        saveButton = findViewById(R.id.saveButton);

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

            // Create an Event object
            Event event = new Event(name, date, location, description); // Use constructor without id

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
}
