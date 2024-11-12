package com.example.eventsphere;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventsphere.entity.Event;
import com.example.eventsphere.entity.EventAdapter;
import com.example.eventsphere.entity.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list); // Ensure you have this layout file

        recyclerView = findViewById(R.id.recyclerView);
        databaseHelper = new DatabaseHelper(this);
        eventList = new ArrayList<>();

        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        loadEvents();
    }

    private void loadEvents() {
        eventList.clear();
        // Logic to load events from the database (similar to MainActivity)
        // Add events to eventList
        eventAdapter.notifyDataSetChanged(); // Notify the adapter of changes
    }

    public void deleteEvent(int eventId) {
        // Logic to delete the event from the database
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
        loadEvents(); // Refresh the event list
    }

    public void updateEvent(Event event) {
        // Logic to update the event
        Toast.makeText(this, "Update event logic here", Toast.LENGTH_SHORT).show();
        // For example, start an UpdateEventActivity with the event details
    }
}
