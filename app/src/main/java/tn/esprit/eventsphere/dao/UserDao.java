package tn.esprit.eventsphere.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.eventsphere.entity.Comment;
import tn.esprit.eventsphere.entity.Share;
import tn.esprit.eventsphere.entity.User;
@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
//    @Update
//    void updateComment(User user);

    // Delete a specific comment
    @Delete
    void deleteUser(User user);
    @Query("SELECT * FROM users WHERE id = :Id")
    User checkUser(int Id);

}
