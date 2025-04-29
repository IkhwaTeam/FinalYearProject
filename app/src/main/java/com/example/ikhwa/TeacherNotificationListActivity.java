package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TeacherNotificationListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> notificationList;
    private ArrayList<String> notificationIdList;
    private ArrayAdapter<String> adapter;
    private String teacherId = "IQII789"; // آپ کے ٹیچر کا ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_notification_list);

        listView = findViewById(R.id.tea_notification_list_view);
        notificationList = new ArrayList<>();
        notificationIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
        listView.setAdapter(adapter);

        loadNotifications();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            showDeleteConfirmation(position);
        });
    }

    private void loadNotifications() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        notificationRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationList.clear();
                notificationIdList.clear();

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String to = snapshot.child("to").getValue(String.class);
                    if ("Teacher".equals(to)) {
                        String title = snapshot.child("title").getValue(String.class);
                        String description = snapshot.child("description").getValue(String.class);
                        String notificationText = title + "\n" + description;

                        notificationList.add(notificationText);
                        notificationIdList.add(snapshot.getKey());
                    }
                }

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Clear Notification")
                .setMessage("Are you sure you want to clear this notification?")
                .setPositiveButton("Yes", (dialog, which) -> clearNotification(position))
                .setNegativeButton("No", null)
                .show();
    }

    private void clearNotification(int position) {
        String notificationId = notificationIdList.get(position);

        DatabaseReference seenRef = FirebaseDatabase.getInstance()
                .getReference("TeacherSeenNotifications")
                .child(teacherId)
                .child(notificationId);

        seenRef.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationList.remove(position);
                notificationIdList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Notification cleared.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to clear notification.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
