package com.example.ikhwa;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationDesignActivity extends AppCompatActivity {

    TextView notiTitle, notiDesc;
    TextView titleView;
    ImageView logoImg;
    String notifId = "", role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_design);

        // Bind views
        notiTitle = findViewById(R.id.notification_title);
        notiDesc = findViewById(R.id.notification_description);
        logoImg = findViewById(R.id.notification_logo);

        // Get data from Intent
        notifId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        role = getIntent().getStringExtra("role");

        // Show data
        notiTitle.setText(title);
        notiDesc.setText(description);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Only delete from StudentSeenNotifications, not from main Notifications node
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && notifId != null && !notifId.isEmpty()) {
            if ("student".equals(role)) {
                FirebaseDatabase.getInstance().getReference("StudentSeenNotifications")
                        .child(user.getUid()).child(notifId).removeValue();
            }
        }
    }
}
