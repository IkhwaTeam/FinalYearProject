package com.example.ikhwa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, thrSignupBtn;
    private TextView errorText, forgetPasswordText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ImageView passwordToggle;
    private DatabaseReference teacherRef, adminRef;
    private boolean isPasswordVisible = false;
    private static final String TAG = "TeacherLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        try {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Auto-login check
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                SharedPreferences prefs = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
                String savedRole = prefs.getString(SplashActivity.ROLE_KEY, "");

                if ("teacher".equals(savedRole) || "admin".equals(savedRole)) {
                    Log.d(TAG, "Current user is a " + savedRole + ", checking role");
                    checkUserRole(currentUser.getUid(), currentUser.getEmail());
                } else if (!TextUtils.isEmpty(savedRole)) {
                    Log.d(TAG, "User logged in as: " + savedRole + ", sign out first");
                    Toast.makeText(this, "Please log out from your " + savedRole + " account first", Toast.LENGTH_SHORT).show();
                }
            }

            // Initialize views
            emailInput = findViewById(R.id.teacher_email);
            passwordInput = findViewById(R.id.teacher_password);
            loginButton = findViewById(R.id.teacher_btn_login);
            thrSignupBtn = findViewById(R.id.thr_sign_up);
            errorText = findViewById(R.id.error_message);
            progressBar = findViewById(R.id.progress_bar);
            forgetPasswordText = findViewById(R.id.forgot_password);

            // Check if views are null
            if (emailInput == null || passwordInput == null || loginButton == null) {
                Log.e(TAG, "Critical views are null - check layout file");
                finish();
                return;
            }

            // Firebase references
            teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");
            adminRef = FirebaseDatabase.getInstance().getReference("Admin");

            // Set click listeners
            if (forgetPasswordText != null) {
                forgetPasswordText.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(TeacherLoginActivity.this, ResetPasswordActivity.class));
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting ResetPasswordActivity", e);
                    }
                });
            }

            passwordToggle = findViewById(R.id.password_toggle);
            if (passwordToggle != null) {
                passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
            }

            loginButton.setOnClickListener(v -> validateTeacherLogin());

            if (thrSignupBtn != null) {
                thrSignupBtn.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(TeacherLoginActivity.this, TeacherRegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting TeacherRegistrationActivity", e);
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            finish();
        }
    }

    private void togglePasswordVisibility() {
        try {
            Typeface currentTypeface = passwordInput.getTypeface();

            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visible_off);
                isPasswordVisible = false;
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_password);
                isPasswordVisible = true;
            }

            passwordInput.setTypeface(currentTypeface);
            passwordInput.setSelection(passwordInput.length());
        } catch (Exception e) {
            Log.e(TAG, "Error toggling password visibility", e);
        }
    }

    private void validateTeacherLogin() {
        try {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            Log.d(TAG, "Login attempt with email: " + email);

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.contains(" ") || password.contains(" ")) {
                Toast.makeText(this, "Fields should not contain spaces.", Toast.LENGTH_SHORT).show();
                return;
            }

            // More flexible email validation
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "Firebase authentication successful");
                                checkUserRole(user.getUid(), email);
                            }
                        } else {
                            Log.e(TAG, "Firebase authentication failed", task.getException());
                            Toast.makeText(this, "Login failed. Please check your email or password.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in validateTeacherLogin", e);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "An error occurred during login", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserRole(String uid, String email) {
        try {
            Log.d(TAG, "Checking role for email: " + email);
            // List of allowed admin emails
            String[] adminEmails = {"innovibes311@gmail.com", "ikhwaappproject2025@gmail.com"};

            // Check if email is an admin
            for (String adminEmail : adminEmails) {
                if (adminEmail.equalsIgnoreCase(email)) {
                    Log.d(TAG, "Admin email detected: " + email);
                    handleAdminLogin();
                    return;
                }
                }

            // Check teacher database
            Query teacherQuery = teacherRef.orderByChild("email").equalTo(email);
            teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            boolean approvedFound = false;
                            for (DataSnapshot teacherSnap : snapshot.getChildren()) {
                                com.example.ikhwa.TeacherModel teacher = teacherSnap.getValue(com.example.ikhwa.TeacherModel.class);
                                if (teacher != null && "approved".equalsIgnoreCase(teacher.getStatus())) {
                                    approvedFound = true;
                                    handleTeacherLogin();
                                    break;
                                }
                            }

                            if (!approvedFound) {
                                showError("Your registration is not approved yet.");
                                mAuth.signOut();
                            }

                        } else {
                            showError("No teacher found with this email.");
                            mAuth.signOut();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in teacher role check", e);
                        showError("Error checking user role.");
                        mAuth.signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error in teacher role check", error.toException());
                    showError("Database error. Try again.");
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in checkUserRole", e);
            showError("Error checking user role.");
            mAuth.signOut();
        }
    }

    private void handleAdminLogin() {
        try {
            SharedPreferences prefs = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
            String existingRole = prefs.getString(SplashActivity.ROLE_KEY, "");

            Log.d(TAG, "Existing role in SharedPreferences: " + existingRole);

            // Admin can override any existing role - clear it first
            if (!existingRole.isEmpty() && !existingRole.equals("admin")) {
                Log.d(TAG, "Clearing existing role: " + existingRole + " for admin login");
                prefs.edit().clear().apply(); // Clear all preferences
            }

            // Save admin role BEFORE starting new activity
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SplashActivity.ROLE_KEY, "admin");
            boolean saved = editor.commit(); // Use commit() for immediate save
            Log.d(TAG, "Saved role 'admin' in SharedPreferences: " + saved);

            Log.d(TAG, "Attempting to start AdminHomeActivity...");

            // Start AdminHomeActivity immediately without delay
            try {
                Intent adminIntent = new Intent(TeacherLoginActivity.this, AdminHomeActivity.class);
                adminIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                adminIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Log.d(TAG, "Intent created, starting activity...");
                startActivity(adminIntent);

                // Show success message
                Toast.makeText(TeacherLoginActivity.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();

                // Finish current activity
                finish();

            } catch (Exception e) {
                Log.e(TAG, "Error starting AdminHomeActivity", e);
                Toast.makeText(this, "Error opening admin panel: " + e.getMessage(), Toast.LENGTH_LONG).show();

                // Log more details about the error
                Log.e(TAG, "Intent creation failed", e);

                // Don't sign out, let admin try again
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in handleAdminLogin", e);
            Toast.makeText(this, "Error during admin login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleTeacherLogin() {
        try {
            SharedPreferences prefs = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
            String existingRole = prefs.getString(SplashActivity.ROLE_KEY, "");

            if (!existingRole.isEmpty() && !existingRole.equals("teacher") && !existingRole.equals("admin")) {
                Toast.makeText(TeacherLoginActivity.this, "Another user is logged in. Please logout first.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                finish();
                return;
            }

            prefs.edit().putString(SplashActivity.ROLE_KEY, "teacher").apply();
            Log.d(TAG, "Saved role 'teacher' in SharedPreferences");

            Toast.makeText(TeacherLoginActivity.this, "Teacher Login Successful!", Toast.LENGTH_SHORT).show();

            Intent teacherIntent = new Intent(TeacherLoginActivity.this, Teacher_home.class);
            teacherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(teacherIntent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error in handleTeacherLogin", e);
            Toast.makeText(this, "Error during teacher login", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        try {
            if (errorText != null) {
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TeacherLoginActivity destroyed");
    }
}