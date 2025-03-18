package com.example.ikhwa;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class TeacherFormDialog extends Dialog {

    private EditText nameInput, idInput, jobTitleInput, joinDateInput;
    private Button saveButton, cancelButton;
    private OnTeacherAddedListener listener;

    public interface OnTeacherAddedListener {
        void onTeacherAdded(String name, String id, String jobTitle, String joinDate);
    }

    public TeacherFormDialog(@NonNull Context context, OnTeacherAddedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_teacher_form_dialog);


        nameInput = findViewById(R.id.et_name);
        idInput = findViewById(R.id.et_id);
        jobTitleInput = findViewById(R.id.et_job_title);
        joinDateInput = findViewById(R.id.et_join_date);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String id = idInput.getText().toString();
            String jobTitle = jobTitleInput.getText().toString();
            String joinDate = joinDateInput.getText().toString();

            // Pass data to the activity
            if (listener != null) {
                listener.onTeacherAdded(name, id, jobTitle, joinDate);
            }

            dismiss();
        });
    }
}
