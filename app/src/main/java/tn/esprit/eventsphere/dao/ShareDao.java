package tn.esprit.eventsphere.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.eventsphere.entity.Share;

@Dao
public interface ShareDao {

    @Insert
    void insertShare(Share share);

    @Query("SELECT * FROM shares WHERE event_id = :eventId")
    List<Share> getSharesByEvent(int eventId);

    @Query("SELECT * FROM shares WHERE shared_by_user = :user")
    List<Share> getSharesByUser(String user);

    @Delete
    void deleteShare(Share share);

    @Query("DELETE FROM shares WHERE event_id = :eventId")
    void deleteSharesByEvent(int eventId);
}

