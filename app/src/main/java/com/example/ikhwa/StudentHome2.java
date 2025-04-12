package com.example.ikhwa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentHome2 extends AppCompatActivity {
    Dialog myDialog, myDialog2;
    TextView studentNameText, studentEmailText;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_home2);

        // Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        studentNameText = findViewById(R.id.st_name); // Name TextView
        studentEmailText = findViewById(R.id.st_email); // Email TextView

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Reference to the student data in Firebase
            databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(uid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("student_name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class); // Get the email

                        // Display student data in TextViews
                        studentNameText.setText(name);
                        studentEmailText.setText(email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StudentHome2.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Dialogs Initialization
        myDialog = new Dialog(this);
        myDialog2 = new Dialog(this);

        // Find buttons
        Button mainButton = findViewById(R.id.main_button);
        Button mainButton1 = findViewById(R.id.main_button1);

        mainButton.setOnClickListener(v -> show_dialog());
        mainButton1.setOnClickListener(v -> show_dialog2());

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(StudentHome2.this, stdprofile.class));
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(StudentHome2.this, std_crs_reg.class));
                    return true;
                } else if (itemId == R.id.nav_setting) {
                    startActivity(new Intent(StudentHome2.this, std_crs_att.class));
                    return true;
                }

                return false;
            }
        });
    }

    // Show First Dialog
    public void show_dialog() {
        myDialog.setContentView(R.layout.course_dialog_show);
        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            myDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ImageView close_btn = myDialog.findViewById(R.id.close_btn);
        if (close_btn != null) {
            close_btn.setOnClickListener(view -> myDialog.dismiss());
        }

        TextView intent_btn = myDialog.findViewById(R.id.crs_reg);
        if (intent_btn != null) {
            intent_btn.setOnClickListener(view -> {
                Intent intent = new Intent(StudentHome2.this, std_crs_reg.class);
                startActivity(intent);
            });
        }

        myDialog.show();
    }

    // Show Second Dialog
    public void show_dialog2() {
        myDialog2.setContentView(R.layout.your_course_dialog);
        if (myDialog2.getWindow() != null) {
            myDialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            myDialog2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ImageView close_btn2 = myDialog2.findViewById(R.id.close_btn1);
        if (close_btn2 != null) {
            close_btn2.setOnClickListener(view -> myDialog2.dismiss());
        }

        TextView intent_btn = myDialog2.findViewById(R.id.view_att);
        if (intent_btn != null) {
            intent_btn.setOnClickListener(view -> {
                Intent intent = new Intent(StudentHome2.this, std_crs_att.class);
                startActivity(intent);
            });
        }

        myDialog2.show();
    }
}
