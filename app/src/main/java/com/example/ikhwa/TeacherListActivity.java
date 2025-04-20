package com.example.ikhwa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherListActivity extends AppCompatActivity implements TeacherFormDialog.TeacherDataListener {

    private RecyclerView teachersRecyclerView;
    private TeacherAdapter adapter;
    private List<TeacherDetails> teacherList = new ArrayList<>();
    private HashMap<String, TeacherDetails> teacherDataMap = new HashMap<>();
    private Button back_button;

    private DatabaseReference teacherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        teacherRef = FirebaseDatabase.getInstance().getReference("teacherProfiles");

        teachersRecyclerView = findViewById(R.id.teacherslist);
        teachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherAdapter(teacherList);
        teachersRecyclerView.setAdapter(adapter);

        fetchTeachersFromFirebase();

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            TeacherFormDialog dialogFragment = new TeacherFormDialog();
            dialogFragment.show(getSupportFragmentManager(), "TeacherFormDialog");
        });

        back_button = findViewById(R.id.btn_back);
        back_button.setOnClickListener(v -> finish());
    }

    private void fetchTeachersFromFirebase() {
        teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TeacherListActivity", "Data changed");

                teacherList.clear();
                teacherDataMap.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TeacherDetails teacher = snapshot.getValue(TeacherDetails.class);
                    if (teacher != null && !teacherDataMap.containsKey(teacher.getId())) {
                        teacherList.add(teacher);
                        teacherDataMap.put(teacher.getId(), teacher);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TeacherListActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTeacherDataEntered(TeacherDetails teacher) {
        // Directly save to Firebase — let Firebase trigger onDataChange
        teacherRef.child(teacher.getId()).setValue(teacher);
    }


    @Override
    public void onTeacherDataUpdated(TeacherDetails updatedTeacher) {
        teacherRef.child(updatedTeacher.getId()).setValue(updatedTeacher);
    }

    public TeacherDetails getTeacherDetailsById(String id) {
        return teacherDataMap.get(id);
    }
}
