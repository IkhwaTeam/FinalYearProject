package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ikhwa.modules.Student;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class stdprofile extends AppCompatActivity {

    EditText etName,etFatherName, etNumber, etAge, etAddress, etEmail;
    TextView tvStudentName;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String uid;
    Button btnEditprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stdprofile); // Make sure XML file name is correct

        // Initialize views
        etName=findViewById(R.id.etName);
        etFatherName = findViewById(R.id.etFatherName);
        etNumber = findViewById(R.id.etNumber);
        etAge = findViewById(R.id.etAge);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        tvStudentName = findViewById(R.id.tvStudentName);
        btnEditprofile = findViewById(R.id.btnEditprofile);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Student");

            databaseReference.child(uid).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        etName.setText(student.getStudent_name());
                        etFatherName.setText(student.getFather_name());
                        etNumber.setText(student.getPhone());
                        etAge.setText(String.valueOf(student.getAge())); // Handle age as a string
                        etAddress.setText(student.getAddress());
                        etEmail.setText(student.getEmail());
                        tvStudentName.setText(student.getStudent_name());
                    }
                } else {
                    Toast.makeText(this, "No profile found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close activity to avoid crash
        }

        disableEditing();

        btnEditprofile.setOnClickListener(v -> {
            Intent intent = new Intent(stdprofile.this, StdEditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void disableEditing() {
        etName.setEnabled(false);
        etFatherName.setEnabled(false);
        etNumber.setEnabled(false);
        etAge.setEnabled(false);
        etAddress.setEnabled(false);
        etEmail.setEnabled(false);
    }
}