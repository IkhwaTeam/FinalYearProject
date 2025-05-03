package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TeacherRegistrationActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText, qualificationEditText,etAddress,etPass,etInt,etwhy,etfather;
    Button registerBtn;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        nameEditText = findViewById(R.id.tea_name);
        emailEditText = findViewById(R.id.tea_email);
        phoneEditText = findViewById(R.id.tea_phone);
        qualificationEditText = findViewById(R.id.tea_qualification);
        etAddress=findViewById(R.id.tea_address);
        etfather=findViewById(R.id.tea_fathername);
        etInt=findViewById(R.id.tea_services);
        etwhy=findViewById(R.id.tea_why);
        etPass=findViewById(R.id.tea_password);
        registerBtn = findViewById(R.id.tea_btn_reg);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("PendingTeacherRequests");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeacherData();
            }
        });
    }

    private void saveTeacherData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String qualification = qualificationEditText.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String intrest = etInt.getText().toString().trim();
        String why = etwhy.getText().toString().trim();
        String father = etfather.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || qualification.isEmpty()||pass.isEmpty() || address.isEmpty() || father.isEmpty() || intrest.isEmpty()||why.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = reference.push().getKey(); // Unique key

        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("qualification", qualification);
        map.put("pass", pass);
        map.put("interst", intrest);
        map.put("why", why);
        map.put("address", address);
        map.put("fathername",father);

        reference.child(id).setValue(map).addOnSuccessListener(unused ->
                Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show());
    }
}
