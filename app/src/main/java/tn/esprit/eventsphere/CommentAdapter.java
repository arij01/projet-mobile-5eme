package tn.esprit.eventsphere;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {


    public interface CommentActionListener {
        void onEditComment(Comment comment, int position);
        void onDeleteComment(Comment comment, int position);
    }

    private List<Comment> commentList;
    private CommentActionListener listener;

    public CommentAdapter(List<Comment> commentList, CommentActionListener listener) {
        this.commentList = commentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentTextView.setText(comment.getCmtText());

        // Set up edit/delete action
        holder.commentOptionsIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.commentOptionsIcon);
            popupMenu.inflate(R.menu.comment_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.edit_comment) {
                    listener.onEditComment(comment, position);
                    return true;
                } else if (itemId == R.id.delete_comment) {
                    listener.onDeleteComment(comment, position);
                    return true;
                } else {
                    return false;
                }
            });
;
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        View commentOptionsIcon;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentOptionsIcon = itemView.findViewById(R.id.commentOptionsIcon); // Assuming this is the options button
        }
    }
}
