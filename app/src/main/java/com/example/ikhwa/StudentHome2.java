package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class StudentHome2 extends AppCompatActivity {

    Button btn_clik_to_open;
    TextView notificationCount;

    DatabaseReference notificationRef, seenRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home2);

        btn_clik_to_open = findViewById(R.id.notification_click_to_open);
        notificationCount = findViewById(R.id.notification_count);

        btn_clik_to_open.setOnClickListener(view -> {
            Intent intent = new Intent(StudentHome2.this, StudentNotificationListActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
            seenRef = FirebaseDatabase.getInstance().getReference("StudentSeenNotifications").child(uid);

            loadUnseenNotificationCount(uid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            loadUnseenNotificationCount(uid);
        }
    }


    private void loadUnseenNotificationCount(String uid) {
        seenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot seenSnapshot) {
                notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot notificationsSnapshot) {
                        int unseenCount = 0;

                        for (DataSnapshot snapshot : notificationsSnapshot.getChildren()) {
                            String notifId = snapshot.getKey();
                            if (!seenSnapshot.hasChild(notifId)) {
                                unseenCount++;
                            }
                        }

                        if (unseenCount == 0) {
                            notificationCount.setVisibility(View.GONE);
                        } else {
                            notificationCount.setText(String.valueOf(unseenCount));
                            notificationCount.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
