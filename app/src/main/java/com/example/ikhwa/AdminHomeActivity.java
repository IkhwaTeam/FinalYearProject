package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Set the toolbar as action bar
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        // Default title hide
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set toolbar icon and overflow color to white
        toolbar.getOverflowIcon().setTint(getResources().getColor(android.R.color.white));
        LinearLayout layout = findViewById(R.id.teach);
        LinearLayout layout1 = findViewById(R.id.course_view);
        LinearLayout layout2 = findViewById(R.id.notification_view);

        layout.setOnClickListener(view -> {
            // Navigate to TeachersActivity
            Intent intent = new Intent(AdminHomeActivity.this, TeacherListActivity.class);
            startActivity(intent);
        });
        layout1.setOnClickListener(view -> {
            // Navigate to CoursesActivity
            Intent intent = new Intent(AdminHomeActivity.this, CourseActivity.class);
            startActivity(intent);
        });

        layout2.setOnClickListener(view -> {
            // Navigate to NotificationsActivity
            Intent intent = new Intent(AdminHomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true; // Ensure menu is displayed
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            // Handle logout action
            Intent intent = new Intent(this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
