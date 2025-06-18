package com.example.ikhwa.modules;

public class Course {
    private String id; // ✅ Course ID for Firebase key
    private String title;
    private String description;
    private String duration;
    private String startDate;
    private String endDate;
    private String type;
    private boolean isEnrolled;
    private String groupId;

    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    public Course(String title, String description, String duration, String startDate, String endDate, String type) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.isEnrolled = false; // Default to not enrolled

    }

    // ✅ Getter and Setter for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }
}
