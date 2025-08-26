package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteCourseActivity extends AppCompatActivity {

    private String courseId, courseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_course);

        // Get courseId and courseType from intent
        courseId = getIntent().getStringExtra("courseId");
        courseType = getIntent().getStringExtra("courseType");

        Button confirmDeleteBtn = findViewById(R.id.confirmDeleteBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(v -> finish());
        confirmDeleteBtn.setOnClickListener(v -> deleteCourse());
    }

    private void deleteCourse() {
        if (courseId == null || courseType == null) {
            Toast.makeText(this, "Invalid course details", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference courseRef = FirebaseDatabase.getInstance()
                .getReference("Courses")
                .child(courseType) // currentCourse or previousCourse
                .child(courseId);

        courseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Course deleted from " + courseType, Toast.LENGTH_SHORT).show();
                    finish(); // close delete activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

