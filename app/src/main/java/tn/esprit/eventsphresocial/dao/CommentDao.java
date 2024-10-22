package tn.esprit.eventsphresocial.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.eventsphresocial.entity.Comment;

@Dao
public interface CommentDao {
    @Insert
    void insertComment(Comment comment);
    @Query("SELECT * FROM comments")
    List<Comment> getAllComments();
    @Update
    void updateComment(Comment comment);

    // Delete a specific comment
    @Delete
    void deleteComment(Comment comment);
}
