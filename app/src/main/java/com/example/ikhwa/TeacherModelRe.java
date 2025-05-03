package com.example.ikhwa;
public class TeacherModelRe {
    public String name, email, phone, qualification,intrest,why,fathername,password,address;

    public TeacherModelRe() {
    }

    public TeacherModelRe(String name, String email, String phone, String qualification,String address,String fathername,String intrest,String why,String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.fathername = fathername;
        this.password = password;
        this.address = address;
        this.intrest = intrest;
        this.why = why;
        this.qualification = qualification;
    }
}
