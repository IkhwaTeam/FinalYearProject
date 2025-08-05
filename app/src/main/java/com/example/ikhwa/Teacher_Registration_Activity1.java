package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ikhwa.modules.TeachersModels;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Teacher_Registration_Activity1 extends AppCompatActivity {

    private EditText name, fatherName, email, qualification, phone, address, password, confirmPassword, services, experiendced;
    private Button registerButton, loginButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference teacherRef, requestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration1);

        name = findViewById(R.id.tea_name);
        fatherName = findViewById(R.id.tea_fathername);
        email = findViewById(R.id.tea_email);
        qualification = findViewById(R.id.tea_qualification);
        phone = findViewById(R.id.tea_phone);
        address = findViewById(R.id.tea_address);
        password = findViewById(R.id.tea_password);
        confirmPassword = findViewById(R.id.tea_cnfrm_password);
        services = findViewById(R.id.tea_services);
        experiendced = findViewById(R.id.tea_experience);

        registerButton = findViewById(R.id.tea_btn_reg);
        loginButton = findViewById(R.id.tea_login);
        progressBar = findViewById(R.id.tr_progress_bar);
        errorMessage = findViewById(R.id.tea_error_message);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");
        requestsRef = FirebaseDatabase.getInstance().getReference("TeacherRequests");

        registerButton.setOnClickListener(v -> registerTeacher());
    }

    private void registerTeacher() {
        String nameStr = name.getText().toString().trim();
        String fatherNameStr = fatherName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String qualificationStr = qualification.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String addressStr = address.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String confirmPasswordStr = confirmPassword.getText().toString().trim();
        String servicesStr = services.getText().toString().trim();
        String whyStr = experiendced.getText().toString().trim();

        if (nameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
            errorMessage.setText("Please fill all required fields!");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (!passwordStr.equals(confirmPasswordStr)) {
            errorMessage.setText("Passwords do not match!");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String teacherId = mAuth.getCurrentUser().getUid();

                        com.example.ikhwa.modules.TeachersModels teacher = new com.example.ikhwa.modules.TeachersModels(
                                teacherId,
                                nameStr,
                                fatherNameStr,
                                emailStr,
                                qualificationStr,
                                phoneStr,
                                addressStr,
                                passwordStr,
                                servicesStr,
                                whyStr,
                                "pending"
                        );

                        teacherRef.child(teacherId).setValue(teacher).addOnCompleteListener(task1 -> {
                            progressBar.setVisibility(View.GONE);

                            if (task1.isSuccessful()) {
                                requestsRef.child(teacherId).setValue(teacher);

                                Toast.makeText(this, "Teacher registered successfully! Waiting for admin approval.", Toast.LENGTH_LONG).show();
                                clearFields();
                            } else {
                                errorMessage.setText("Registration failed. Try again.");
                                errorMessage.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setText("Authentication failed: " + task.getException().getMessage());
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void clearFields() {
        name.setText("");
        fatherName.setText("");
        email.setText("");
        qualification.setText("");
        phone.setText("");
        address.setText("");
        password.setText("");
        confirmPassword.setText("");
        services.setText("");
        experiendced.setText("");
        errorMessage.setVisibility(View.GONE);
    }
}
