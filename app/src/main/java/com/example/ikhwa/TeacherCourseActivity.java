package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        String startDateStr = courseSnap.child("startDate").getValue(String.class);
        String endDateStr = courseSnap.child("endDate").getValue(String.class);

        if (title == null || duration == null || courseId == null) return;

        View courseView = LayoutInflater.from(this).inflate(R.layout.activity_course_item, container, false);

        TextView titleView = courseView.findViewById(R.id.courseTitle);
        TextView durationView = courseView.findViewById(R.id.courseDuration);
        ProgressBar progressBar = courseView.findViewById(R.id.progressBar);
        TextView statusView = courseView.findViewById(R.id.enrollmentStatus);
        Button viewBtn = courseView.findViewById(R.id.main_button);

        titleView.setText(title);
        durationView.setText(duration);
        statusView.setText(isAssigned ? "Assigned" : "Not Assigned");

        // Progress Bar setup
        if (isAssigned && startDateStr != null && endDateStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);
                Date today = new Date();

                long totalDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                long passedDays = (today.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);

                int progress = (int) ((Math.max(0, Math.min(passedDays, totalDays)) * 100) / totalDays);
                progressBar.setProgress(progress);
                progressBar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }

        // Add margin above View Button
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewBtn.getLayoutParams();
        params.topMargin = dpToPx(15);
        viewBtn.setLayoutParams(params);

        // Updated click behavior
        viewBtn.setOnClickListener(v -> {
            if (isAssigned) {
                Intent intent = new Intent(TeacherCourseActivity.this, TeacherGroupsActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("courseTitle", title);
                startActivity(intent);
            } else {
                showCourseDetailDialog(title, duration, startDateStr, endDateStr, false, courseId);
            }
        });

        container.addView(courseView);
    }

    private void showCourseDetailDialog(String title, String duration, String startDate, String endDate, boolean isAssigned, String courseId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tea_dialog_course_detail, null);

        TextView titleText = dialogView.findViewById(R.id.dialogCourseTitle);
        TextView durationText = dialogView.findViewById(R.id.dialogCourseDuration);
        TextView startText = dialogView.findViewById(R.id.dialogCourseStart);
        TextView endText = dialogView.findViewById(R.id.dialogCourseEnd);
        TextView assignText = dialogView.findViewById(R.id.dialogAssignmentStatus);
        Button closeButton = dialogView.findViewById(R.id.btnCloseDialog);

        titleText.setText(title);
        durationText.setText("Duration: " + duration);
        startText.setText("Start Date: " + (startDate != null ? startDate : "N/A"));
        endText.setText("End Date: " + (endDate != null ? endDate : "N/A"));
        assignText.setText(isAssigned ? "You are assigned to this course." : "You are not assigned to this course.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Optional "View Groups" button if assigned
        if (isAssigned) {
            dialog.setOnShowListener(d -> {
                Button viewGroupBtn = new Button(this);
                viewGroupBtn.setText("View Groups");
                viewGroupBtn.setBackgroundColor(getResources().getColor(R.color.teal_700));
                viewGroupBtn.setTextColor(getResources().getColor(android.R.color.white));
                viewGroupBtn.setPadding(20, 10, 20, 10);

                ((LinearLayout) dialogView).addView(viewGroupBtn);

                viewGroupBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent intent = new Intent(TeacherCourseActivity.this, TeacherGroupsActivity.class);
                    intent.putExtra("courseId", courseId);
                    intent.putExtra("courseTitle", title);
                    startActivity(intent);
                });
            });
        }

        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
