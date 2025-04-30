package com.example.ikhwa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;



public class Teacher_home extends AppCompatActivity {

    Button t_staff_see_more, t_course_see_more;
    TextView tv_analytics;
     // RecyclerView for notifications
    TeacherAdapter teacherAdapter;  // Adapter for RecyclerView
    List<TeacherDetails> teacherList = new ArrayList<>();  // List for teacher data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_home);

        // ✅ Firebase Notification Listener
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Notifications");

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);

            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // ✅ Initialize RecyclerView and Adapter


        teacherAdapter = new TeacherAdapter(teacherList);

        // ✅ Load Teacher Data (Optional)
        loadTeacherData();

        // ✅ Button Initialization
        t_staff_see_more = findViewById(R.id.tbtn_see_staff);
        t_course_see_more = findViewById(R.id.tbtn_see_course);
        tv_analytics = findViewById(R.id.tvt_analytics);

        // ✅ Button Clicks
        t_course_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Teacher_home.this, TeacherCourseActivity.class);
                startActivity(intent);
            }
        });

        t_staff_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Teacher_home.this, StafftActivity.class);
                startActivity(intent);
            }
        });

        tv_analytics.setOnClickListener(v -> {
            Intent intent = new Intent(Teacher_home.this, AnalyticsTeacherActivity.class);
            startActivity(intent);
        });
    }

    private void loadTeacherData() {
        // Here you can load teacher data for RecyclerView if needed
    }
}
