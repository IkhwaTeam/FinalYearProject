package com.example.ikhwa;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.Course;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class StudentHome2 extends AppCompatActivity {

    Dialog myDialog, myDialog2;
    TextView studentNameText, studentEmailText, notificationBadge;
    DatabaseReference databaseReference;
    int[] backgroundColors = { R.drawable.bg_green, R.drawable.bg_blue, R.drawable.bg_red };
    LinearLayout layout;
    FirebaseAuth mAuth;
    RecyclerView courseRecycler;
    List<Course> courseList;
    CourseAdapter courseAdapter;
    Button notifybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.student_home2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        studentNameText = findViewById(R.id.st_name);
        studentEmailText = findViewById(R.id.st_email);
        layout = findViewById(R.id.st_add_your);
        notificationBadge = findViewById(R.id.notification_badge);
        notifybtn = findViewById(R.id.notification_click_to_open);

        courseRecycler = findViewById(R.id.recyclerCourseContainer);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);
        courseRecycler.setAdapter(courseAdapter);

        notifybtn.setOnClickListener(view -> {
            String uid = FirebaseAuth.getInstance().getUid();
            DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications");
            DatabaseReference readRef = FirebaseDatabase.getInstance().getReference("NotificationReads")
                    .child("Student").child(uid);

            notifRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DataSnapshot snap : task.getResult().getChildren()) {
                        readRef.child(snap.getKey()).setValue(true);
                    }
                    notificationBadge.setVisibility(View.GONE);
                    startActivity(new Intent(StudentHome2.this, StudentNotificationActivity.class));
                }
            });
        });

        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(uid);

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String name = task.getResult().child("student_name").getValue(String.class);
                    String email = task.getResult().child("email").getValue(String.class);
                    studentNameText.setText(name);
                    studentEmailText.setText(email);
                    showEnrolledCourses(uid);
                } else {
                    Toast.makeText(this, "Failed to load student data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        myDialog = new Dialog(this);
        myDialog2 = new Dialog(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, stdprofile.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, StudentNotificationActivity.class));
                return true;
            } else if (itemId == R.id.nav_setting) {
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            }
            return false;
        });

        loadCurrentCourses();
        checkUnreadNotifications();
    }

    private void checkUnreadNotifications() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications");
        DatabaseReference readRef = FirebaseDatabase.getInstance().getReference("NotificationReads")
                .child("Student").child(uid);

        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allNotifsSnapshot) {
                readRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot readSnapshot) {
                        int unreadCount = 0;
                        for (DataSnapshot notif : allNotifsSnapshot.getChildren()) {
                            String target = notif.child("target").getValue(String.class);
                            if (target != null && (target.equals("Student") || target.equals("all"))) {
                                if (!readSnapshot.hasChild(notif.getKey())) {
                                    unreadCount++;
                                }
                            }
                        }

                        if (unreadCount > 0) {
                            notificationBadge.setVisibility(View.VISIBLE);
                            notificationBadge.setText(String.valueOf(unreadCount));
                        } else {
                            notificationBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadCurrentCourses() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Courses/currentCourse");

        courseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                courseList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Course course = snapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(snapshot.getKey());
                        courseList.add(course);
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showEnrolledCourses(String uid) {
        DatabaseReference enrolledRef = FirebaseDatabase.getInstance().getReference("Enrollments").child(uid);
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Courses/currentCourse");

        layout.removeAllViews();

        enrolledRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot courseSnap : task.getResult().getChildren()) {
                    String courseId = courseSnap.getKey();

                    courseRef.child(courseId).get().addOnCompleteListener(courseTask -> {
                        if (courseTask.isSuccessful() && courseTask.getResult().exists()) {
                            Course course = courseTask.getResult().getValue(Course.class);
                            if (course != null) {
                                course.setId(courseId);

                                // ✅ Get first groupId from groups
                                if (courseTask.getResult().hasChild("groups")) {
                                    DataSnapshot groupsSnap = courseTask.getResult().child("groups");
                                    for (DataSnapshot group : groupsSnap.getChildren()) {
                                        String groupId = group.getKey();
                                        course.setGroupId(groupId);
                                        break;
                                    }
                                }

                                addCourseCard(course, uid);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addCourseCard(Course course, String uid) {
        View courseView = getLayoutInflater().inflate(R.layout.your_course_card, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 0, 16, 0);
        courseView.setLayoutParams(layoutParams);

        TextView tvCourseTitle = courseView.findViewById(R.id.tv_course_title);
        TextView tvChapterCount = courseView.findViewById(R.id.tv_chapter_count);
        TextView tvType = courseView.findViewById(R.id.course_type);
        TextView tvProgress = courseView.findViewById(R.id.tv_progress);
        ProgressBar progressBar = courseView.findViewById(R.id.progress_bar);
        RelativeLayout cardLayout = courseView.findViewById(R.id.card_background_layout);

        tvCourseTitle.setText(course.getTitle());
        tvChapterCount.setText(course.getDuration());
        tvType.setText(course.getType());

        int index = layout.getChildCount();
        int colorResId = backgroundColors[index % backgroundColors.length];
        cardLayout.setBackgroundResource(colorResId);

        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("Courses")
                .child(course.getId()).child("lectures");

        DatabaseReference attemptRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("attemptedLectures").child(course.getId());

        lecturesRef.get().addOnCompleteListener(lectureTask -> {
            if (lectureTask.isSuccessful()) {
                long totalLectures = lectureTask.getResult().getChildrenCount();

                attemptRef.get().addOnCompleteListener(attemptTask -> {
                    if (attemptTask.isSuccessful()) {
                        long attempted = attemptTask.getResult().getChildrenCount();
                        tvProgress.setText(attempted + "/" + totalLectures + " Completed");

                        if (totalLectures > 0) {
                            int percentage = (int) ((attempted * 100.0f) / totalLectures);
                            progressBar.setProgress(percentage);
                        } else {
                            progressBar.setProgress(0);
                        }
                    }
                });
            } else {
                tvProgress.setText("0/" + course.getDuration() + " Completed");
                progressBar.setProgress(0);
            }
        });

        Button btnView = courseView.findViewById(R.id.btn_view_lecture);
        btnView.setOnClickListener(view -> {
            if (course.getType().equalsIgnoreCase("Quiz Based")) {
                Intent intent = new Intent(StudentHome2.this, LectureListActivity.class);
                intent.putExtra("courseId", course.getId());
                startActivity(intent);
            } else if (course.getType().equalsIgnoreCase("Attendance Based")) {
                Intent intent = new Intent(StudentHome2.this, StudentAttendanceViewActivity.class);
                intent.putExtra("courseId", course.getId());
                intent.putExtra("groupId", course.getGroupId()); // ✅
                intent.putExtra("courseTitle", course.getTitle());
                startActivity(intent);
            } else {
                Toast.makeText(StudentHome2.this, "Unknown course type", Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(courseView);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
