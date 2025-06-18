package com.example.ikhwa.modules;
public class GroupStudent {
    private String name;
    private String email;

    public GroupStudent() {} // required for Firebase

    public GroupStudent(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
}
