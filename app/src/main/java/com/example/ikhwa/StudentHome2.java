package com.example.ikhwa;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.student_home2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        studentNameText = findViewById(R.id.st_name);
        studentEmailText = findViewById(R.id.st_email);
        layout = findViewById(R.id.st_add_your); // initialize layout

        // Initialize RecyclerView
        courseRecycler = findViewById(R.id.recyclerCourseContainer);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);
        courseRecycler.setAdapter(courseAdapter);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(uid);

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String name = task.getResult().child("student_name").getValue(String.class);
                    String email = task.getResult().child("email").getValue(String.class);
                    studentNameText.setText(name);
                    studentEmailText.setText(email);

                    // Load enrolled courses after getting UID
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
                startActivity(new Intent(StudentHome2.this, YourCoursesActivity.class));
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

        courseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Course course = snapshot.getValue(Course.class);
                if (course != null) {
                    courseList.add(course);
                    courseAdapter.notifyItemInserted(courseList.size() - 1);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showEnrolledCourses(String uid) {
        Set<String> enrolledCourses = EnrolledCoursesManager.getEnrolledCourses(this, uid);
        List<String> courseList = new ArrayList<>(enrolledCourses);

        layout.removeAllViews(); // clear previous views

        for (int i = 0; i < courseList.size(); i++) {
            String title = courseList.get(i);
            View courseView = getLayoutInflater().inflate(R.layout.your_course_card, null);

            TextView tvCourseTitle = courseView.findViewById(R.id.tv_course_title);
            TextView tvChapterCount = courseView.findViewById(R.id.tv_chapter_count);
            TextView tvProgress = courseView.findViewById(R.id.tv_progress);

            LinearLayout cardLayout = courseView.findViewById(R.id.card_background_layout);

            tvCourseTitle.setText(title);
            tvChapterCount.setText("40 Days");
            tvProgress.setText("0/40 Completed");

            int colorResId = backgroundColors[i % backgroundColors.length];
            cardLayout.setBackgroundResource(colorResId);

            layout.addView(courseView);
        }
    }

    public void show_dialog() {
        myDialog.setContentView(R.layout.course_dialog_show);
        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            myDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ImageView close_btn = myDialog.findViewById(R.id.close_btn);
        if (close_btn != null) {
            close_btn.setOnClickListener(view -> myDialog.dismiss());
        }

        TextView intent_btn = myDialog.findViewById(R.id.crs_reg);
        if (intent_btn != null) {
            intent_btn.setOnClickListener(view -> {
                Intent intent = new Intent(StudentHome2.this, StudentHome2.class);
                startActivity(intent);
            });
        }

        myDialog.show();
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
