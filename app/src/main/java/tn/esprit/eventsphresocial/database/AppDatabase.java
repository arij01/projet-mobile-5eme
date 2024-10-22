package tn.esprit.eventsphresocial.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.eventsphresocial.dao.CommentDao;
import tn.esprit.eventsphresocial.entity.Comment;

@Database(entities = {Comment.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract CommentDao commentDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "comment_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
