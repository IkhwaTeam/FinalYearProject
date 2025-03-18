package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StudentHomeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // Initialize the toolbar AFTER setContentView()
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
