package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView errorText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference teacherRef, adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        emailInput = findViewById(R.id.teacher_email);
        passwordInput = findViewById(R.id.teacher_password);
        loginButton = findViewById(R.id.teacher_btn_login);
        errorText = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        loginButton.setOnClickListener(v -> validateTeacherLogin());
    }

    private void validateTeacherLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorText.setText("Enter a valid email.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            errorText.setText("Please enter password.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // ✅ Authenticate with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid(), email);
                        }
                    } else {
                        errorText.setText("Login failed: " + task.getException().getMessage());
                    }
                });
    }

    private void checkUserRole(String uid, String email) {
        // ✅ Admin Check
        if (email.equals("ikhwa1122@gmail.com")) {
            Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TeacherLoginActivity.this, AdminHomeActivity.class));
            finish();
            return;
        }

        // ✅ Teacher Check (Using Email Instead of UID)
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
