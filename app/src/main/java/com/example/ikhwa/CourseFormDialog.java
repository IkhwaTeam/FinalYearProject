package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CourseFormDialog extends AppCompatActivity {

    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form_dialog);

        etName = findViewById(R.id.et_name);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Handle Save Button
        btnSave.setOnClickListener(v -> saveCourse());

        // Handle Cancel Button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCourse() {
        String courseName = etName.getText().toString().trim();

        if (courseName.isEmpty()) {
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send course name back to CourseActivity
        Intent intent = new Intent();
        intent.putExtra("COURSE_NAME", courseName);
        setResult(RESULT_OK, intent);
        finish(); // Close the dialog
    }
}
