package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentHomeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // ✅ Firebase Notification Listener
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Notifications");

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);

                Notificationclass.showNotificationDesignActivity(StudentHomeActivity.this, title, description);
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // ✅ Toolbar Setup
        toolbar = findViewById(R.id.student_toolbar);
        setSupportActionBar(toolbar);

        // Set toolbar overflow icon color to white
        if (toolbar.getOverflowIcon() != null) {
            toolbar.getOverflowIcon().setTint(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.sttr_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.timings_menu) {
            Toast.makeText(this, "Timing menu selected", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.notification_menu) {
            Toast.makeText(this, "Notification menu selected", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_logout) {
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
