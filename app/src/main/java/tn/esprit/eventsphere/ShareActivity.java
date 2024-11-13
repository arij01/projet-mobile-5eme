package tn.esprit.eventsphere;

import android.content.Intent;
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
import android.net.Uri;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class ShareActivity extends AppCompatActivity {


    private AppDatabase db;
    private EventDao eventDao;
    private ShareDao shareDao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        FacebookSdk.sdkInitialize(getApplicationContext());
       AppEventsLogger.activateApp(this.getApplication());

        // Initialize database and DAOs
        db = AppDatabase.getAppDatabase(this);
        eventDao = db.eventDao();
        shareDao = db.shareDao();

        executor = Executors.newSingleThreadExecutor();

        // Initialize the share button
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareEventOnPlatform("Instagram"));


    }
    private void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share Event");
        startActivity(shareIntent);
    }

    private void shareEventOnPlatform(String platform) {

        executor.execute(() -> {

            Event event = eventDao.getEventById(3);

            if (event != null) {
                Share share = new Share(3, platform);

                shareDao.insertShare(share);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Event shared on " + platform, Toast.LENGTH_SHORT).show();
                    String content = "Check out this amazing event ";

                    switch (platform) {
                        case "Facebook":
                            shareToFacebook(content);
                            break;
                        case "Twitter":
                            shareToTwitter(content);
                            break;
//                        case "Instagram":
//                            Uri imageUri = Uri.parse("file://path_to_your_image.jpg");
//                            shareToInstagram(imageUri);
//                            break;
                        default:
                            shareText(content);
                            break;
                    }
                });
            } else {
                // If event does not exist, show an error message
                runOnUiThread(() ->
                        Toast.makeText(this, "Event with ID 3 not found. Unable to share.", Toast.LENGTH_SHORT).show()
                );
                Log.e("ShareActivity", "Event with ID 3 not found.");
            }
        });
    }
    // Share content to Facebook using Facebook SDK
    private void shareToFacebook(String content) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote(content)
                .setContentUrl(Uri.parse("https://www.example.com/event")) // Add event-specific link if available
                .build();
        ShareDialog.show(this, linkContent);
    }

    // Share content to Twitter using Twitter's URL scheme
    private void shareToTwitter(String text) {
        String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(text);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        startActivity(intent);
    }

    // Share an image to Instagram
    private void shareToInstagram(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setPackage("com.instagram.android");

        try {
            startActivity(shareIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Instagram is not installed.", Toast.LENGTH_SHORT).show();
            Log.e("ShareActivity", "Instagram app is not available.", e);
        }
    }
}
