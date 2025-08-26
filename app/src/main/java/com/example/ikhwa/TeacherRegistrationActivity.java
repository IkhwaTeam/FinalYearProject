package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherRegistrationActivity extends AppCompatActivity {

    private EditText name, fatherName, email, qualification, phone, address, password, confirmPassword, services, experience;
    private Button registerButton, loginButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference teacherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        name = findViewById(R.id.tea_name);
        fatherName = findViewById(R.id.tea_fathername);
        email = findViewById(R.id.tea_email);
        qualification = findViewById(R.id.tea_qualification);
        phone = findViewById(R.id.tea_phone);
        address = findViewById(R.id.tea_address);
        password = findViewById(R.id.tea_password);
        confirmPassword = findViewById(R.id.tea_cnfrm_password);
        services = findViewById(R.id.tea_services);
        experience = findViewById(R.id.tea_experience);

        registerButton = findViewById(R.id.tea_btn_reg);
        loginButton = findViewById(R.id.tea_login);
        progressBar = findViewById(R.id.tr_progress_bar);
        errorMessage = findViewById(R.id.tea_error_message);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");

        registerButton.setOnClickListener(v -> registerTeacher());

        // 🔹 Login button intent
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(TeacherRegistrationActivity.this, TeacherLoginActivity.class));
            finish();
        });
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
        String experienceStr = experience.getText().toString().trim();

        // 🔹 Validation
        if (nameStr.isEmpty() || fatherNameStr.isEmpty() || emailStr.isEmpty() || qualificationStr.isEmpty() ||
                phoneStr.isEmpty() || addressStr.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty() ||
                servicesStr.isEmpty() || experienceStr.isEmpty()) {
            errorMessage.setText("⚠️ Please fill all fields!");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            errorMessage.setText("⚠️ Please enter a valid email address!");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if (!passwordStr.equals(confirmPasswordStr)) {
            errorMessage.setText("⚠️ Passwords do not match!");
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String teacherId = firebaseUser.getUid();

                        com.example.ikhwa.TeacherModel teacher = new com.example.ikhwa.TeacherModel(
                                teacherId,
                                nameStr,
                                fatherNameStr,
                                emailStr,
                                qualificationStr,
                                phoneStr,
                                addressStr,
                                passwordStr,
                                servicesStr,
                                experienceStr,
                                "pending"
                        );

                        teacherRef.child(teacherId).setValue(teacher).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // ✅ Email verification link send karo
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(verifyTask -> {
                                            progressBar.setVisibility(View.GONE);
                                            if (verifyTask.isSuccessful()) {
                                                Toast.makeText(this, "✅ Registered successfully! Please verify your email. Waiting for admin approval.", Toast.LENGTH_LONG).show();
                                                clearFields();
                                                mAuth.signOut(); // Auto logout until email verified
                                            } else {
                                                errorMessage.setText("❌ Could not send verification email.");
                                                errorMessage.setVisibility(View.VISIBLE);
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                errorMessage.setText("❌ Registration failed. Try again.");
                                errorMessage.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setText("❌ Authentication failed: " + task.getException().getMessage());
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    // 🔹 Method jo sari fields clear karega
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
        experience.setText("");
        errorMessage.setVisibility(View.GONE);
    }
}
