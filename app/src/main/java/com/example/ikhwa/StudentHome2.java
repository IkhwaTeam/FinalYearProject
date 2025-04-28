package com.example.ikhwa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class StudentHome2 extends AppCompatActivity {

    Dialog myDialog, myDialog2;
    TextView studentNameText, studentEmailText, notificationTitle, notificationCount, clickToOpen;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    int unseenCount = 0;
    String latestNotificationId = "";
    String latestNotificationTitle = "";
    String latestNotificationDesc = "";

    RecyclerView courseRecycler;
    List<Course> courseList;
    CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_home2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        studentNameText = findViewById(R.id.st_name);
        studentEmailText = findViewById(R.id.st_email);
        notificationTitle = findViewById(R.id.notification_update);
        notificationCount = findViewById(R.id.notification_count);
        clickToOpen = findViewById(R.id.notification_click_to_open);

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
                } else {
                    Toast.makeText(StudentHome2.this, "Failed to load student data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        myDialog = new Dialog(this);
        myDialog2 = new Dialog(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StudentHome2.this, stdprofile.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(StudentHome2.this, StudentHome2.class));
                return true;
            } else if (itemId == R.id.nav_setting) {
                startActivity(new Intent(StudentHome2.this, std_crs_att.class));
                return true;
            }
            return false;
        });

        loadNotification();
        loadCurrentCourses();
    }

    private void loadNotification() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
        DatabaseReference seenRef = FirebaseDatabase.getInstance().getReference("StudentSeenNotifications").child(uid);

        seenRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot seenSnapshot = task.getResult();
                notificationRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String notifId = snapshot.getKey();
                        String title = snapshot.child("title").getValue(String.class);
                        String description = snapshot.child("description").getValue(String.class);

                        if (!seenSnapshot.hasChild(notifId)) {
                            unseenCount++;
                            notificationCount.setText(String.valueOf(unseenCount));
                            notificationCount.setVisibility(View.VISIBLE);
                            notificationTitle.setText("New Update");

                            latestNotificationId = notifId;
                            latestNotificationTitle = title;
                            latestNotificationDesc = description;

                            View.OnClickListener openNotification = v -> {
                                Intent intent = new Intent(StudentHome2.this, NotificationDesignActivity.class);
                                intent.putExtra("title", latestNotificationTitle);
                                intent.putExtra("description", latestNotificationDesc);
                                intent.putExtra("id", latestNotificationId);
                                intent.putExtra("role", "student");
                                startActivity(intent);

                                seenRef.child(latestNotificationId).setValue(true);

                                unseenCount = 0;
                                notificationCount.setVisibility(View.GONE);
                            };

                            notificationTitle.setOnClickListener(openNotification);
                            clickToOpen.setOnClickListener(openNotification);
                        }
                    }

                    @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
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
}
