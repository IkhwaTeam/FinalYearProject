package com.example.ikhwa;
public class TeacherDetails {
    private String name;
    private String fatherName;
    private String email;
    private String id;
    private String phone;
    private String joinDate;

    public TeacherDetails() {
        // Required empty constructor for Firebase
    }

    public TeacherDetails(String name, String fatherName, String email, String id, String phone, String joinDate) {
        this.name = name;
        this.fatherName = fatherName;
        this.email = email;
        this.id = id;
        this.phone = phone;
        this.joinDate = joinDate;
    }

    // Getters
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getEmail() { return email; }
    public String getId() { return id; }
    public String getPhone() { return phone; }
    public String getJoinDate() { return joinDate; }
}
