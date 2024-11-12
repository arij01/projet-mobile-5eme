package tn.esprit.eventsphere.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "shares",
        foreignKeys = @ForeignKey(entity = Event.class,
                parentColumns = "id",
                childColumns = "event_id",
                onDelete = ForeignKey.CASCADE))

//@Entity(tableName = "shares")
public class Share {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "event_id", index = true)
    private int eventId;

    @ColumnInfo(name = "shared_by_user")
    private String sharedByUser;

    @ColumnInfo(name = "platform")
    private String platform; // e.g., "Facebook", "Twitter"

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public int getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public String getSharedByUser() {
        return sharedByUser;
    }

    public String getPlatform() {
        return platform;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setSharedByUser(String sharedByUser) {
        this.sharedByUser = sharedByUser;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    public Share(int id, String platform) {
        this.id = id;
        this.platform = platform;
    }

//    public Share(int eventId, String sharedByUser, String platform, long timestamp) {
//        this.eventId = eventId;
//        this.sharedByUser = sharedByUser;
//        this.platform = platform;
//        this.timestamp = timestamp;
//    }
}