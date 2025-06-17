package com.example.ikhwa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeActivity extends AppCompatActivity {

    private ImageButton courseBtn, teacherBtn, studentBtn, notificationBtn, menuBtn;
    private static final String TAG = "AdminHomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        courseBtn = findViewById(R.id.course_btn);
        teacherBtn = findViewById(R.id.teacher_btn);
        studentBtn = findViewById(R.id.student_btn);
        notificationBtn = findViewById(R.id.notification_btn);
        menuBtn = findViewById(R.id.menuButton);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String status = snap.child("status").getValue(String.class);
                    if ("pending".equals(status)) {
                        String name = snap.child("name").getValue(String.class);
                        String key = snap.getKey();
                        showNotificationCard(name, key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        courseBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, CourseActivity.class);
            startActivity(intent);
        });

        teacherBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, TeacherListActivity.class);
            startActivity(intent);
        });



        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, TeacherListActivity.class);
            startActivity(intent);
        });

        notificationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, AdminNotificationActivity.class);
            startActivity(intent);
        });

        menuBtn.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, 0, 0, R.style.PopupMenuStyle);
        popupMenu.getMenuInflater().inflate(R.menu.logout, popupMenu.getMenu());

        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem menuItem = popupMenu.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(menuItem.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#242E67")), 0, spanString.length(), 0);
            menuItem.setTitle(spanString);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                logoutUser();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void logoutUser() {
        Log.d(TAG, "Logging out user");

        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SplashActivity.ROLE_KEY);
        editor.apply();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(AdminHomeActivity.this, SelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showNotificationCard(String name, String key) {
        // You can implement this method according to your UI design.
        Log.d(TAG, "New Pending Request: " + name + " (" + key + ")");
    }
}
