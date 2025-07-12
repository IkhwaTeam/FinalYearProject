package com.example.ikhwa;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.prolificinteractive.materialcalendarview.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class std_crs_att extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView tvPresent, tvAbsent, tvLeave, tvHeader, tvSummary, tvStudentName, tvCourseName;
    private DatabaseReference attendanceRef, studentRef, courseRef;
    private String uid, selectedDate, courseId, groupId;

    int totalPresent = 0, totalAbsent = 0, totalLeave = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_crs_att);

        calendarView = findViewById(R.id.calendarView);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);
        tvLeave = findViewById(R.id.tvAbsent1);
        tvHeader = findViewById(R.id.tvHeader);
        tvSummary = findViewById(R.id.tvSummary);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvCourseName = findViewById(R.id.tvCourseName);

        uid = FirebaseAuth.getInstance().getUid();
        courseId = getIntent().getStringExtra("courseId");
        groupId = getIntent().getStringExtra("groupId");

        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance").child(courseId).child(groupId);
        studentRef = FirebaseDatabase.getInstance().getReference("Student").child(uid);
        courseRef = FirebaseDatabase.getInstance().getReference("Courses/currentCourse").child(courseId);

        loadStudentName();
        loadCourseName();

        selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        loadAttendance(selectedDate);
        decorateCalendarAndCount();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date.getDate());
            loadAttendance(selectedDate);
        });
    }

    private void loadStudentName() {
        studentRef.child("student_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if (name != null) tvStudentName.setText(name);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadCourseName() {
        courseRef.child("course_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if (name != null) tvCourseName.setText(name);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAttendance(String selectedDate) {
        attendanceRef.child(selectedDate).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);

                tvPresent.setText("Present: " + totalPresent);
                tvAbsent.setText("Absent: " + totalAbsent);
                tvLeave.setText("Leave: " + totalLeave);

                if (status == null) {
                    tvSummary.setText("No attendance found for " + selectedDate);
                    return;
                }

                switch (status) {
                    case "P":
                        tvSummary.setText("You were Present on " + selectedDate);
                        break;
                    case "A":
                        tvSummary.setText("You were Absent on " + selectedDate);
                        break;
                    case "L":
                        tvSummary.setText("You were on Leave on " + selectedDate);
                        break;
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void decorateCalendarAndCount() {
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CalendarDay> presentDates = new ArrayList<>();
                List<CalendarDay> absentDates = new ArrayList<>();
                List<CalendarDay> leaveDates = new ArrayList<>();

                totalPresent = 0;
                totalAbsent = 0;
                totalLeave = 0;

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    String dateStr = dateSnap.getKey();
                    String status = dateSnap.child(uid).getValue(String.class);

                    if (status == null) continue;

                    try {
                        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        CalendarDay day = CalendarDay.from(cal);

                        switch (status) {
                            case "P":
                                presentDates.add(day);
                                totalPresent++;
                                break;
                            case "A":
                                absentDates.add(day);
                                totalAbsent++;
                                break;
                            case "L":
                                leaveDates.add(day);
                                totalLeave++;
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Drawable greenCircle = ContextCompat.getDrawable(std_crs_att.this, R.drawable.circle_green);
                Drawable redCircle = ContextCompat.getDrawable(std_crs_att.this, R.drawable.circle_red);
                Drawable yellowCircle = ContextCompat.getDrawable(std_crs_att.this, R.drawable.circle_yellow);

                calendarView.addDecorator(new CircleDecorator(greenCircle, presentDates));
                calendarView.addDecorator(new CircleDecorator(redCircle, absentDates));
                calendarView.addDecorator(new CircleDecorator(yellowCircle, leaveDates));

                tvPresent.setText("Present: " + totalPresent);
                tvAbsent.setText("Absent: " + totalAbsent);
                tvLeave.setText("Leave: " + totalLeave);

                int total = totalPresent + totalAbsent + totalLeave;
                if (total > 0) {
                    double percentage = (totalPresent * 100.0) / total;
                    tvHeader.setText("Attendance\n(" + String.format(Locale.getDefault(), "%.1f", percentage) + "% Present)");
                } else {
                    tvHeader.setText("Attendance");
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ✅ CircleDecorator class
    public static class CircleDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        private final Drawable drawable;

        public CircleDecorator(Drawable drawable, Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
            this.drawable = drawable;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(drawable);
        }
    }
}
