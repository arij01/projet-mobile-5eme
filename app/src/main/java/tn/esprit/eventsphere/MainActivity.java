package tn.esprit.eventsphere;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.eventsphere.dao.EventDao;
import tn.esprit.eventsphere.dao.ShareDao;
import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Comment;
import tn.esprit.eventsphere.entity.Event;
import tn.esprit.eventsphere.entity.Share;

public class MainActivity extends AppCompatActivity implements CommentAdapter.CommentActionListener {

    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private Button postButton;
    private AppDatabase db;
    private ExecutorService executor;
    private EventDao eventDao;
    private ShareDao shareDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getAppDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        commentList = new ArrayList<>();

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(commentList, this);
        commentsRecyclerView.setAdapter(commentAdapter);

        commentInput = findViewById(R.id.commentInput);
        postButton = findViewById(R.id.postButton);

        loadCommentsFromDatabase();

        postButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString();
            if (!commentText.isEmpty()) {
                addComment(commentText);
            } else {
                Toast.makeText(MainActivity.this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCommentsFromDatabase() {
        executor.execute(() -> {
            List<Comment> comments = db.commentDao().getAllComments();
            runOnUiThread(() -> {
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addComment(String commentText) {
        Comment comment = new Comment(commentText);
        executor.execute(() -> {
            db.commentDao().insertComment(comment);
            runOnUiThread(() -> {
                commentList.add(comment);
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentInput.setText("");
            });
        });
    }

    //@Override
    public void onEditComment(Comment comment, int position) {
        // Show edit dialog here, and update the database + RecyclerView if edited
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Comment");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(comment.getCmtText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCommentText = input.getText().toString();
            if (!newCommentText.isEmpty()) {
                comment.setCmtText(newCommentText);
                executor.execute(() -> {
                    db.commentDao().updateComment(comment);
                    runOnUiThread(() -> {
                        commentList.set(position, comment);
                        commentAdapter.notifyItemChanged(position);
                    });
                });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    //@Override
    public void onDeleteComment(Comment comment, int position) {
        executor.execute(() -> {
            db.commentDao().deleteComment(comment);
            runOnUiThread(() -> {
                commentList.remove(position);
                commentAdapter.notifyItemRemoved(position);
            });
        });
    }
    // Method to test Share functionality
    private void testShareFeature() {
        executor.execute(() -> {
            // Create a temporary Event for testing
            Event tempEvent = new Event("Temporary Event for Testing");
            long eventId = eventDao.insertEvent(tempEvent);

            // Create a Share associated with this Event
            Share share = new Share((int) eventId, "Facebook");
            shareDao.insertShare(share);

            // Fetch and log the share to verify
            Share fetchedShare = shareDao.getShareByEventId((int) eventId);
            if (fetchedShare != null) {
                Log.d("ShareTest", "Shared on: " + fetchedShare.getPlatform());
            } else {
                Log.d("ShareTest", "Share not found.");
            }
        });
    }

}