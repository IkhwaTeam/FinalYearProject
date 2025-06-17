package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class PendingTeachersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TeacherAdapter teacherAdapter;
    private List<com.example.ikhwa.TeacherModel> teacherList;
    private DatabaseReference teacherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_teachers);

        recyclerView = findViewById(R.id.recyclerView_pending_teachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        teacherList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(teacherList, this);

        recyclerView.setAdapter(teacherAdapter);

        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");

        loadPendingTeachers();
    }

    private void loadPendingTeachers() {
        teacherRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teacherList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            TeacherModel teacher = dataSnapshot.getValue(TeacherModel.class);
                            teacher.setTeacherId(dataSnapshot.getKey());
                            teacherList.add(teacher);
                        }
                        teacherAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PendingTeachersActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
