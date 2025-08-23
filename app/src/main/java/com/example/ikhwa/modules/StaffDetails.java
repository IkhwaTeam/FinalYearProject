package com.example.ikhwa.modules;

public class StaffDetails {
    private String name;
    private String qualification;
    private String status; // status may be null

    public StaffDetails() {
    }

    public StaffDetails(String name, String qualification, String status) {
        this.name = name;
        this.qualification = qualification;
        this.status = status;
    }

    public String getName() { return name; }
    public String getQualification() { return qualification; }
    public String getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setStatus(String status) { this.status = status; }
}
