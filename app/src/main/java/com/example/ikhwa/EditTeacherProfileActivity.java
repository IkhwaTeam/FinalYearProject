package com.example.ikhwa;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ikhwa.modules.TeacherRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class EditTeacherProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etQualification, etFatherName, etAddress, etInterest, etWhy;
    Button btnUpdate;

    FirebaseAuth mAuth;
    DatabaseReference teacherRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher_profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etQualification = findViewById(R.id.etQualification);
        etFatherName = findViewById(R.id.etFatherName);
        etAddress = findViewById(R.id.etAddress);
        etInterest = findViewById(R.id.etInterest);
        etWhy = findViewById(R.id.etWhy);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Firebase references
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        teacherRef = FirebaseDatabase.getInstance().getReference("TeacherRequests");

        loadExistingData();

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadExistingData() {
        teacherRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TeacherRequest teacher = snapshot.getValue(TeacherRequest.class);
                if (teacher != null) {
                    etName.setText(teacher.getName());
                    etEmail.setText(teacher.getEmail());
                    etPhone.setText(teacher.getPhone());
                    etQualification.setText(teacher.getQualification());
                    etFatherName.setText(teacher.getFatherName());
                    etAddress.setText(teacher.getAddress());
                    etInterest.setText(teacher.getInterest());
                    etWhy.setText(teacher.getWhy());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditTeacherProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String qualification = etQualification.getText().toString();
        String fatherName = etFatherName.getText().toString();
        String address = etAddress.getText().toString();
        String interest = etInterest.getText().toString();
        String why = etWhy.getText().toString();

        TeacherRequest updated = new TeacherRequest(
                uid, name, email, phone, qualification,
                "", // password not changed
                interest, why, address, fatherName
        );

        teacherRef.child(uid).setValue(updated)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
