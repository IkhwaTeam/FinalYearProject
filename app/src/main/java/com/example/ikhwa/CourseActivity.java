package com.example.ikhwa;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CourseActivity extends AppCompatActivity {

    private LinearLayout courseContainer;
    private int courseCount = 1;
    Button back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        courseContainer = findViewById(R.id.course_container);

        // Floating Action Button Click Listener
        findViewById(R.id.fab_add_course).setOnClickListener(v -> showCourseFormDialog());
        back_button = findViewById(R.id.beck);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Method to show the custom CourseFormDialog
    private void showCourseFormDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_course_form_dialog);

        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etDescription = dialog.findViewById(R.id.et_id);
        EditText etDuration = dialog.findViewById(R.id.et_job_title);
        EditText etStartDate = dialog.findViewById(R.id.et_join_date);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || duration.isEmpty() || startDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                addNewCourse(name, description, duration, startDate);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Method to dynamically add a new course
    private void addNewCourse(String name, String description, String duration, String startDate) {
        FrameLayout newCourse = new FrameLayout(this);

        // Set proper dimensions and margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.course_width),
                getResources().getDimensionPixelSize(R.dimen.course_height)
        );
        layoutParams.setMargins(0, 0, 8, 0); // Ensure no extra vertical margin
        newCourse.setLayoutParams(layoutParams);
        newCourse.setBackgroundResource(R.drawable.g_out);

        TextView courseText = new TextView(this);
        courseText.setText(name);
        courseText.setTextSize(22);
        courseText.setTextColor(getResources().getColor(R.color.primary));
        courseText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        courseText.setTypeface(null, android.graphics.Typeface.BOLD); // Ensure bold text

        newCourse.addView(courseText);
        courseContainer.addView(newCourse);

        Toast.makeText(this, "Course Added: " + name, Toast.LENGTH_SHORT).show();
    }



}
