package com.example.ikhwa;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ikhwa.modules.TeacherRequest;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class AdminApprovalActivity extends AppCompatActivity {

    private ListView listView;
    private PendingTeacherAdapter adapter;
    private ArrayList<TeacherRequest> teacherRequests;

    private DatabaseReference pendingRef, approvedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval);

        listView = findViewById(R.id.listView);
        teacherRequests = new ArrayList<>();
        adapter = new PendingTeacherAdapter(this, teacherRequests);
        listView.setAdapter(adapter);

        pendingRef = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");
        approvedRef = FirebaseDatabase.getInstance().getReference("ApprovedTeachers");

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        pendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherRequests.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TeacherRequest request = data.getValue(TeacherRequest.class);
                    teacherRequests.add(request);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminApprovalActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void approveTeacher(TeacherRequest request) {
        approvedRef.child(request.getId()).setValue(request).addOnSuccessListener(unused -> {
            pendingRef.child(request.getId()).removeValue();
            Toast.makeText(this, "Teacher Approved", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Approval Failed", Toast.LENGTH_SHORT).show());
    }

    public void rejectTeacher(TeacherRequest request) {
        pendingRef.child(request.getId()).removeValue().addOnSuccessListener(unused -> {
            Toast.makeText(this, "Teacher Rejected", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Rejection Failed", Toast.LENGTH_SHORT).show());
    }
}
