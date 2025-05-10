package com.example.ikhwa;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnrolledCoursesManager {

    public static void enrollCourse(Context context, String uid, String title,String chapterCount,String tvProgress,
                                    String description, String duration, String startDate, String endDate,String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EnrolledCourse").child(uid);

        EnrolledCourse course = new EnrolledCourse( title,chapterCount,tvProgress,  description,  duration,  startDate,  endDate, type) ;

        ref.child(title).setValue(course)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Course enrolled in database!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to enroll: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
