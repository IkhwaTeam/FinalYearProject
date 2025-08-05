package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.TeacherListAdapter1;
import com.example.ikhwa.modules.TeachersModels;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class Approve_Rejected_Activity1 extends AppCompatActivity {

    private RecyclerView approvedRecyclerView, rejectedRecyclerView;
    private ProgressBar progressBar;
    private DatabaseReference teacherRef;

    private List<TeachersModels> approvedList = new ArrayList<>();
    private List<TeachersModels> rejectedList = new ArrayList<>();
    private TeacherListAdapter1 approvedAdapter, rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approave_rejected1);

        approvedRecyclerView = findViewById(R.id.approved_recycler);
        rejectedRecyclerView = findViewById(R.id.rejected_recycler);
        progressBar = findViewById(R.id.progress_bar);

        approvedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rejectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        approvedAdapter = new TeacherListAdapter1(approvedList);
        rejectedAdapter = new TeacherListAdapter1(rejectedList);

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
                    TeachersModels teacher = teacherSnap.getValue(TeachersModels.class);
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
                    Toast.makeText(Approve_Rejected_Activity1.this, "No approved or rejected teachers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Approve_Rejected_Activity1.this, "Database error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
