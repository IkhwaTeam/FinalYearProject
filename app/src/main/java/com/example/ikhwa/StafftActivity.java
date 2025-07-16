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

import com.example.ikhwa.modules.StaffDetails;
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

        teachersContainer = findViewById(R.id.teachers_container);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TeacherRequests");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teachersContainer.removeAllViews(); // clear before adding

                for (DataSnapshot snap : snapshot.getChildren()) {
                    StaffDetails staff = snap.getValue(StaffDetails.class);

                    if (staff != null) {
                        View cardView = LayoutInflater.from(StafftActivity.this)
                                .inflate(R.layout.teacher_list_item, teachersContainer, false);

                        TextView nameTV = cardView.findViewById(R.id.teacher_name);
                        nameTV.setText(staff.getName());

                        Button btnSeeMore = cardView.findViewById(R.id.btn_see_details_tea);
                        btnSeeMore.setOnClickListener(v -> showDetailsDialog(staff));

                        teachersContainer.addView(cardView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StafftActivity.this, "Failed to load TeacherRequests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDetailsDialog(StaffDetails staff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_teacher_details, null);

        // Find and set data in views
        TextView tvName = dialogView.findViewById(R.id.tv_dialog_name);
        TextView tvQualification = dialogView.findViewById(R.id.tv_dialog_qualification);
        Button btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        tvName.setText("Name: " + staff.getName());
        tvQualification.setText("Qualification: " + staff.getQualification());

        builder.setView(dialogView); // Set custom view

        // Create dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Close button action
        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

}

