package com.example.eventsphere.entity;

public class Event {
    private int id;
    private String name;
    private String date;
    private String location;
    private String description;
    private String imageUri; // Add this line


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Event(int id, String name, String date, String location, String description, String imageUri) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageUri = imageUri; // Add this line
    }
    public Event(String name, String date, String location, String description, String imageUri) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageUri = imageUri;
    }


    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }

    // Setters (optionnel, selon votre besoin)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
}
