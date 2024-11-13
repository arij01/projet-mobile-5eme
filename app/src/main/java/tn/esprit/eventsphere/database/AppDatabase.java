package tn.esprit.eventsphere.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.eventsphere.dao.CategoryDao;
import tn.esprit.eventsphere.entity.Category;
import tn.esprit.eventsphere.dao.EventDao;
import tn.esprit.eventsphere.entity.Event;

@Database(entities = {Event.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;  // Use volatile for thread safety

    // Abstract methods to get DAOs
    public abstract CategoryDao categoryDao();
    public abstract EventDao eventDao();

    // Singleton pattern for getting the database instance
    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {  // Double-check locking
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "events.db")
                            .fallbackToDestructiveMigration()  // Optional: handle migrations if needed
                            .build();
                }
            }
        }
        return instance;
    }
}
