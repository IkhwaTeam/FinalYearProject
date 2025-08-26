package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StudentRegistrationActivity extends AppCompatActivity {

    EditText nameInput, emailInput, fathernameInput, ageInput, phoneInput, addressInput, passwordInput, cnfrmPasswordInput;
    Button registerButton, loginBtn;
    ProgressBar progressBar;
    TextView errorText;
    ImageView passwordToggle, cnfrmPasswordToggle;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Initialize Views
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

        // Password Toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility(passwordInput, passwordToggle));

        // Confirm Password Toggle
        cnfrmPasswordToggle.setOnClickListener(v -> togglePasswordVisibility(cnfrmPasswordInput, cnfrmPasswordToggle));

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentRegistrationActivity.this, StudentLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Password Visibility Toggle
    private void togglePasswordVisibility(EditText input, ImageView toggle) {
        Typeface currentTypeface = input.getTypeface();
        if (input.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggle.setImageResource(R.drawable.ic_password);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggle.setImageResource(R.drawable.ic_visible_off);
        }
        input.setTypeface(currentTypeface);
        input.setSelection(input.length());
    }

    //Validation Function
    private boolean validateForm() {
        boolean isValid = true;

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = cnfrmPasswordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String fathername = fathernameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        // Email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            isValid = false;
        }

        // Name (only alphabets)
        if (TextUtils.isEmpty(name) || !name.matches("^[a-zA-Z ]+$")) {
            nameInput.setError("Enter valid name");
            isValid = false;
        }

        // Father Name (only alphabets)
        if (TextUtils.isEmpty(fathername) || !fathername.matches("^[a-zA-Z ]+$")) {
            fathernameInput.setError("Enter valid father's name (letters only)");
            isValid = false;
        }

        // Age (5–100 only)
        if (TextUtils.isEmpty(age) || !TextUtils.isDigitsOnly(age)) {
            ageInput.setError("Enter valid age");
            isValid = false;
        } else {
            int ageVal = Integer.parseInt(age);
            if (ageVal < 5 || ageVal > 100) {
                ageInput.setError("Age must be between 5 and 100");
                isValid = false;
            }
        }

        // Phone (10–15 digits)
        if (TextUtils.isEmpty(phone) || !phone.matches("^[0-9]{10,15}$")) {
            phoneInput.setError("Enter valid phone (10-15 digits)");
            isValid = false;
        }

        // Address
        if (TextUtils.isEmpty(address)) {
            addressInput.setError("Address is required");
            isValid = false;
        }

        // Password (Strong Rule)
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,16}$";
        if (!password.matches(passwordRegex)) {
            passwordInput.setError("Password 8-16 chars, must contain upper, lower, digit, special char");
            isValid = false;
        }

        // Confirm Password
        if (!password.equals(confirmPassword)) {
            cnfrmPasswordInput.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void validateAndRegister() {
        if (!validateForm()) {
            return;
        }

        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String fathername = fathernameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        errorText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Check if email already exists
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getSignInMethods().isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            emailInput.setError("This email is already registered");
                            return;
                        }
                        // Proceed with registration
                        registerUser(email, password, name, fathername, age, phone, address);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        errorText.setText("Error checking email: " + task.getException().getMessage());
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void registerUser(String email, String password, String name, String fathername, String age, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send email verification
                            user.sendEmailVerification()
                                    .addOnSuccessListener(unused -> {
                                        String userId = user.getUid();

                                        HashMap<String, Object> studentData = new HashMap<>();
                                        studentData.put("uid", userId);
                                        studentData.put("email", email);
                                        studentData.put("student_name", name);
                                        studentData.put("father_name", fathername);
                                        studentData.put("age", age);
                                        studentData.put("phone", phone);
                                        studentData.put("address", address);

                                        databaseReference.child(userId).setValue(studentData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Registration successful! Please verify your email before login.",
                                                            Toast.LENGTH_LONG).show();

                                                    mAuth.signOut(); // logout after registration
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    errorText.setText("Database error: " + e.getMessage());
                                                    errorText.setVisibility(View.VISIBLE);
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        errorText.setText("Failed to send verification email: " + e.getMessage());
                                        errorText.setVisibility(View.VISIBLE);
                                    });
                        }
                    } else {
                        errorText.setText("Authentication failed: " + task.getException().getMessage());
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
    }
}
