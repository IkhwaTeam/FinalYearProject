package com.example.ikhwa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private Button back_button, btnSendNotification;
    private EditText etTitle, etDescription;
    private Spinner spinnerRecipients;
    private LinearLayout notificationContainer;

    private DatabaseReference notificationRef;
    private String editingNotificationId = null;
    private View editingNotificationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        back_button = findViewById(R.id.back_btn);
        btnSendNotification = findViewById(R.id.btnSendNotification);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerRecipients = findViewById(R.id.spinnerRecipients);
        notificationContainer = findViewById(R.id.notificationContainer);

        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        back_button.setOnClickListener(view -> finish());

        btnSendNotification.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {
                // Show a confirmation dialog to confirm sending the notification
                showConfirmationDialog(title, description);
            } else {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog(final String title, final String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Notification")
                .setMessage("Do you want to send this notification?\n\nTitle: " + title + "\nDescription: " + description)
                .setPositiveButton("Send", (dialog, which) -> {
                    // Send the notification after confirmation
                    sendNotification(title, description);
                })
                .setNegativeButton("Edit", (dialog, which) -> {
                    // Allow the admin to edit the notification
                    etTitle.setText(title);
                    etDescription.setText(description);
                })
                .setNeutralButton("Delete", (dialog, which) -> {
                    // Allow the admin to delete the notification
                    etTitle.setText("");
                    etDescription.setText("");
                    Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
                });

        builder.create().show();
    }

    private void sendNotification(String title, String description) {
        if (editingNotificationId == null) {
            // New notification
            String key = notificationRef.push().getKey();

            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("description", description);

            notificationRef.child(key).setValue(map);

            Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();

            // Add notification to layout without edit/delete buttons initially
            addNotificationToLayout(key, title, description, true);  // 'true' allows editing after sending
        } else {
            // Update existing notification
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("title", title);
            updateMap.put("description", description);

            notificationRef.child(editingNotificationId).updateChildren(updateMap);

            // Update the view content
            if (editingNotificationView != null) {
                TextView tvTitle = editingNotificationView.findViewById(R.id.tvTitle);
                TextView tvDescription = editingNotificationView.findViewById(R.id.tvDescription);
                tvTitle.setText(title);
                tvDescription.setText(description);
            }

            Toast.makeText(this, "Notification updated", Toast.LENGTH_SHORT).show();

            // Reset editing state
            editingNotificationId = null;
            editingNotificationView = null;
            btnSendNotification.setText("Send Notification");
        }

        // Reset input fields after sending
        etTitle.setText("");
        etDescription.setText("");
    }

    private void addNotificationToLayout(String id, String title, String description, boolean isEditable) {
        View notificationView = LayoutInflater.from(this).inflate(R.layout.item_notification_admin, null);

        TextView tvTitle = notificationView.findViewById(R.id.tvTitle);
        TextView tvDescription = notificationView.findViewById(R.id.tvDescription);
        Button btnEdit = notificationView.findViewById(R.id.btnEdit);
        Button btnDelete = notificationView.findViewById(R.id.btnDelete);

        tvTitle.setText(title);
        tvDescription.setText(description);

        // Only show edit and delete buttons if the notification is editable (i.e., after it has been sent)
        if (isEditable) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        btnEdit.setOnClickListener(v -> {
            etTitle.setText(title);
            etDescription.setText(description);
            editingNotificationId = id;
            editingNotificationView = notificationView;
            btnSendNotification.setText("Update Notification");
        });

        btnDelete.setOnClickListener(v -> {
            notificationRef.child(id).removeValue();
            notificationContainer.removeView(notificationView);
            Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();

            // Reset editing if the one being edited is deleted
            if (editingNotificationId != null && editingNotificationId.equals(id)) {
                etTitle.setText("");
                etDescription.setText("");
                btnSendNotification.setText("Send Notification");
                editingNotificationId = null;
                editingNotificationView = null;
            }
        });

        notificationContainer.addView(notificationView, 0);
    }
}
