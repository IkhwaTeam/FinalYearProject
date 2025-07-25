package com.example.ikhwa;

import
        android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ikhwa.modules.TeacherRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class TeacherProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvQualification, tvInterest, tvWhy, tvAddress, tvFatherName;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    Button btn_change_pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvQualification = findViewById(R.id.tvQualification);
        tvInterest = findViewById(R.id.tvInterest);
        tvWhy = findViewById(R.id.tvWhy);
        tvAddress = findViewById(R.id.tvAddress);
        tvFatherName = findViewById(R.id.tvFatherName);
        btn_change_pro=findViewById(R.id.btn_change_profile_tea);
        btn_change_pro.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherProfileActivity.this, EditTeacherProfileActivity.class);
            startActivity(intent);
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid(); // get teacher UID

        ref = FirebaseDatabase.getInstance().getReference("TeacherRequests");

        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TeacherRequest teacher = snapshot.getValue(TeacherRequest.class);

                    if (teacher != null) {
                        tvName.setText(teacher.getName());
                        tvEmail.setText(teacher.getEmail());
                        tvPhone.setText(teacher.getPhone());
                        tvQualification.setText(teacher.getQualification());
                        tvInterest.setText(teacher.getInterest());
                        tvWhy.setText(teacher.getWhy());
                        tvAddress.setText(teacher.getAddress());
                        tvFatherName.setText(teacher.getFatherName());
                    }
                } else {
                    Toast.makeText(TeacherProfileActivity.this, "No profile found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TeacherProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
