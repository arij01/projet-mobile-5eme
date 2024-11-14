package tn.esprit.eventsphere;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        // Initializing database and DAOs
        db = AppDatabase.getAppDatabase(this);
        eventDao = db.eventDao();
        shareDao = db.shareDao();
        executor = Executors.newSingleThreadExecutor();

        // Initializing share button
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> showPlatformSelectionDialog());
    }

    private void showPlatformSelectionDialog() {

        String[] platforms = {"Facebook", "Twitter", "Instagram", "Other"};

        //Alert pop up to choose a platform
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a platform")
                .setItems(platforms, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Share on selected platform
                        shareEventOnPlatform(platforms[which]);
                    }
                });
        builder.create().show();
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
                    String content = "Check out this amazing event";

                    switch (platform) {
                        case "Facebook":
                            shareToFacebook(content);
                            break;
                        case "Twitter":
                            shareToTwitter(content);
                            break;
                        case "Instagram":
                            //Uri imageUri = Uri.parse("file://path_to_your_image.jpg");
                            shareToInstagram();
                            break;
                        default:
                            shareText(content);
                            break;
                    }
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Event with ID 3 not found. Unable to share.", Toast.LENGTH_SHORT).show()
                );
                Log.e("ShareActivity", "Event with ID 3 not found.");
            }
        });
    }

    //  using Facebook SDK
    private void shareToFacebook(String content) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote(content)
                .setContentUrl(Uri.parse("https://www.example.com/event"))
                .build();
        ShareDialog.show(this, linkContent);
    }

    // using Twitter's URL scheme
    private void shareToTwitter(String text) {
        String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(text);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        startActivity(intent);
    }
    //save image to Picture foder before sharing
    private void saveTestImageToExternalStorage() {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shared_image); // Replace with actual drawable name
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "shared_image.jpg");

            FileOutputStream out = new FileOutputStream(dir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareToInstagram() {
        saveTestImageToExternalStorage(); // Ensure image is saved

        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "shared_image.jpg");

        if (!imageFile.exists()) {
            Toast.makeText(this, "Image not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri contentUri = FileProvider.getUriForFile(this, "tn.esprit.eventsphere.fileprovider", imageFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setPackage("com.instagram.android");

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Instagram is not installed.", Toast.LENGTH_SHORT).show();
            Log.e("ShareActivity", "Instagram app is not available.", e);
        }
    }


}
