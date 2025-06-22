package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.GroupStudent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupForCourseActivity extends AppCompatActivity {

    private DatabaseReference courseRef;
    private String courseId;
    RecyclerView groupRecyclerView;
    GroupAdapter groupAdapter;
    private Map<String, String> teacherNames = new HashMap<>();
    Map<String, List<GroupStudent>> groupData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_for_course); // create this layout

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());
        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseId = getIntent().getStringExtra("courseId");

        if (courseId != null) {
            courseRef = FirebaseDatabase.getInstance()
                    .getReference("Courses")
                    .child("currentCourse")
                    .child(courseId);

            createFirstGroupIfNotExists(courseId);
        } else {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFirstGroupIfNotExists(String courseId) {
        DatabaseReference group1Ref = courseRef.child("groups").child("group_1");

        group1Ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupData.clear();
                teacherNames.clear(); // clear teacherNames too

                for (DataSnapshot groupSnap : snapshot.getChildren()) {
                    String groupName = groupSnap.getKey();

                    // Fetch teacher's name
                    String assignedTeacherName = groupSnap.child("assignedTeacherName").getValue(String.class);
                    if (assignedTeacherName != null) {
                        teacherNames.put(groupName, assignedTeacherName);
                    }

                    List<GroupStudent> students = new ArrayList<>();
                    DataSnapshot studentsSnap = groupSnap.child("students");
                    for (DataSnapshot studentSnap : studentsSnap.getChildren()) {
                        GroupStudent student = studentSnap.getValue(GroupStudent.class);
                        if (student != null) {
                            students.add(student);
                        }
                    }
                    groupData.put(groupName, students);
                }

                List<String> groupNames = new ArrayList<>(groupData.keySet());
                groupAdapter = new GroupAdapter(CreateGroupForCourseActivity.this, groupNames, groupData, courseId, teacherNames);
                groupRecyclerView.setAdapter(groupAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateGroupForCourseActivity.this, "Error checking group", Toast.LENGTH_SHORT).show();
            }
        });

        courseRef.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupData.clear();
                teacherNames.clear(); // also clear teacher names to avoid stale data

                for (DataSnapshot groupSnap : snapshot.getChildren()) {
                    String groupName = groupSnap.getKey();

                    // Fetch teacher's name
                    String assignedTeacherName = groupSnap.child("assignedTeacherName").getValue(String.class);
                    if (assignedTeacherName != null) {
                        teacherNames.put(groupName, assignedTeacherName);
                    }

                    List<GroupStudent> students = new ArrayList<>();
                    DataSnapshot studentsSnap = groupSnap.child("students");
                    for (DataSnapshot studentSnap : studentsSnap.getChildren()) {
                        GroupStudent student = studentSnap.getValue(GroupStudent.class);
                        if (student != null) {
                            students.add(student);
                        }
                    }
                    groupData.put(groupName, students);
                }

                List<String> groupNames = new ArrayList<>(groupData.keySet());
                groupAdapter = new GroupAdapter(CreateGroupForCourseActivity.this, groupNames, groupData, courseId, teacherNames);
                groupRecyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateGroupForCourseActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
