package com.example.ikhwa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class TeacherCourseActivity extends AppCompatActivity {

    private LinearLayout currentCourseContainer;
    private LinearLayout previousCourseContainer;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);

        currentCourseContainer = findViewById(R.id.current_course_container);
        previousCourseContainer = findViewById(R.id.previous_course_container);

        fetchCurrentCourses();
        fetchPreviousCourses();

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(TeacherCourseActivity.this, Teacher_home.class));
        });
    }

    private void fetchCurrentCourses() {
        FirebaseDatabase.getInstance().getReference("Courses/currentCourse")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        currentCourseContainer.removeAllViews();

                        for (DataSnapshot courseSnap : snapshot.getChildren()) {
                            addCourseToLayout(courseSnap, true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void fetchPreviousCourses() {
        FirebaseDatabase.getInstance().getReference("Courses/previousCourse")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        previousCourseContainer.removeAllViews();

                        for (DataSnapshot courseSnap : snapshot.getChildren()) {
                            addCourseToLayout(courseSnap, false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void addCourseToLayout(DataSnapshot courseSnap, boolean isCurrent) {
        String title = courseSnap.child("title").getValue(String.class);
        String description = courseSnap.child("description").getValue(String.class);
        String duration = courseSnap.child("duration").getValue(String.class);
        String startDate = courseSnap.child("startDate").getValue(String.class);
        String endDate = courseSnap.child("endDate").getValue(String.class);

        if (title == null || duration == null || startDate == null || endDate == null) return;

        View courseView = LayoutInflater.from(this).inflate(R.layout.activity_course_item,
                isCurrent ? currentCourseContainer : previousCourseContainer, false);

        TextView titleView = courseView.findViewById(R.id.courseTitle);
        TextView durationView = courseView.findViewById(R.id.courseDuration);
        ProgressBar progressBar = courseView.findViewById(R.id.progressBar);
        TextView statusView = courseView.findViewById(R.id.enrollmentStatus);
        Button viewBtn = courseView.findViewById(R.id.main_button);

        titleView.setText(title);
        durationView.setText(duration);
        progressBar.setProgress(0);
        statusView.setText("Not enrolled");

        viewBtn.setOnClickListener(v -> {
            if (isCurrent) {
                showCurrentCourseDialogTea();
            } else {
                showPreviousCourseDialogTea();
            }
        });

        if (isCurrent) {
            currentCourseContainer.addView(courseView);
        } else {
            previousCourseContainer.addView(courseView);
        }
    }

    private void showCurrentCourseDialogTea() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.currentcoursedialogtea);
        dialog.setCancelable(true);

        ImageView closeBtn = dialog.findViewById(R.id.close_btn1);
        if (closeBtn != null) closeBtn.setOnClickListener(v -> dialog.dismiss());

        Button btnMarkAtt = dialog.findViewById(R.id.mark_att);
        if (btnMarkAtt != null) {
            btnMarkAtt.setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(TeacherCourseActivity.this, TeacherAttendanceActivity.class));
            });
        }

        dialog.show();
    }

    private void showPreviousCourseDialogTea() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.previouscoursedialogtea);
        dialog.setCancelable(true);

        ImageView closeBtn = dialog.findViewById(R.id.teacher_close_btn_pre);
        if (closeBtn != null) closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
