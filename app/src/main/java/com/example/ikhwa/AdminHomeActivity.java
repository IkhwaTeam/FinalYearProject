package com.example.ikhwa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AdminHomeActivity extends AppCompatActivity {

    private static final String TAG = "AdminHomeActivity";

    private ImageButton courseBtn, teacherBtn, studentBtn, notificationBtn, menuBtn;
    private TextView courseCount, teacherCount, studentCount, notificationCount,studedils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Buttons
        courseBtn = findViewById(R.id.course_btn);
        teacherBtn = findViewById(R.id.teacher_btn);
        studentBtn = findViewById(R.id.student_btn);
        notificationBtn = findViewById(R.id.notification_btn);
        menuBtn = findViewById(R.id.menuButton);
studedils=findViewById(R.id.student_details);
        // Count TextViews
        courseCount = findViewById(R.id.no_of_courses);
        teacherCount = findViewById(R.id.no_of_teachers);
        studentCount = findViewById(R.id.no_of_students);
        notificationCount = findViewById(R.id.no_of_items);


        loadCountsFromFirebase();
        setupListeners();
        listenForPendingTeacherRequests();
    }

    private void loadCountsFromFirebase() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // Courses Count (from multiple sources)
        db.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCourses = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    if (key != null && key.equals("currentCourse")) {
                        totalCourses += (int) child.getChildrenCount();
                    } else if (key != null && key.equals("previousCourse")) {
                        totalCourses += (int) child.getChildrenCount();
                    } else {
                        totalCourses++;
                    }
                }

                courseCount.setText(totalCourses + " Courses");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                courseCount.setText("0 Courses");
            }
        });

        // Teachers Count
        db.child("Teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                teacherCount.setText(count + " Teachers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                teacherCount.setText("0 Teachers");
            }
        });

        // Students Count
        db.child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                studentCount.setText(count + " Students");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                studentCount.setText("0 Students");
            }
        });

        // Notifications Count
        db.child("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                notificationCount.setText(count + " Notifications");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notificationCount.setText("0 Notifications");
            }
        });
    }

    private void setupListeners() {
        courseBtn.setOnClickListener(view -> startActivity(new Intent(this, CourseActivity.class)));
        teacherBtn.setOnClickListener(view -> startActivity(new Intent(this, PendingTeachersActivity.class)));
        studentBtn.setOnClickListener(view -> startActivity(new Intent(this, Students_Details_Activity.class)));
        notificationBtn.setOnClickListener(view -> startActivity(new Intent(this, AdminNotificationActivity.class)));
        studedils.setOnClickListener(view -> startActivity(new Intent(this, ApprovedRejectedTeachersActivity.class)));
        menuBtn.setOnClickListener(this::showPopupMenu);
    }

    private void listenForPendingTeacherRequests() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String status = snap.child("status").getValue(String.class);
                    if ("pending".equals(status)) {
                        String name = snap.child("name").getValue(String.class);
                        String key = snap.getKey();
                        showNotificationCard(name, key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, 0, 0, R.style.PopupMenuStyle);
        popupMenu.getMenuInflater().inflate(R.menu.logout, popupMenu.getMenu());

        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem menuItem = popupMenu.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(menuItem.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#242E67")), 0, spanString.length(), 0);
            menuItem.setTitle(spanString);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                logoutUser();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void logoutUser() {
        Log.d(TAG, "Logging out user");

        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SplashActivity.ROLE_KEY);
        editor.apply();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(AdminHomeActivity.this, SelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void showNotificationCard(String name, String key) {
        Log.d(TAG, "New Pending Request: " + name + " (" + key + ")");
    }
}
