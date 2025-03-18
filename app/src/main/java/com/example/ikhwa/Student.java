package com.example.ikhwa;

public class Student {
    private String email;
    private String studentId;
    private int age;
    private String phone;
    private String address;
    private String password;

    // **Empty Constructor (Required for Firebase)**
    public Student() {
    }

    // **Parameterized Constructor**
    public Student(String email, String studentId, int age, String phone, String address, String password) {
        this.email = email;
        this.studentId = studentId;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    // **Getters and Setters**
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
