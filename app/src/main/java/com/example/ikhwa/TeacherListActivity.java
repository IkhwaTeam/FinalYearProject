package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherListActivity extends AppCompatActivity implements TeacherFormDialog.TeacherDataListener {

    private RecyclerView teachersRecyclerView;
    private TeacherAdapter adapter;
    private List<TeacherDetails> teacherList = new ArrayList<>();
    private HashMap<String, TeacherDetails> teacherDataMap = new HashMap<>();
    private Button back_button;

    private DatabaseReference teacherRef; // Firebase Database Reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        // Initialize Firebase Database reference
        teacherRef = FirebaseDatabase.getInstance().getReference("teacherProfiles");

        // Initialize RecyclerView
        teachersRecyclerView = findViewById(R.id.teacherslist);
        teachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherAdapter(teacherList);
        teachersRecyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchTeachersFromFirebase();

        // FAB action
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            TeacherFormDialog dialogFragment = new TeacherFormDialog();
            dialogFragment.show(getSupportFragmentManager(), "TeacherFormDialog");
        });

        // Back button
        back_button = findViewById(R.id.btn_back);
        back_button.setOnClickListener(v -> finish());
    }

    // Method to fetch teacher data from Firebase
    private void fetchTeachersFromFirebase() {
        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing teacher data
                teacherList.clear();

                // Loop through the snapshot and retrieve teacher data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TeacherDetails teacher = snapshot.getValue(TeacherDetails.class);
                    if (teacher != null) {
                        teacherList.add(teacher); // Add teacher to the list
                    }
                }

                // Notify adapter to update the RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @Override
    public void onTeacherDataEntered(TeacherDetails teacher) {
        // Add the teacher data to Firebase
        String teacherId = teacher.getId();  // Ensure this is unique
        teacherRef.child(teacherId).setValue(teacher);

        // Add to local list for RecyclerView update
        teacherDataMap.put(teacherId, teacher);
        teacherList.add(teacher); // Add to list
        adapter.notifyItemInserted(teacherList.size() - 1); // Notify adapter that item has been inserted
    }

    @Override
    public void onTeacherDataUpdated(TeacherDetails updatedTeacher) {
        // Update teacher data in Firebase
        String teacherId = updatedTeacher.getId();
        teacherRef.child(teacherId).setValue(updatedTeacher);

        // Find and update teacher in local list
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getId().equals(teacherId)) {
                teacherList.set(i, updatedTeacher); // Replace with updated data
                adapter.notifyItemChanged(i); // Notify adapter to refresh this item
                break;
            }
        }
    }

    // Method to fetch teacher details by their ID
    public TeacherDetails getTeacherDetailsById(String id) {
        return teacherDataMap.get(id);
    }
}
