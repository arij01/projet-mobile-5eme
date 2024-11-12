package tn.esprit.eventsphere.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


import tn.esprit.eventsphere.entity.Event;

public interface EventDao {
    @Insert
    void insertEvent(Event event);
    @Query("SELECT * FROM events")
    List<Event> getAllEvents();
    @Update
    void updateEvent(Event event);

    // Delete a specific comment
    @Delete
    void deleteEvent(Event event);
}
