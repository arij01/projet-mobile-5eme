package tn.esprit.eventsphere.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.eventsphere.dao.CategoryDao;
import tn.esprit.eventsphere.entity.Category;
import tn.esprit.eventsphere.dao.EventDao;
import tn.esprit.eventsphere.entity.Event;

@Database(entities = {Event.class, Category.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CategoryDao categoryDao();
    public abstract EventDao eventDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "events.db")
                    .fallbackToDestructiveMigration() // Cette méthode supprime et recrée la base de données lors du changement de version
                    .build();
        }
        return instance;
    }
}
