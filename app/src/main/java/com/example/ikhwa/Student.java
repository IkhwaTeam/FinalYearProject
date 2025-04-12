package com.example.ikhwa;

public class Student {
    private String email;
    private String studentId;
    private String age;
    private String phone;
    private String address;
    private String password;
    private String student_name;
    private String father_name;

    public Student() {
    }

    public Student(String email, String studentId, String age, String phone, String address, String password, String student_name, String father_name) {
        this.email = email;
        this.studentId = studentId;
        this.age = age;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.student_name = student_name;
        this.father_name = father_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

