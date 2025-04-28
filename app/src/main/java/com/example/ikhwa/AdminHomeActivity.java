package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public class AdminHomeActivity extends AppCompatActivity {

    private ImageButton courseBtn, teacherBtn, studentBtn, notificationBtn, menuBtn;

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
                Intent intent = new Intent(AdminHomeActivity.this, TeacherLoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();

    }


    // These two methods are optional now because you are handling the menu through a popup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            Intent intent = new Intent(this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
