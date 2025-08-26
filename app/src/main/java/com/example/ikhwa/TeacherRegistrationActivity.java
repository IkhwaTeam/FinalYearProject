package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherRegistrationActivity extends AppCompatActivity {

    private EditText name, fatherName, email, qualification, phone, address, password, confirmPassword, services, experience;
    private Button registerButton, loginButton;
    private ProgressBar progressBar;
    private ImageView passwordToggle, confirmPasswordToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference teacherRef;
    private boolean isPasswordVisible = false;
    private boolean isCnfrmPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        // 🔹 Binding views
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
        passwordToggle = findViewById(R.id.tea_password_toggle);
        confirmPasswordToggle = findViewById(R.id.tea_cnfrm_password_toggle);

        registerButton = findViewById(R.id.tea_btn_reg);
        loginButton = findViewById(R.id.tea_login);
        progressBar = findViewById(R.id.tr_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");

        registerButton.setOnClickListener(v -> registerTeacher());

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(TeacherRegistrationActivity.this, TeacherLoginActivity.class));
            finish();
        });

        if (passwordToggle != null) {
            passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        }
        if (confirmPasswordToggle != null) {
            confirmPasswordToggle.setOnClickListener(v -> togglecnfrmPasswordVisibility());
        }

    }

    private void togglePasswordVisibility() {
        Typeface currentTypeface = password.getTypeface();
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_visible_off);
            isPasswordVisible = false;
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_password);
            isPasswordVisible = true;
        }
        password.setTypeface(currentTypeface);
        password.setSelection(password.length());
    }
    private void togglecnfrmPasswordVisibility() {
        Typeface currentTypeface = confirmPassword.getTypeface();
        if (isCnfrmPasswordVisible) {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.ic_visible_off);
            isCnfrmPasswordVisible = false;
        } else {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordToggle.setImageResource(R.drawable.ic_password);
            isCnfrmPasswordVisible = true;
        }
        confirmPassword.setTypeface(currentTypeface);
        confirmPassword.setSelection(confirmPassword.length());
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

        boolean hasError = false;

        // Name validation
        if (nameStr.isEmpty()) {
            name.setError("Name is required");
            hasError = true;
        } else if (!nameStr.matches("^[A-Za-z ]+$")) {
            name.setError("Only letters allowed");
            hasError = true;
        }

        //  Father Name validation
        if (fatherNameStr.isEmpty()) {
            fatherName.setError("Father Name is required");
            hasError = true;
        } else if (!fatherNameStr.matches("^[A-Za-z ]+$")) {
            fatherName.setError("Only letters allowed");
            hasError = true;
        }

        // Email validation
        if (emailStr.isEmpty()) {
            email.setError("Email is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Enter valid email");
            hasError = true;
        }

        // Qualification
        if (qualificationStr.isEmpty()) {
            qualification.setError("Qualification required");
            hasError = true;
        }

        // Phone validation
        if (phoneStr.isEmpty()) {
            phone.setError("Phone number is required");
            hasError = true;
        } else if (!phoneStr.matches("^[0-9]{10,15}$")) {
            phone.setError("Enter valid phone (10–15 digits)");
            hasError = true;
        }

        // Address validation
        if (addressStr.isEmpty()) {
            address.setError("Address is required");
            hasError = true;
        }

        // Services validation
        if (servicesStr.isEmpty()) {
            services.setError("Services are required");
            hasError = true;
        }

        // Experience validation
        if (experienceStr.isEmpty()) {
            experience.setError("Experience is required");
            hasError = true;
        }

        // Password validation
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,16}$";
        if (passwordStr.isEmpty()) {
            password.setError("Password is required");
            hasError = true;
        } else if (!passwordStr.matches(passwordRegex)) {
            password.setError("8–16 chars, Uppercase, Lowercase, Number & Special char");
            hasError = true;
        }

        // Confirm Password
        if (!passwordStr.equals(confirmPasswordStr)) {
            confirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        if (hasError) return; // agar koi error hai to aage mat jao

        progressBar.setVisibility(View.VISIBLE);

        // 🔹 Firebase Auth
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
                            progressBar.setVisibility(View.GONE);
                            if (task1.isSuccessful()) {
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(verifyTask -> {
                                            if (verifyTask.isSuccessful()) {
                                                Toast.makeText(this, "✅ Registered! Verify email. Waiting for admin approval.", Toast.LENGTH_LONG).show();
                                                clearFields();
                                                mAuth.signOut();
                                            } else {
                                                Toast.makeText(this, "❌ Could not send verification email.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "❌ Registration failed. Try again.", Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "❌ Auth failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ✅ Clear all fields
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
    }
}
