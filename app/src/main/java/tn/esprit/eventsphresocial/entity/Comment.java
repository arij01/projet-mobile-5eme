package tn.esprit.eventsphresocial.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments")
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private int cmtId;
    @ColumnInfo(name = "comment")
    private String cmtText;

    public Comment(int cmtId, String cmtText) {
        this.cmtId = cmtId;
        this.cmtText = cmtText;
    }

    public Comment() {
    }

    public Comment(String cmtText) {
        this.cmtText = cmtText;

    }

    public String getCmtText() {
        return cmtText;
    }

    public void setCmtText(String cmtText) {
        this.cmtText = cmtText;
    }

    public int getCmtId() {
        return cmtId;
    }

    public void setCmtId(int cmtId) {
        this.cmtId = cmtId;
    }
}
