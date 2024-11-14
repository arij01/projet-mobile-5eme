package tn.esprit.eventsphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the buttons
        Button addEventButton = findViewById(R.id.addEventButton);
        Button addCategoryButton = findViewById(R.id.addCategoryButton);  // Button for adding category
        Button viewEventListButton = findViewById(R.id.viewEventListButton);  // New button to view event list

        // Set OnClickListener for the add event button
        addEventButton.setOnClickListener(v -> {
            // Start AddEventActivity
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for the add category button
        addCategoryButton.setOnClickListener(v -> {
            // Start AddCategoryActivity
            Intent intent = new Intent(MainActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for the view event list button
        viewEventListButton.setOnClickListener(v -> {
            // Start EventListActivity
            Intent intent = new Intent(MainActivity.this, EventListActivity.class);
            startActivity(intent);
        });
    }
}
