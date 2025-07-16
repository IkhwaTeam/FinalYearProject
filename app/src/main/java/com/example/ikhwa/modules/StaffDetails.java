package com.example.ikhwa.modules;
public class StaffDetails {
    private String name;

    private String qualification;

    // Required no-argument constructor for Firebase
    public StaffDetails() {
    }

    // All-argument constructor
    public StaffDetails(String name,String qualification) {
        this.name = name;

        this.qualification=qualification;

    }

    // Getter methods
    public String getName() { return name; }

    public String getQualification() { return qualification; }


    // Setter methods
    public void setName(String name) { this.name = name; }

    public void setQualification(String qualification) { this.qualification = qualification; }


    @Override
    public String toString() {
        return "TeacherDetails{" +
                "name='" + name + '\'' +

                ", qualification='" + qualification + '\'' +

                '}';
    }
}
