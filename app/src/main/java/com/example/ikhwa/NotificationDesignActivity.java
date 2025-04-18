package com.example.ikhwa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;

public class NotificationDesignActivity extends AppCompatActivity {

    TextView notiTitle, notiDesc;
    ImageView logoImg;
    Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_design); // ← Your custom layout

        // Bind views
        notiTitle = findViewById(R.id.notification_title);
        notiDesc = findViewById(R.id.notification_description);
        logoImg = findViewById(R.id.notification_logo);
        deleteBtn = findViewById(R.id.notification_delete_btn); // Add this button in your XML

        // Get data from Intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String notifId = getIntent().getStringExtra("id"); // Optional for delete

        // Show data in layout
        notiTitle.setText(title);
        notiDesc.setText(description);

        // Delete logic
        deleteBtn.setOnClickListener(v -> {
            if (notifId != null && !notifId.isEmpty()) {
                FirebaseDatabase.getInstance().getReference("Notifications")
                        .child(notifId).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity
                        });
            }
        });

        // Optional: Only show notification if you want
        // showCustomNotification(title, description);
    }

    private void showCustomNotification(String title, String description) {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.activity_notification_design);
        notificationLayout.setTextViewText(R.id.notification_title, title);
        notificationLayout.setTextViewText(R.id.notification_description, description);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.logo)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
    }
}
