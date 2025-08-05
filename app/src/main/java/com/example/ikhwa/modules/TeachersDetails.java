package com.example.ikhwa.modules;
public class TeachersDetails {
    private String name;
    private String fatherName;
    private String id;
    private String email;
    private String phone;
    private String joinDate;

    // Required no-argument constructor for Firebase
    public TeachersDetails() {
    }

    // All-argument constructor
    public TeachersDetails(String name, String fatherName, String id, String email, String phone, String joinDate) {
        this.name = name;
        this.fatherName = fatherName;
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.joinDate = joinDate;
    }

    // Getter methods
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getJoinDate() { return joinDate; }

    // Setter methods
    public void setName(String name) { this.name = name; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "TeacherDetails{" +
                "name='" + name + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", joinDate='" + joinDate + '\'' +
                '}';
    }
}
