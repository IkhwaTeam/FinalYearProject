package com.example.ikhwa;

public class NotificationModel {
    String id, title, description, timestamp, target;

    public NotificationModel() {}

    public NotificationModel(String id, String title, String description, String timestamp, String target) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.target = target;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public String getTarget() { return target; }

    // Setters (optional if using Firebase)
}

