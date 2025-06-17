package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ApprovedRejectedTeachersActivity extends AppCompatActivity {

    private RecyclerView approvedRecyclerView, rejectedRecyclerView;
    private ProgressBar progressBar;
    private DatabaseReference teacherRef;

    private List<com.example.ikhwa.TeacherModel> approvedList = new ArrayList<>();
    private List<com.example.ikhwa.TeacherModel> rejectedList = new ArrayList<>();
    private TeacherListAdapter approvedAdapter, rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_rejected_teachers);

        approvedRecyclerView = findViewById(R.id.approved_recycler);
        rejectedRecyclerView = findViewById(R.id.rejected_recycler);
        progressBar = findViewById(R.id.progress_bar);

        approvedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rejectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        approvedAdapter = new TeacherListAdapter(approvedList);
        rejectedAdapter = new TeacherListAdapter(rejectedList);

        approvedRecyclerView.setAdapter(approvedAdapter);
        rejectedRecyclerView.setAdapter(rejectedAdapter);

        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");

        loadTeachers();
    }

    private void loadTeachers() {
        progressBar.setVisibility(View.VISIBLE);

        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                approvedList.clear();
                rejectedList.clear();

                for (DataSnapshot teacherSnap : snapshot.getChildren()) {
                    com.example.ikhwa.TeacherModel teacher = teacherSnap.getValue(com.example.ikhwa.TeacherModel.class);
                    if (teacher != null && teacher.getStatus() != null) {
                        if ("approved".equalsIgnoreCase(teacher.getStatus())) {
                            approvedList.add(teacher);
                        } else if ("rejected".equalsIgnoreCase(teacher.getStatus())) {
                            rejectedList.add(teacher);
                        }
                    }
                }

                approvedAdapter.notifyDataSetChanged();
                rejectedAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                if (approvedList.isEmpty() && rejectedList.isEmpty()) {
                    Toast.makeText(ApprovedRejectedTeachersActivity.this, "No approved or rejected teachers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ApprovedRejectedTeachersActivity.this, "Database error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
