package com.example.ikhwa.modules;

public class StudentDetails {
    private String student_name;
    private String father_name;
    private String email;

    public StudentDetails() {
        // Default constructor required for Firebase
    }

    public StudentDetails(String student_name, String father_name, String email) {
        this.student_name = student_name;
        this.father_name = father_name;
        this.email = email;
    }

    public String getStudent_name() {
        return student_name;
    }

    public String getfather_name() {
        return father_name;
    }

    public String getEmail() {
        return email;
    }

    public void setStudent_name(String Student_name) {
        this.student_name = Student_name;
    }

    public void setfather_name(String father_name) {
        this.father_name = father_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
