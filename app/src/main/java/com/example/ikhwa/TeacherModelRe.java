package com.example.ikhwa;
public class TeacherModelRe {
    String name, email, phone;

    public TeacherModelRe() {} // Required for Firebase

    public TeacherModelRe(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
