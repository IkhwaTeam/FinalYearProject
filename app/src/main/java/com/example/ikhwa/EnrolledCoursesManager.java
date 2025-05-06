package com.example.ikhwa;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnrolledCoursesManager {

    public static void enrollCourse(Context context, String uid, String courseTitle) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EnrolledCourses").child(uid);

        ref.child(courseTitle).setValue(true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Course enrolled in database!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to enroll: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
