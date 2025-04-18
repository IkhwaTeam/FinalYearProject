package com.example.ikhwa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import java.util.Map;
import java.util.HashMap;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private Button back_button, btnSendNotification;
    private EditText etTitle, etDescription;
    private Spinner spinnerRecipients;
    private LinearLayout notificationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize UI Components
        back_button = findViewById(R.id.back_btn);
        btnSendNotification = findViewById(R.id.btnSendNotification);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerRecipients = findViewById(R.id.spinnerRecipients);
        notificationContainer = findViewById(R.id.notificationContainer);

        // Back Button Click Listener
        back_button.setOnClickListener(view -> finish());

        // Send Notification Action
        // Inside your onClick of the Send Notification Button:
        btnSendNotification.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {
                // 1. Firebase پر save کرنا
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");
                String key = ref.push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("description", description);

                ref.child(key).setValue(map);

                Toast.makeText(NotificationsActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();

                // 2. Custom Notification بھیجنا
                Notificationclass.showNotificationDesignActivity(NotificationsActivity.this, title, description);
            } else {
                Toast.makeText(NotificationsActivity.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Add notification to the LinearLayout dynamically
    private void addNotification() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String recipient = spinnerRecipients.getSelectedItem().toString();

        if (!title.isEmpty() && !description.isEmpty()) {
            // Inflate new notification layout
            View notificationView = LayoutInflater.from(this).inflate(R.layout.item_notification, null);

            // Reference notification layout views
            TextView tvNotificationTitle = notificationView.findViewById(R.id.tr1);
            TextView tvNotificationDescription = notificationView.findViewById(R.id.tr1);

            // Set notification content
            tvNotificationTitle.setText("Ikhwa ." + getCurrentTime() + " min");
            tvNotificationDescription.setText(title + " - " + description);

            // Add notification to the container
            notificationContainer.addView(notificationView, 0);

            // Clear input fields
            etTitle.setText("");
            etDescription.setText("");
        }
    }

    // Get current time in HH:mm format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
