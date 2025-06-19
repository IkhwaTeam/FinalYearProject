package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.List;

public class Student_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<GroupStudent> studentList;
    private StudentListAdapter adapter;
    private DatabaseReference courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        adapter = new StudentListAdapter(studentList);
        recyclerView.setAdapter(adapter);

        String groupName = getIntent().getStringExtra("groupName");
        loadGroupStudents(groupName);
    }

    private void loadGroupStudents(String groupName) {
        courseRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses").child("currentCourse").child("groups").child(groupName).child("students");

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    GroupStudent student = studentSnap.getValue(GroupStudent.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Student_List.this, "Failed to fetch students", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
