package com.example.ikhwa.modules;

public class TeacherRequest1 {
    private String id, name, email, phone, qualification, password, experience, services, address, fatherName;

    public TeacherRequest1() {
        // Firebase requires empty constructor
    }

    public TeacherRequest1(String id, String name, String email, String phone, String qualification,
                          String password, String experience, String services, String address, String fatherName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.password = password;
        this.experience = experience;
        this.services = services;
        this.address = address;
        this.fatherName = fatherName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getQualification() { return qualification; }
    public String getPassword() { return password; }
    public String getExperience() { return experience; }
    public String getServices() { return services; }
    public String getAddress() { return address; }
    public String getFatherName() { return fatherName; }
}
