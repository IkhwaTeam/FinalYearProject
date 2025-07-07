package com.example.ikhwa;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    TextView studentNameText, studentEmailText;
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

        courseRecycler = findViewById(R.id.recyclerCourseContainer);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);
        courseRecycler.setAdapter(courseAdapter);

        notifybtn = findViewById(R.id.notification_click_to_open);
        notifybtn.setOnClickListener(view -> {
            Intent intent = new Intent(StudentHome2.this, StudentNotificationActivity.class);
            startActivity(intent);
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
                    Toast.makeText(StudentHome2.this, "Failed to load student data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        myDialog = new Dialog(this);
        myDialog2 = new Dialog(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StudentHome2.this, stdprofile.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(StudentHome2.this, StudentNotificationActivity.class));
                return true;
            } else if (itemId == R.id.nav_setting) {
                startActivity(new Intent(StudentHome2.this, SettingActivity.class));
                return true;
            }
            return false;
        });

        loadCurrentCourses();
    }

    private void loadCurrentCourses() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Courses/currentCourse");

        courseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                courseList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Course course = snapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(snapshot.getKey()); // Set courseId
                        courseList.add(course);
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showEnrolledCourses(String uid) {
        DatabaseReference enrolledRef = FirebaseDatabase.getInstance().getReference("Enrollments").child(uid);
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Courses").child("currentCourse");

        layout.removeAllViews();

        enrolledRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot courseSnap : task.getResult().getChildren()) {
                    String courseId = courseSnap.getKey();

                    courseRef.child(courseId).get().addOnCompleteListener(courseTask -> {
                        if (courseTask.isSuccessful() && courseTask.getResult().exists()) {
                            Course course = courseTask.getResult().getValue(Course.class);
                            if (course != null) {
                                course.setId(courseId); // set course ID manually
                                addCourseCard(course);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addCourseCard(Course course) {
        View courseView = getLayoutInflater().inflate(R.layout.your_course_card, null);

        // Add margin between cards
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
        RelativeLayout cardLayout = courseView.findViewById(R.id.card_background_layout);

        tvCourseTitle.setText(course.getTitle());
        tvChapterCount.setText(course.getDuration());
        tvType.setText(course.getType());
        tvProgress.setText("0/" + course.getDuration() + " Completed");

        int index = layout.getChildCount();
        int colorResId = backgroundColors[index % backgroundColors.length];
        cardLayout.setBackgroundResource(colorResId);

        // Course click action based on type
        courseView.setOnClickListener(view -> {
            if (course.getType().equalsIgnoreCase("Quiz Based")) {
                Intent intent = new Intent(StudentHome2.this, LectureListActivity.class);
                intent.putExtra("courseId", course.getId());
                startActivity(intent);
            } else if (course.getType().equalsIgnoreCase("Attendance Based")) {
                show_dialog2();
            } else {
                Toast.makeText(StudentHome2.this, "Unknown course type", Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(courseView);
    }

    public void show_dialog2() {
        myDialog2.setContentView(R.layout.your_course_dialog);
        if (myDialog2.getWindow() != null) {
            myDialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            myDialog2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ImageView close_btn2 = myDialog2.findViewById(R.id.close_btn1);
        if (close_btn2 != null) {
            close_btn2.setOnClickListener(view -> myDialog2.dismiss());
        }

        TextView intent_btn = myDialog2.findViewById(R.id.view_att);
        if (intent_btn != null) {
            intent_btn.setOnClickListener(view -> {
                Intent intent = new Intent(StudentHome2.this, std_crs_att.class);
                startActivity(intent);
            });
        }

        myDialog2.show();
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
