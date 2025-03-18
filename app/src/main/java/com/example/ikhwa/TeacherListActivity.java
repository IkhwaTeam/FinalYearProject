package com.example.ikhwa;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TeacherListActivity extends AppCompatActivity implements TeacherFormDialog.OnTeacherAddedListener {

    private LinearLayout teachersListLayout; // Reference to the LinearLayout where FrameLayouts will be added
    Button back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        // Initialize views
        teachersListLayout = findViewById(R.id.teacherslist);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // Open the dialog when FAB is clicked
        fabAdd.setOnClickListener(v -> {
            TeacherFormDialog dialog = new TeacherFormDialog(this, this);
            dialog.show();
        });

        back_button = findViewById(R.id.btn_back);
        back_button.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onTeacherAdded(String name, String id, String jobTitle, String joinDate) {
        // Create a new FrameLayout dynamically
        FrameLayout teacherFrame = new FrameLayout(this);
        LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(120)  // Set height to 120dp
        );
        frameParams.setMargins(dpToPx(8), dpToPx(16), dpToPx(8), dpToPx(8)); // Apply margins
        teacherFrame.setLayoutParams(frameParams);
        teacherFrame.setBackgroundResource(R.drawable.g_out); // Use your existing background
        teacherFrame.setElevation(dpToPx(10)); // Apply elevation

        // Create a TextView to show the teacher's name
        TextView teacherText = new TextView(this);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = android.view.Gravity.CENTER; // Center the text
        teacherText.setLayoutParams(textParams);
        teacherText.setText(name); // Set teacher's name
        teacherText.setTextSize(22); // Set text size
        teacherText.setTypeface(null, Typeface.BOLD); // Set text to bold
        teacherText.setTextColor(android.graphics.Color.parseColor("#25C3E9")); // Set text color

        // Add TextView to FrameLayout
        teacherFrame.addView(teacherText);

        // Add FrameLayout to the teacher list layout
        teachersListLayout.addView(teacherFrame);
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}
