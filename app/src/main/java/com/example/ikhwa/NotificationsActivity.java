package com.example.ikhwa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnSendNotification;
    private LinearLayout notificationContainer;
    private Spinner spinnerRecipients;

    private DatabaseReference notificationRef;
    private String editingNotificationId = null;
    private View editingNotificationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSendNotification = findViewById(R.id.btn_send_notification);
        notificationContainer = findViewById(R.id.notificationContainer);
        spinnerRecipients = findViewById(R.id.spinnerRecipients);

        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipients_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecipients.setAdapter(adapter);

        btnSendNotification.setOnClickListener(view -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(NotificationsActivity.this, "Please enter both title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmationDialog(title, description);
        });

        loadNotifications();
    }

    private void sendNotification(String title, String description) {
        long timestamp = System.currentTimeMillis();

        if (editingNotificationId == null) {
            String key = notificationRef.push().getKey();

            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("description", description);
            map.put("recipient", spinnerRecipients.getSelectedItem().toString());
            map.put("timestamp", timestamp);

            notificationRef.child(key).setValue(map);
            Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();

            addNotificationToLayout(key, title, description, timestamp, true);
        } else {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("title", title);
            updateMap.put("description", description);
            updateMap.put("recipient", spinnerRecipients.getSelectedItem().toString());
            updateMap.put("timestamp", timestamp);

            notificationRef.child(editingNotificationId).updateChildren(updateMap);

            if (editingNotificationView != null) {
                TextView tvTitle = editingNotificationView.findViewById(R.id.tvTitle);
                TextView tvDescription = editingNotificationView.findViewById(R.id.tvDescription);
                tvTitle.setText(title);
                tvDescription.setText(description);
            }

            Toast.makeText(this, "Notification updated", Toast.LENGTH_SHORT).show();

            editingNotificationId = null;
            editingNotificationView = null;
            btnSendNotification.setText("Send Notification");
        }

        etTitle.setText("");
        etDescription.setText("");
    }

    private void loadNotifications() {
        notificationRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String key = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    addNotificationToLayout(key, title, description, timestamp, false);
                }
            }
        });
    }

    private void addNotificationToLayout(String key, String title, String description, long timestamp, boolean scrollToBottom) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_notification_admin, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvTimestamp = view.findViewById(R.id.tvTimestamp);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        tvTitle.setText(title);
        tvDescription.setText(description);
        tvTimestamp.setText(formatTimestamp(timestamp));

        btnEdit.setOnClickListener(v -> {
            etTitle.setText(title);
            etDescription.setText(description);
            editingNotificationId = key;
            editingNotificationView = view;
            btnSendNotification.setText("Update Notification");
        });

        btnDelete.setOnClickListener(v -> {
            notificationRef.child(key).removeValue();
            notificationContainer.removeView(view);
            Toast.makeText(NotificationsActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
        });

        notificationContainer.addView(view);
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void showConfirmationDialog(String title, String description) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setMessage("Do you want to send, edit, or delete this notification?")
                .setPositiveButton("Send", (dialog, which) -> sendNotification(title, description))
                .setNeutralButton("Edit", (dialog, which) -> {
                    etTitle.setText(title);
                    etDescription.setText(description);
                    btnSendNotification.setText("Update Notification");
                    Toast.makeText(this, "You can now edit the notification", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Clear", (dialog, which) -> {
                    etTitle.setText("");
                    etDescription.setText("");
                    Toast.makeText(this, "Notification content cleared", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}