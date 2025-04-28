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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StudentRegistrationActivity extends AppCompatActivity {
    EditText nameInput,emailInput,fathernameInput, ageInput, phoneInput, addressInput, passwordInput, cnfrmPasswordInput;
    Button registerButton,loginBtn ;
    ProgressBar progressBar;
    TextView errorText;
    ImageView passwordToggle, cnfrmPasswordToggle;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        emailInput = findViewById(R.id.st_email);
        nameInput = findViewById(R.id.st_name);
        fathernameInput = findViewById(R.id.st_fathername);
        ageInput = findViewById(R.id.st_age);
        phoneInput = findViewById(R.id.st_phone);
        addressInput = findViewById(R.id.st_address);
        passwordInput = findViewById(R.id.st_password);
        cnfrmPasswordInput = findViewById(R.id.st_cnfrm_password);
        registerButton = findViewById(R.id.st_btn_reg);
        loginBtn = findViewById(R.id.st_login);
        progressBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.error_message);
        passwordToggle = findViewById(R.id.password_toggle);
        cnfrmPasswordToggle = findViewById(R.id.cnfrm_password_toggle);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        registerButton.setOnClickListener(v -> validateAndRegister());

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
        cnfrmPasswordToggle.setOnClickListener(v -> {
                    Typeface currentTypeface = cnfrmPasswordInput.getTypeface();

                    if (isPasswordVisible) {
                        cnfrmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        cnfrmPasswordToggle.setImageResource(R.drawable.ic_visible_off); // closed eye icon
                        isPasswordVisible = false;
                    } else {
                        cnfrmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        cnfrmPasswordToggle.setImageResource(R.drawable.ic_password); // open eye icon
                        isPasswordVisible = true;
                    }

                    // Restore Typeface to keep the font style same
                    cnfrmPasswordInput.setTypeface(currentTypeface);

                    // Move cursor to the end after toggling
                    cnfrmPasswordInput.setSelection(cnfrmPasswordInput.length());
        });

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentRegistrationActivity.this, StudentLoginActivity.class);
            startActivity(intent);
            finish();
            });

    }

    private void validateAndRegister() {
        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String fathername = fathernameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        errorText.setVisibility(View.GONE);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(fathername)) {
            fathernameInput.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(age) || Integer.parseInt(age) < 5) {
            ageInput.setError("Enter a valid age (5+)");
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            phoneInput.setError("Enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            addressInput.setError("Address is required");
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            passwordInput.setError("Password must be 6-16 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Register student in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Store student details in Firebase Database
                            HashMap<String, Object> studentData = new HashMap<>();
                            studentData.put("uid", userId);
                            studentData.put("email", email);
                            studentData.put("student_name", name);
                            studentData.put("father_name", fathername);
                            studentData.put("age", age);
                            studentData.put("phone", phone);
                            studentData.put("address", address);
                            studentData.put("password", password);



                            databaseReference.child(userId).setValue(studentData)
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        errorText.setText("Database error: " + e.getMessage());
                                        errorText.setVisibility(View.VISIBLE);
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        errorText.setText("Authentication failed: " + task.getException().getMessage());
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
    }
}
