package com.example.ikhwa;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLoginActivity extends AppCompatActivity {
    EditText emailInput, passwordInput;
    Button loginButton;
    ProgressBar progressBar;
    TextView errorText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        emailInput = findViewById(R.id.st_email);
        passwordInput = findViewById(R.id.st_password);
        loginButton = findViewById(R.id.st_btn_login);
        progressBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.error_message);

        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        loginButton.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        errorText.setVisibility(View.GONE);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            passwordInput.setError("Password must be 6-16 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        String storedPassword = user.child("password").getValue(String.class);

                        if (storedPassword != null && storedPassword.equals(password)) {
                            errorText.setVisibility(View.GONE);
                            // Proceed to student home screen
                        } else {
                            errorText.setText("Incorrect password");
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    errorText.setText("No account found with this email");
                    errorText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                errorText.setText("Database error. Try again.");
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }
}
