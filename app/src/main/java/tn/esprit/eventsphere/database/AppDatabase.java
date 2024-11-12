package tn.esprit.eventsphere.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.eventsphere.dao.CommentDao;
import tn.esprit.eventsphere.dao.ShareDao;
import tn.esprit.eventsphere.entity.Comment;
import tn.esprit.eventsphere.entity.Share;

@Database(entities = {Comment.class, Share.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract CommentDao commentDao();
    public abstract ShareDao shareDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "comment_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}

