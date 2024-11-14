package tn.esprit.eventsphere.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "category")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    public Category(String name) {
        this.name = name;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
