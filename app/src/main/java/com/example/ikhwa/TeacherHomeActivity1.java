package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class TeacherHomeActivity1 extends AppCompatActivity {

    DatabaseReference tokenRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home1);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tokenRef = FirebaseDatabase.getInstance().getReference("TeacherTokens");

        // ✅ Subscribe to topic "Teacher"
        FirebaseMessaging.getInstance().subscribeToTopic("Teacher")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Subscribed to Teacher topic", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Topic subscription failed", Toast.LENGTH_SHORT).show();
                    }
                });

        // ✅ Save token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        tokenRef.child(userId).setValue(token);
                        Toast.makeText(this, "Token saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to get token", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
