package tn.esprit.eventsphere;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.eventsphere.entity.Event;
import tn.esprit.eventsphere.database.AppDatabase;

public class EventListActivity extends AppCompatActivity {

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
                    eventAdapter = new EventAdapter(eventList);
                    recyclerView.setAdapter(eventAdapter);
                } else {
                    Toast.makeText(EventListActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
