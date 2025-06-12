package com.example.ikhwa;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TeacherRegistrationActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText, qualificationEditText, etAddress, etPass, etInt, etWhy, etFather;
    Button registerBtn;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        // Initialize UI components
        nameEditText = findViewById(R.id.tea_name);
        emailEditText = findViewById(R.id.tea_email);
        phoneEditText = findViewById(R.id.tea_phone);
        qualificationEditText = findViewById(R.id.tea_qualification);
        etAddress = findViewById(R.id.tea_address);
        etFather = findViewById(R.id.tea_fathername);
        etInt = findViewById(R.id.tea_services);
        etWhy = findViewById(R.id.tea_why);
        etPass = findViewById(R.id.tea_password);
        registerBtn = findViewById(R.id.tea_btn_reg);

        // Firebase reference
        reference = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");

        registerBtn.setOnClickListener(v -> saveTeacherData());
    }

    private void saveTeacherData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String qualification = qualificationEditText.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String interest = etInt.getText().toString().trim();
        String why = etWhy.getText().toString().trim();
        String fatherName = etFather.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || qualification.isEmpty()
                || password.isEmpty() || address.isEmpty() || fatherName.isEmpty()
                || interest.isEmpty() || why.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique ID
        String id = reference.push().getKey();

        // Data to save
        HashMap<String, String> teacherMap = new HashMap<>();
        teacherMap.put("name", name);
        teacherMap.put("email", email);
        teacherMap.put("phone", phone);
        teacherMap.put("qualification", qualification);
        teacherMap.put("password", password);
        teacherMap.put("interest", interest);
        teacherMap.put("why", why);
        teacherMap.put("address", address);
        teacherMap.put("father_name", fatherName);

        // Save to Firebase
        reference.child(id).setValue(teacherMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Registration Request Sent!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        nameEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        qualificationEditText.setText("");
        etAddress.setText("");
        etPass.setText("");
        etInt.setText("");
        etWhy.setText("");
        etFather.setText("");
    }
}
