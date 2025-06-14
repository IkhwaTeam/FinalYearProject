package com.example.ikhwa;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminNotificationActivity extends AppCompatActivity {

    EditText etTitle, etDescription;
    Spinner spinnerTarget;
    Button btnSend;
    RecyclerView recyclerView;
    List<NotificationModel> notificationList = new ArrayList<>();
    NotificationAdapter adapter;

    DatabaseReference notifRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerTarget = findViewById(R.id.spinnerTarget);
        btnSend = findViewById(R.id.btnsend);
        recyclerView = findViewById(R.id.recyclerNotifications);

        notifRef = FirebaseDatabase.getInstance().getReference("Notifications");

        String[] targets = {"all", "Student", "Teacher"};

        // ✅ Custom layout used for spinner text color
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // 👈 Custom layout
                targets
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdown style
        spinnerTarget.setAdapter(adapterSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, notificationList, true);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendNotification());
        loadNotifications();
    }

    private void sendNotification() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String target = spinnerTarget.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = notifRef.push().getKey();
        String timestamp = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(new Date());

        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("timestamp", timestamp);
        map.put("target", target);

        notifRef.child(id).setValue(map).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show();
            etTitle.setText("");
            etDescription.setText("");
        });
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
                    notificationList.add(new NotificationModel(id, title, desc, time, target));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminNotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
