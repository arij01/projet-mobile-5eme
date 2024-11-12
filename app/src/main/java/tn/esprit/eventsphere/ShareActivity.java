package tn.esprit.eventsphere;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.eventsphere.dao.EventDao;
import tn.esprit.eventsphere.dao.ShareDao;
import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Event;
import tn.esprit.eventsphere.entity.Share;

public class ShareActivity extends AppCompatActivity {

    private AppDatabase db;
    private EventDao eventDao;
    private ShareDao shareDao;

   // private int eventId;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Initialize database and DAOs
        db = AppDatabase.getAppDatabase(this);
        eventDao = db.eventDao();
        shareDao = db.shareDao();

        executor = Executors.newSingleThreadExecutor();

        // Insert a test event and share (for testing purposes)
        //insertTestEventAndShare();

        // Initialize the share button
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareEventOnPlatform("Facebook"));
    }

//    private void insertTestEventAndShare() {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            // Insert a temporary Event
////            Event tempEvent = new Event("New Event");
////            eventId = tempEvent.getId();
//
//            // Check if Event was inserted successfully
//            if (eventId > 0) {
//                Log.d("ShareActivity", "Event created with ID: " + eventId);
//
//                // Delay slightly before inserting the Share, if needed
//                try {
//                    Thread.sleep(100); // Adjust as needed
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // Now create a Share associated with this Event ID
//                Share share = new Share((int) eventId, "Facebook");
//                shareDao.insertShare(share);
//                Log.d("ShareActivity", "Share created for Event ID: " + eventId);
//            } else {
//                Log.e("ShareActivity", "Failed to create Event for Share testing.");
//            }
//        });
//
//    }

    private void shareEventOnPlatform(String platform) {
        //long timestamp = System.currentTimeMillis();

        // Create a Share object with the event ID
        Share share = new Share(3, platform);

        // Insert share data into the database
        executor.execute(() -> {
            shareDao.insertShare(share);
            runOnUiThread(() ->
                    Toast.makeText(this, "Event shared on " + platform, Toast.LENGTH_SHORT).show()
            );
        });
    }
}
