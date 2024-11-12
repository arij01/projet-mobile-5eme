package com.example.eventsphere;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventsphere.entity.DatabaseHelper;
import com.example.eventsphere.entity.Event;
import com.example.eventsphere.entity.EventAdapter;
import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);

        databaseHelper = new DatabaseHelper(this);

        loadEvents();
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

        eventAdapter.notifyDataSetChanged();
    }
}
