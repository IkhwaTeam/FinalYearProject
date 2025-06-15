package com.example.ikhwa.modules;

public class Teacher {
    private String email;
    private String teacherId;
    private String password;
    private String role; // **Only used for admins (Auto-assigned in DB)**

    // **Empty Constructor (Required for Firebase)**
    public Teacher() {
    }

    // **Constructor for Regular Teachers (No Role)**
    public Teacher(String email, String teacherId, String password) {
        this.email = email;
        this.teacherId = teacherId;
        this.password = password;
    }

    // **Constructor for Admin (Role auto-assigned)**
    public Teacher(String email, String teacherId, String password, String role) {
        this.email = email;
        this.teacherId = teacherId;
        this.password = password;
        this.role = role; // Only added when fetched from DB
    }

    // **Getters (Only need to read data)**
    public String getEmail() {
        return email;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role; // Can be null for normal teachers
    }
}
