package com.example.ikhwa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TeacherRegistrationActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText, qualificationEditText,
            etAddress, etPass, etInt, etWhy, etFather;
    Button registerBtn;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

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

        String key = reference.push().getKey();

        HashMap<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("name", name);
        teacherMap.put("email", email);
        teacherMap.put("phone", phone);
        teacherMap.put("qualification", qualification);
        teacherMap.put("password", password);
        teacherMap.put("interest", interest);
        teacherMap.put("why", why);
        teacherMap.put("address", address);
        teacherMap.put("father_name", fatherName);
        teacherMap.put("status", "pending");

        reference.child(key).setValue(teacherMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Request sent to Admin!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
