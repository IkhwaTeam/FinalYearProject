package com.example.ikhwa.modules;

public class TeacherRequest {
    private String id, name, email, phone, qualification, password, interest, why, address, fatherName;

    public TeacherRequest() {
        // Firebase requires empty constructor
    }

    public TeacherRequest(String id, String name, String email, String phone, String qualification,
                          String password, String interest, String why, String address, String fatherName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.password = password;
        this.interest = interest;
        this.why = why;
        this.address = address;
        this.fatherName = fatherName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getQualification() { return qualification; }
    public String getPassword() { return password; }
    public String getInterest() { return interest; }
    public String getWhy() { return why; }
    public String getAddress() { return address; }
    public String getFatherName() { return fatherName; }
}
