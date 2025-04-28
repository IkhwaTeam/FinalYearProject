package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {

    private boolean isStudentSelected = false;
    private boolean isTeacherSelected = false;

    private RelativeLayout studentOption, teacherOption;
    private TextView studentText, teacherText;
    private TextView continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        studentOption = findViewById(R.id.studentOption);
        teacherOption = findViewById(R.id.teacherOption);
        continueButton = findViewById(R.id.continueButton);
        studentText = findViewById(R.id.studentText);
        teacherText = findViewById(R.id.teacherText);

        // Handling Student Option Click
        studentOption.setOnClickListener(view -> {
            isStudentSelected = true;
            isTeacherSelected = false;

            // Setting Backgrounds
            studentOption.setBackgroundResource(R.drawable.option_selector_background);
            teacherOption.setBackgroundResource(R.drawable.option_selector_bg);
            studentText.setTextColor(getResources().getColor(R.color.selected_color));
            teacherText.setTextColor(getResources().getColor(R.color.non_selected_color));


        });

        // Handling Teacher Option Click
        teacherOption.setOnClickListener(view -> {
            isStudentSelected = false;
            isTeacherSelected = true;

            // Setting Backgrounds
            teacherOption.setBackgroundResource(R.drawable.option_selector_background2);
            studentOption.setBackgroundResource(R.drawable.option_selector_bg);
            teacherText.setTextColor(getResources().getColor(R.color.selected_color));
            studentText.setTextColor(getResources().getColor(R.color.non_selected_color));

        });

        // Continue Button Logic
        continueButton.setOnClickListener(v -> {
            if (isStudentSelected) {
                Intent intent = new Intent(SelectionActivity.this, StudentLoginActivity.class);
                startActivity(intent);
            } else if (isTeacherSelected) {
                Intent intent = new Intent(SelectionActivity.this, TeacherLoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SelectionActivity.this, "Select an account type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
