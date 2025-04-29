// StudentNotificationItem.java

package com.example.ikhwa;

public class StudentNotificationItem {
    private String title;
    private String description;
    private String date;
    private String time;

    public StudentNotificationItem() {
        // Default constructor required for calls to DataSnapshot.getValue(StudentNotificationItem.class)
    }

    public StudentNotificationItem(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
