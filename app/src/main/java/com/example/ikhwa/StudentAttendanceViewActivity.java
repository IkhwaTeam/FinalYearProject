package com.example.ikhwa;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentAttendanceViewActivity extends AppCompatActivity {

    LinearLayout attendanceContainer;
    String courseId, groupId, courseTitle, studentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_view);

        attendanceContainer = findViewById(R.id.attendanceContainer);
        courseId = getIntent().getStringExtra("courseId");
        groupId = getIntent().getStringExtra("groupId");
        courseTitle = getIntent().getStringExtra("courseTitle");
        studentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        TextView titleView = findViewById(R.id.attendanceCourseTitle);
        titleView.setText("Attendance - " + courseTitle);

        if (courseId == null || groupId == null || courseId.isEmpty() || groupId.isEmpty()) {
            Toast.makeText(this, "Missing course or group information", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            loadAttendance();
        }
    }

    private void loadAttendance() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(courseId)
                .child(groupId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceContainer.removeAllViews();

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    String date = dateSnap.getKey();
                    String status = dateSnap.child(studentUid).getValue(String.class);

                    if (status != null) {
                        View row = LayoutInflater.from(StudentAttendanceViewActivity.this)
                                .inflate(R.layout.item_attendance_row, attendanceContainer, false);

                        TextView dateView = row.findViewById(R.id.dateView);
                        TextView statusView = row.findViewById(R.id.statusView);

                        dateView.setText(date);
                        statusView.setText(status.toUpperCase());

                        switch (status.toUpperCase()) {
                            case "P":
                                statusView.setTextColor(Color.parseColor("#1DB586")); // Blue
                                break;
                            case "A":
                                statusView.setTextColor(Color.parseColor("#FD6A63")); // Red
                                break;
                            case "L":
                                statusView.setTextColor(Color.parseColor("#FDC25B")); // yellow
                                break;
                            default:
                                statusView.setTextColor(Color.GRAY);
                                break;
                        }

                        attendanceContainer.addView(row);
                    }
                }

                if (attendanceContainer.getChildCount() == 0) {
                    Toast.makeText(StudentAttendanceViewActivity.this,
                            "No attendance records yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentAttendanceViewActivity.this,
                        "Failed to load attendance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
