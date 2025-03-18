package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        emailInput = findViewById(R.id.ad_email);
        passwordInput = findViewById(R.id.ad_password);
        loginButton = findViewById(R.id.ad_btn_login);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (email.equals("ikhwa1122@gmail.com")) {
                        // Admin Login
                        saveToDatabase(user.getUid(), email, "Admin");
                        startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
                    } else {
                        // User Login
                        saveToDatabase(user.getUid(), email, "User");
                        startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
                    }
                    finish();
                }
            } else {
                Toast.makeText(AdminLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToDatabase(String userId, String email, String role) {
        User user = new User(email, role);
        databaseReference.child(userId).setValue(user);
    }

    public static class User {
        public String email, role;

        public User() {}

        public User(String email, String role) {
            this.email = email;
            this.role = role;
        }
    }
}
