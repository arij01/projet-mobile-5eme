package com.example.eventsphere;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventsphere.entity.DatabaseHelper;

public class UpdateEventActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText editTextName, editTextDate, editTextLocation, editTextDescription;
    private Button buttonUpdate;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // Initialiser les vues
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // Initialiser le helper de base de données
        databaseHelper = new DatabaseHelper(this);

        // Récupérer les données passées
        eventId = getIntent().getIntExtra("eventId", -1);
        editTextName.setText(getIntent().getStringExtra("eventName"));
        editTextDate.setText(getIntent().getStringExtra("eventDate"));
        editTextLocation.setText(getIntent().getStringExtra("eventLocation"));
        editTextDescription.setText(getIntent().getStringExtra("eventDescription"));

        // Gestionnaire du clic sur le bouton de mise à jour
        buttonUpdate.setOnClickListener(v -> updateEvent());
    }

    private void updateEvent() {
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

        // Mise à jour de l'événement
        db.update("events", values, "id = ?", new String[]{String.valueOf(eventId)});
        db.close();

        Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        finish(); // Ferme l'activité et retourne à MainActivity
    }
}
