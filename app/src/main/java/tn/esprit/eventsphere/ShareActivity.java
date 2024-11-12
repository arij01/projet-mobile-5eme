package tn.esprit.eventsphere;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Share;

public class ShareActivity extends AppCompatActivity {

    private AppDatabase db;
    private int eventId; // This should be passed when sharing a specific event
    private String user = "User123"; // Replace with actual user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        db = AppDatabase.getAppDatabase(this);

        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareEventOnPlatform("Facebook"));
    }

    private void shareEventOnPlatform(String platform) {
        long timestamp = System.currentTimeMillis();

        //Share share = new Share(eventId, user, platform, timestamp);
        Share share = new Share(eventId, platform);

        // Insert share data into the database
        new Thread(() -> {
            db.shareDao().insertShare(share);
            runOnUiThread(() ->
                    Toast.makeText(this, "Event shared on " + platform, Toast.LENGTH_SHORT).show()
            );
        }).start();

        // You can also integrate social media APIs here to actually share the event
        // on social media platforms like Facebook, Twitter, etc.
    }
}
