package com.example.ikhwa;

public class TeacherModel {

    private String teacherId;
    private String name;
    private String fatherName;
    private String email;
    private String qualification;
    private String phone;

    private String address;
    private String password;
    private String services;
    private String experience;
    private String status;

    public TeacherModel() {
    }

    public TeacherModel(String teacherId, String name, String fatherName, String email, String qualification,String phone,  String address, String password, String services, String experience, String status) {
        this.teacherId = teacherId;
        this.name = name;
        this.fatherName = fatherName;
        this.email = email;
        this.qualification = qualification;
        this.phone = phone;

        this.address = address;
        this.password = password;
        this.services = services;
        this.experience = experience;
        this.status = status;
    }

    // Getters & Setters...

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
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

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getExperience() {
        return experience;
    }

    public void setWhyInterested(String whyInterested) {
        this.experience = experience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
