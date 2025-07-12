package com.example.ikhwa;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class StudentAttendanceStatusActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvPresent, tvAbsent, tvLeave, tvHeader, tvSummary, tvStudentName, tvCourseName;
    private DatabaseReference attendanceRef, studentRef, courseRef;
    private String uid, selectedDate, courseId, groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_status);

        // Bind Views
        calendarView = findViewById(R.id.calendarView);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);
        tvLeave = findViewById(R.id.tvAbsent1); // Leave
        tvHeader = findViewById(R.id.tvHeader);
        tvSummary = findViewById(R.id.tvSummary);
        tvStudentName = findViewById(R.id.tvStudentName); // Add this TextView in XML
        tvCourseName = findViewById(R.id.tvCourseName);   // Add this TextView in XML

        uid = FirebaseAuth.getInstance().getUid();
        courseId = getIntent().getStringExtra("courseId");
        groupId = getIntent().getStringExtra("groupId");

        studentRef = FirebaseDatabase.getInstance().getReference("Student").child(uid);
        courseRef = FirebaseDatabase.getInstance().getReference("Courses").child("currentCourse").child(courseId);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance").child(courseId).child(groupId);

        // Default date today
        selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        loadAttendance(selectedDate);
        loadStudentName();
        loadCourseName();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.getTime());
            loadAttendance(selectedDate);
        });
    }

    private void loadAttendance(String date) {
        attendanceRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                int presentCount = 0, absentCount = 0, leaveCount = 0;
                String studentStatus = "-";

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String status = snap.getValue(String.class);
                    if ("P".equals(status)) presentCount++;
                    else if ("A".equals(status)) absentCount++;
                    else if ("L".equals(status)) leaveCount++;

                    if (snap.getKey().equals(uid)) {
                        studentStatus = status;
                    }
                }

                tvPresent.setText("Present: " + presentCount);
                tvAbsent.setText("Absent: " + absentCount);
                tvLeave.setText("Leave: " + leaveCount);

                switch (studentStatus) {
                    case "P": tvSummary.setText("You were Present on " + date); break;
                    case "A": tvSummary.setText("You were Absent on " + date); break;
                    case "L": tvSummary.setText("You were on Leave on " + date); break;
                    default:  tvSummary.setText("No attendance found for " + date); break;
                }

                int total = presentCount + absentCount + leaveCount;
                if (total > 0) {
                    double percentage = (presentCount * 100.0) / total;
                    tvHeader.setText("Attendance\n(" + String.format(Locale.getDefault(), "%.1f", percentage) + "% Present)");
                } else {
                    tvHeader.setText("Attendance");
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentAttendanceStatusActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentName() {
        studentRef.child("student_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                tvStudentName.setText(name != null ? name : "Student");
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                tvStudentName.setText("Student");
            }
        });
    }

    private void loadCourseName() {
        courseRef.child("course_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                tvCourseName.setText(name != null ? name : "Course");
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                tvCourseName.setText("Course");
            }
        });
    }
}
