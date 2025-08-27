package com.example.ikhwa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.ikhwa.modules.Student;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StdEditProfileActivity extends AppCompatActivity {

    EditText etStudentNameEdit,etFatherNameEdit, etNumberEdit, etAgeEdit, etAddressEdit, etEmailEdit;
    Button btnSave;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_edit_profile); // Make sure this matches your XML

        // Initialize views
        etStudentNameEdit=findViewById(R.id.etStuNameEdit);
        etFatherNameEdit = findViewById(R.id.etFatherNameEdit);
        etNumberEdit = findViewById(R.id.etNumberEdit);
        etAgeEdit = findViewById(R.id.etAgeEdit);
        etAddressEdit = findViewById(R.id.etAddressEdit);
        etEmailEdit = findViewById(R.id.etEmailEdit);
        btnSave = findViewById(R.id.btnSave);

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        // Load existing data
        databaseReference.child(uid).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Student student = dataSnapshot.getValue(Student.class);
                if (student != null) {
                    etStudentNameEdit.setText(student.getStudent_name());
                    etFatherNameEdit.setText(student.getFather_name());
                    etNumberEdit.setText(student.getPhone());
                    etAgeEdit.setText(student.getAge()); // Already string
                    etAddressEdit.setText(student.getAddress());
                    etEmailEdit.setText(student.getEmail());
                }
            } else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
        });

        // Save button logic
        btnSave.setOnClickListener(v -> {
            String studentName = etStudentNameEdit.getText().toString().trim();
            String fatherName = etFatherNameEdit.getText().toString().trim();
            String phone = etNumberEdit.getText().toString().trim();
            String age = etAgeEdit.getText().toString().trim(); // Keep age as String
            String address = etAddressEdit.getText().toString().trim();
            String email = etEmailEdit.getText().toString().trim();

            if (TextUtils.isEmpty(studentName) ||TextUtils.isEmpty(fatherName) || TextUtils.isEmpty(phone) ||
                    TextUtils.isEmpty(age) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update individual fields in Firebase
            databaseReference.child(uid).child("student_name").setValue(studentName);
            databaseReference.child(uid).child("father_name").setValue(fatherName);
            databaseReference.child(uid).child("phone").setValue(phone);
            databaseReference.child(uid).child("age").setValue(age);
            databaseReference.child(uid).child("address").setValue(address);
            databaseReference.child(uid).child("email").setValue(email);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
