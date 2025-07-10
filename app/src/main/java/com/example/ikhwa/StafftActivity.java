package com.example.ikhwa;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import com.example.ikhwa.modules.TeacherDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ValueEventListener;

public class StafftActivity extends AppCompatActivity {

    LinearLayout teachersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stafft);

        // Make sure your layout has a LinearLayout with this ID
        teachersContainer = findViewById(R.id.teachers_container);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teachers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teachersContainer.removeAllViews(); // clear before adding

                for (DataSnapshot snap : snapshot.getChildren()) {
                    TeacherDetails teacher = snap.getValue(TeacherDetails.class);

                    if (teacher != null) {
                        View cardView = LayoutInflater.from(StafftActivity.this)
                                .inflate(R.layout.teacher_list_item, teachersContainer, false);

                        TextView nameTV = cardView.findViewById(R.id.teacher_name);
                        nameTV.setText(teacher.getName());

                        Button btnSeeMore = cardView.findViewById(R.id.btn_see_details_tea);
                        btnSeeMore.setOnClickListener(v -> showDetailsDialog(teacher));

                        teachersContainer.addView(cardView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StafftActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDetailsDialog(TeacherDetails teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create custom layout
        View dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null);

        // Create TextView dynamically
        TextView messageTextView = dialogView.findViewById(android.R.id.text1);
        messageTextView.setTextColor(Color.BLACK); // text black
        messageTextView.setTextSize(16);
        messageTextView.setPadding(30, 30, 30, 30);
        messageTextView.setBackgroundColor(Color.WHITE); // background white

        // Set message content
        String message = "Name: " + teacher.getName() + "\n"
                + "Father Name: " + teacher.getFatherName() + "\n"
                + "Email: " + teacher.getEmail() + "\n"
                + "Phone: " + teacher.getPhone() + "\n"
                + "Join Date: " + teacher.getJoinDate();

        messageTextView.setText(message);

        builder.setView(dialogView);

        // Add Close button
        builder.setPositiveButton("Close", null);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Optional: Change button color
        Button closeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (closeButton != null) {
            closeButton.setBackgroundColor(Color.parseColor("#FFBB33")); // yellow/orange
            closeButton.setTextColor(Color.BLACK); // button text black
        }
    }


}


