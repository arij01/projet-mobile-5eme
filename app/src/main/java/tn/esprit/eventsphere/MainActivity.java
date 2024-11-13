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

        // Bind the button
        Button addEventButton = findViewById(R.id.addEventButton);

        // Set OnClickListener for the add event button
        addEventButton.setOnClickListener(v -> {
            // Start AddEventActivity
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivity(intent);
        });
    }
}
