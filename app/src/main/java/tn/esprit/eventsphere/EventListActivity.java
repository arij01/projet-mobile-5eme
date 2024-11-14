package tn.esprit.eventsphere;
import android.view.View;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Event;

public class EventListActivity extends AppCompatActivity implements EventAdapter.EventActionListener {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all events from the database in a background thread
        new Thread(() -> {
            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
            eventList = db.eventDao().getAllEvents(); // Assuming getAllEvents() is defined in your DAO
            runOnUiThread(() -> {
                if (eventList != null && !eventList.isEmpty()) {
                    // Set the adapter to display the events
                    eventAdapter = new EventAdapter(eventList, this);
                    recyclerView.setAdapter(eventAdapter);
                } else {
                    Toast.makeText(EventListActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onEditEvent(Event event, int position) {
        // Show an edit dialog to update event details
        showEditEventDialog(event, position);
    }

    @Override
    public void onDeleteEvent(Event event, int position) {
        // Handle the delete event, e.g., delete from database
        new Thread(() -> {
            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
            db.eventDao().deleteEvent(event);  // Assuming deleteEvent() is defined in your DAO
            runOnUiThread(() -> {
                eventList.remove(position);
                eventAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void showEditEventDialog(Event event, int position) {
        // Create an AlertDialog to edit the event
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Event");

        // Inflate custom layout with EditTexts for editing
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_event, null);
        builder.setView(dialogView);

        // Initialize EditTexts and set existing event details
        EditText editName = dialogView.findViewById(R.id.editEventName);
        EditText editDate = dialogView.findViewById(R.id.editEventDate);
        EditText editLocation = dialogView.findViewById(R.id.editEventLocation);
        EditText editDescription = dialogView.findViewById(R.id.editEventDescription);

        editName.setText(event.getName());
        editDate.setText(event.getDate());
        editLocation.setText(event.getLocation());
        editDescription.setText(event.getDescription());

        // Handle the "Save" button click
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get updated details
            String updatedName = editName.getText().toString();
            String updatedDate = editDate.getText().toString();
            String updatedLocation = editLocation.getText().toString();
            String updatedDescription = editDescription.getText().toString();

            // Update event properties
            event.setName(updatedName);
            event.setDate(updatedDate);
            event.setLocation(updatedLocation);
            event.setDescription(updatedDescription);

            // Update event in the database
            new Thread(() -> {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                db.eventDao().updateEvent(event);  // Assuming updateEvent() is defined in your DAO
                runOnUiThread(() -> {
                    // Update the RecyclerView item
                    eventList.set(position, event);
                    eventAdapter.notifyItemChanged(position);
                    Toast.makeText(EventListActivity.this, "Event updated", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        // Handle the "Cancel" button click
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }
}
