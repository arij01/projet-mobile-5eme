package tn.esprit.eventsphresocial;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

import tn.esprit.eventsphresocial.database.AppDatabase;
import tn.esprit.eventsphresocial.entity.Comment;
import tn.esprit.eventsphresocial.entity.CommentAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private Button postButton, deleteButton, updateButton;
    private Comment selectedComment;
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database and DAO
        db = AppDatabase.getAppDatabase(this);


        // Load comments from the database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                commentList = db.commentDao().getAllComments();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentAdapter = new CommentAdapter(commentList);
                        commentsRecyclerView.setAdapter(commentAdapter);
                    }
                });
            }
        });

        // Initialize UI components
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);
        postButton = findViewById(R.id.postButton);
//        deleteButton = findViewById(R.id.deleteButton);
//        updateButton = findViewById(R.id.updateButton);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add a new comment
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentInput.getText().toString();
                if (!commentText.isEmpty()) {
                    Comment comment = new Comment(commentText);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.commentDao().insertComment(comment);
                            runOnUiThread(() -> {
                                commentList.add(comment);
                                commentAdapter.notifyItemInserted(commentList.size() - 1);
                            });
                        }
                    });
                    commentInput.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}