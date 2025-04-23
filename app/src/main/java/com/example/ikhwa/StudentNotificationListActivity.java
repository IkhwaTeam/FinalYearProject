package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentNotificationListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> notificationTitles = new ArrayList<>();
    ArrayList<String> notificationIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    TextView countTextView;

    DatabaseReference notificationRef, seenRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notification_list);

        listView = findViewById(R.id.notification_list_view);
        countTextView = findViewById(R.id.unseen_count_text);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationTitles);
        listView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
            seenRef = FirebaseDatabase.getInstance().getReference("StudentSeenNotifications").child(uid);

            loadNotifications(uid);
        }
    }

    private void loadNotifications(String uid) {
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationTitles.clear();
                notificationIds.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String notifId = dataSnapshot.getKey();
                    String title = dataSnapshot.child("title").getValue(String.class);

                    if (notifId != null && title != null) {
                        notificationTitles.add(title);
                        notificationIds.add(notifId);
                    }
                }
                adapter.notifyDataSetChanged();
                updateUnseenCount(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // Set item click listener to show notification details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String notificationId = notificationIds.get(position);
            String title = notificationTitles.get(position);

            // Fetch the description of the notification from Firebase
            notificationRef.child(notificationId).child("description").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String description = snapshot.getValue(String.class);

                    // Start the NotificationDesignActivity with the fetched data
                    Intent intent = new Intent(StudentNotificationListActivity.this, NotificationDesignActivity.class);
                    intent.putExtra("id", notificationId);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("role", "student");
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            // Once the notification is clicked, mark it as seen and delete it from Firebase
            deleteNotification(notificationId);
        });
    }

    private void deleteNotification(String notificationId) {
        // Mark notification as seen and delete it from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            seenRef.child(notificationId).setValue(true);  // Mark as seen
            notificationRef.child(notificationId).removeValue();  // Delete notification from Firebase
            updateUnseenCount(user.getUid()); // Update unseen count after deletion
        }
    }

    private void updateUnseenCount(String uid) {
        seenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unseenCount = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (!childSnapshot.getValue(Boolean.class)) {
                        unseenCount++;
                    }
                }

                // Update the unseen count in UI
                if (unseenCount == 0) {
                    countTextView.setVisibility(View.GONE);
                } else {
                    countTextView.setText("Unseen: " + unseenCount);
                    countTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
