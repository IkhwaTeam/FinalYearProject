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

    private EditText emailInput, passwordInput, teacherIdInput;
    private Button loginButton,thrSignupBtn;
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

        emailInput = findViewById(R.id.teacher_email);
        passwordInput = findViewById(R.id.teacher_password);
        teacherIdInput = findViewById(R.id.teacher_id);
        loginButton = findViewById(R.id.teacher_btn_login);
        thrSignupBtn = findViewById(R.id.thr_sign_up);
        errorText = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progress_bar);
        forgetPasswordText = findViewById(R.id.forgot_password);

        // 🔥 Correct node name "teachers"
        teacherRef = FirebaseDatabase.getInstance().getReference("Teachers");
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        forgetPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(TeacherLoginActivity.this, ResetPasswordActivity.class));
        });

        passwordToggle = findViewById(R.id.password_toggle);
        passwordToggle.setOnClickListener(v -> {
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
        });

        loginButton.setOnClickListener(v -> validateTeacherLogin());
        // Sign up button intent
        thrSignupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherLoginActivity.this, Teacher_Registration_Activity1.class);
            startActivity(intent);
            finish();
        });
    }



    private void validateTeacherLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String teacherId = teacherIdInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || teacherId.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.contains(" ") || password.contains(" ") || teacherId.contains(" ")) {
            Toast.makeText(this, "Fields should not contain spaces.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches("^[a-z0-9]+@[a-z]{3,10}\\.[a-z]{2,6}$")) {
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
                            checkUserRole(user.getUid(), email);
                        }
                    } else {
                        Toast.makeText(this, "Login failed. Please check your email or password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid, String email) {
        if (email.equals("ikhwa1122@gmail.com")) {
            SharedPreferences prefs = getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE);
            String existingRole = prefs.getString(SplashActivity.ROLE_KEY, "");

            if (!existingRole.isEmpty() && !existingRole.equals("teacher") && !existingRole.equals("admin")) {
                Toast.makeText(this, "Another user is logged in. Please logout first.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                finish();
                return;
            }

            prefs.edit().putString(SplashActivity.ROLE_KEY, "admin").apply();
            Log.d(TAG, "Saved role 'admin' in SharedPreferences");

            Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TeacherLoginActivity.this, Admin_home_Activity1.class));
            finish();
            return;
        }

        Query teacherQuery = teacherRef.orderByChild("email").equalTo(email);
        teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean approvedFound = false;
                    for (DataSnapshot teacherSnap : snapshot.getChildren()) {
                        com.example.ikhwa.TeacherModel teacher = teacherSnap.getValue(com.example.ikhwa.TeacherModel.class);
                        if (teacher != null && "approved".equalsIgnoreCase(teacher.getStatus())) {
                            approvedFound = true;

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

                            Toast.makeText(TeacherLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TeacherLoginActivity.this, TeacherHomeActivity1.class));
                            finish();
                            break;
                        }
                    }

                    if (!approvedFound) {
                        errorText.setText("Your registration is not approved yet.");
                        errorText.setVisibility(View.VISIBLE);
                        mAuth.signOut();
                    }

                } else {
                    errorText.setText("No teacher found with this email.");
                    errorText.setVisibility(View.VISIBLE);
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorText.setText("Database error. Try again.");
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }
}
