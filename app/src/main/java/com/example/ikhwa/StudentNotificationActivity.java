package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentNotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<NotificationModel> notificationList = new ArrayList<>();
    NotificationAdapter adapter;
    DatabaseReference notifRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notification);

        recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(this, notificationList,false);
        recyclerView.setAdapter(adapter);

        notifRef = FirebaseDatabase.getInstance().getReference("Notifications");

        loadNotifications();
    }

    private void loadNotifications() {
        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String id = snap.getKey();
                    String title = snap.child("title").getValue(String.class);
                    String desc = snap.child("description").getValue(String.class);
                    String time = snap.child("timestamp").getValue(String.class);
                    String target = snap.child("target").getValue(String.class);

                    // Show only if target is "students" or "all"
                    if (target != null && (target.equals("Student") || target.equals("all"))) {
                        notificationList.add(new NotificationModel(id, title, desc, time, target));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentNotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
