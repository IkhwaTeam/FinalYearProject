package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class TeacherNotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<NotificationModel> notificationList;
    NotificationAdapter adapter;

    DatabaseReference notifRef, readRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_notification);

        recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, false); // false: no edit/delete
        recyclerView.setAdapter(adapter);

        uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        notifRef = FirebaseDatabase.getInstance().getReference("Notifications");
        readRef = FirebaseDatabase.getInstance().getReference("NotificationReads")
                .child("Teacher").child(uid);

        loadAndMarkNotifications();
    }

    private void loadAndMarkNotifications() {
        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String id = snap.getKey();
                    String title = snap.child("title").getValue(String.class);
                    String desc = snap.child("description").getValue(String.class);
                    String time = snap.child("timestamp").getValue(String.class);
                    String target = snap.child("target").getValue(String.class);

                    if (target != null &&
                            (target.equalsIgnoreCase("Teacher") || target.equalsIgnoreCase("All"))) {
                        notificationList.add(new NotificationModel(id, title, desc, time, target));

                        // ✅ Mark this notification as read
                        readRef.child(id).setValue(true);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherNotificationActivity.this,
                        "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
