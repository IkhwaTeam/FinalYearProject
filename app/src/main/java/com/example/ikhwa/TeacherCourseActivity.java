package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class TeacherCourseActivity extends AppCompatActivity {

    private LinearLayout allCourseContainer;
    private LinearLayout yourCourseContainer;
    private Button backButton;
    private String teacherUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);

        allCourseContainer = findViewById(R.id.current_course_container);
        yourCourseContainer = findViewById(R.id.previous_course_container);
        backButton = findViewById(R.id.back);
        teacherUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadAllCourses();
        loadAssignedCourses();

        backButton.setOnClickListener(v ->
                startActivity(new Intent(TeacherCourseActivity.this, Teacher_home.class))
        );
    }

    private void loadAllCourses() {
        FirebaseDatabase.getInstance().getReference("Courses/currentCourse")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allCourseContainer.removeAllViews();
                        for (DataSnapshot courseSnap : snapshot.getChildren()) {
                            addCourseToLayout(courseSnap, allCourseContainer, false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void loadAssignedCourses() {
        yourCourseContainer.removeAllViews();

        String[] types = {"currentCourse", "previousCourse"};

        for (String type : types) {
            FirebaseDatabase.getInstance().getReference("Courses").child(type)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot courseSnap : snapshot.getChildren()) {
                                if (courseSnap.hasChild("groups")) {
                                    for (DataSnapshot groupSnap : courseSnap.child("groups").getChildren()) {
                                        String assignedTeacherId = groupSnap.child("assignedTeacherId").getValue(String.class);
                                        if (teacherUid.equals(assignedTeacherId)) {
                                            addCourseToLayout(courseSnap, yourCourseContainer, true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
        }
    }

    private void addCourseToLayout(DataSnapshot courseSnap, LinearLayout container, boolean isAssigned) {
        String courseId = courseSnap.getKey();
        String title = courseSnap.child("title").getValue(String.class);
        String duration = courseSnap.child("duration").getValue(String.class);

        if (title == null || duration == null || courseId == null) return;

        View courseView = LayoutInflater.from(this).inflate(R.layout.activity_course_item, container, false);

        TextView titleView = courseView.findViewById(R.id.courseTitle);
        TextView durationView = courseView.findViewById(R.id.courseDuration);
        ProgressBar progressBar = courseView.findViewById(R.id.progressBar);
        TextView statusView = courseView.findViewById(R.id.enrollmentStatus);
        Button viewBtn = courseView.findViewById(R.id.main_button);

        titleView.setText(title);
        durationView.setText(duration);
        progressBar.setProgress(0);
        statusView.setText(isAssigned ? "Assigned" : "Not Assigned");

        viewBtn.setOnClickListener(v -> {
            if (isAssigned) {
                Intent intent = new Intent(TeacherCourseActivity.this, TeacherGroupsActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("courseTitle", title);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You are not assigned to this course", Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(courseView);
    }
}
