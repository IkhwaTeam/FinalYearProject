package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, teacherIdInput;
    private Button loginButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    ImageView passwordToggle;
    private DatabaseReference teacherRef, adminRef;
    boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        emailInput = findViewById(R.id.teacher_email);
        passwordInput = findViewById(R.id.teacher_password);
        teacherIdInput = findViewById(R.id.teacher_id);
        loginButton = findViewById(R.id.teacher_btn_login);
        errorText = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        loginButton.setOnClickListener(v -> validateTeacherLogin());

        passwordToggle = findViewById(R.id.password_toggle);
        // Password toggle click event
        passwordToggle.setOnClickListener(v -> {
            // Save current Typeface
            Typeface currentTypeface = passwordInput.getTypeface();

            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visible_off); // closed eye icon
                isPasswordVisible = false;
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_password); // open eye icon
                isPasswordVisible = true;
            }

            // Restore Typeface to keep the font style same
            passwordInput.setTypeface(currentTypeface);

            // Move cursor to the end after toggling
            passwordInput.setSelection(passwordInput.length());
        });
    }

    private void validateTeacherLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String teacherId = teacherIdInput.getText().toString().trim();

        // If fields are empty
        if (email.isEmpty() || password.isEmpty() || teacherId.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        // If fields contain spaces
        if (email.contains(" ") || password.contains(" ") || teacherId.contains(" ")) {
            Toast.makeText(this, "Fields should not contain spaces.", Toast.LENGTH_SHORT).show();
            return;
        }
        // If wrong password format entered
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,15}$")) {
            Toast.makeText(this, "Invalid password entered!", Toast.LENGTH_SHORT).show();
            return;
        }
        // If wrong email format entered
        if (!email.matches("^[a-z0-9]+@[a-z]{3,10}\\.[a-z]{2,6}$")) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }
        // If wrong id entered
        if (!teacherId.matches("^[a-zA-Z0-9@\\-_]{10,20}$")) {
            Toast.makeText(this, "Invalid ID format!", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        // Authenticate with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid(), email);
                        }
                    } else {
                        // Firebase sign-in failure
                        Toast.makeText(this, "Login failed. Please check your email or password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid, String email) {
        // Admin Check
        if (email.equals("ikhwa1122@gmail.com")) {
            Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TeacherLoginActivity.this, AdminHomeActivity.class));
            finish();
            return;
        }

        // Teacher Check (Using Email Instead of UID)
        Query teacherQuery = teacherRef.orderByChild("email").equalTo(email);
        teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TeacherLoginActivity.this, Teacher_home.class));
                    finish();
                } else {
                    errorText.setText("No teacher found with this email.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorText.setText("Database error. Try again.");
            }
        });
    }

}