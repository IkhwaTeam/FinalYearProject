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

public class StudentLoginActivity extends AppCompatActivity {

    EditText stEmail, stPassword;
    Button stLoginBtn, stSignupBtn;
    ProgressBar progressBar;
    TextView errorMessage;
    ImageView passwordToggle;
    FirebaseAuth mAuth;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        stEmail = findViewById(R.id.st_email);
        stPassword = findViewById(R.id.st_password);
        stLoginBtn = findViewById(R.id.st_btn_login);
        stSignupBtn = findViewById(R.id.st_sign_up);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.error_message);
        passwordToggle = findViewById(R.id.password_toggle);

        mAuth = FirebaseAuth.getInstance();

        // Password toggle click event
        passwordToggle.setOnClickListener(v -> {
            // Save current Typeface
            Typeface currentTypeface = stPassword.getTypeface();

            if (isPasswordVisible) {
                stPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_visible_off); // closed eye icon
                isPasswordVisible = false;
            } else {
                stPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_password); // open eye icon
                isPasswordVisible = true;
            }

            // Restore Typeface to keep the font style same
            stPassword.setTypeface(currentTypeface);

            // Move cursor to the end after toggling
            stPassword.setSelection(stPassword.length());
        });

        // Login button click event
        stLoginBtn.setOnClickListener(v -> loginStudent());

        // Sign up button intent
        stSignupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentLoginActivity.this, StudentRegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginStudent() {
        String email = stEmail.getText().toString().trim();
        String password = stPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showError("All fields are required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Firebase Authentication login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(StudentLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Redirect to home screen
                        Intent intent = new Intent(StudentLoginActivity.this, StudentHome2.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showError("Invalid email or password");
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    showError("Error: " + e.getMessage());
                });
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }
}
