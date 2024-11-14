package tn.esprit.eventsphere.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


import tn.esprit.eventsphere.entity.Event;
import tn.esprit.eventsphere.entity.Share;

@Dao
public interface EventDao {

    @Insert
    long insertEvent(Event event);  // Insert a new event

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();  // Get all events

    @Update
    int updateEvent(Event event);  // Update an event

    @Delete
    int deleteEvent(Event event);  // Delete an event
    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventById(int eventId);
}
