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
import android.widget.ImageButton;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

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
            Intent intent = new Intent(AdminHomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Handle menu button click
        menuBtn.setOnClickListener(view -> showPopupMenu(view));
    }

    private void showPopupMenu(android.view.View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, 0, 0, R.style.PopupMenuStyle);
        popupMenu.getMenuInflater().inflate(R.menu.logout, popupMenu.getMenu());

        // Change the text color of all items
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem menuItem = popupMenu.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(menuItem.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#242E67")), 0, spanString.length(), 0); // Your desired color
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

        // Clear the saved role from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SplashActivity.ROLE_KEY);  // Remove the role stored
        editor.apply();

        // Log the user out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Redirect to login screen after logout
        Intent intent = new Intent(AdminHomeActivity.this, SelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Ensure that the AdminHomeActivity is closed
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

    // Override the back button behavior
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Remove super.onBackPressed() to stop navigating back
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Close all activities
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}