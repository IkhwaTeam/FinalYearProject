package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {

    private boolean isStudentSelected = false;
    private boolean isTeacherSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Button studentButton = findViewById(R.id.studentButton);
        Button teacherButton = findViewById(R.id.teacherButton);
        Button continueButton = findViewById(R.id.continueButton);


        // Highlight Student Button
        studentButton.setOnClickListener(view -> {
            isStudentSelected = true;
            isTeacherSelected = false;

            // Set Button Styles
            studentButton.setBackgroundResource(R.drawable.g1_2);
            teacherButton.setBackgroundResource(R.drawable.g2_1);

            // Change Text Color
            studentButton.setTextColor(getResources().getColor(R.color.white));
            teacherButton.setTextColor(getResources().getColor(R.color.blue_205cdc));
        });

        // Highlight Teacher Button
        teacherButton.setOnClickListener(view -> {
            isStudentSelected = false;
            isTeacherSelected = true;

            // Set Button Styles
            teacherButton.setBackgroundResource(R.drawable.g2_2);
            studentButton.setBackgroundResource(R.drawable.g1_1);

            // Change Text Color
            teacherButton.setTextColor(getResources().getColor(R.color.white));
            studentButton.setTextColor(getResources().getColor(R.color.blue_205cdc));
        });

        // Continue Button Logic
        continueButton.setOnClickListener(v -> {
            if (isStudentSelected) {
                // Navigate to StudentLoginActivity
                Intent intent = new Intent(SelectionActivity.this, StudentLoginActivity.class);
                startActivity(intent);
            } else if (isTeacherSelected) {
                // Navigate to TeacherLoginActivity
                Intent intent = new Intent(SelectionActivity.this, TeacherLoginActivity.class);
                startActivity(intent);
            } else {
                // No selection made; show a message
                Toast.makeText(SelectionActivity.this, "Select an account type", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
