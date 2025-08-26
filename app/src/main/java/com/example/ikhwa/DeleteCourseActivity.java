package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteCourseActivity extends AppCompatActivity {

    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_course);

        // Get courseId from intent
        courseId = getIntent().getStringExtra("courseId");

        Button confirmDeleteBtn = findViewById(R.id.confirmDeleteBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(v -> finish());
        confirmDeleteBtn.setOnClickListener(v -> deleteCourse());
    }

    private void deleteCourse() {
        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course id", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference courseRef = FirebaseDatabase.getInstance()
                .getReference("Courses")
                .child("currentCourse") // fixed path since all are here
                .child(courseId);

        courseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
