package com.example.ikhwa;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ikhwa.modules.StaffDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StafftActivity extends AppCompatActivity {

    LinearLayout teachersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stafft);

        teachersContainer = findViewById(R.id.teachers_container);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teachers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teachersContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(StafftActivity.this, "No teachers found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    StaffDetails staff = snap.getValue(StaffDetails.class);

                    if (staff != null) {
                        String status = staff.getStatus();

                        // ✅ Only show if status = approved
                        if (status == null || !status.equalsIgnoreCase("approved")) {
                            continue; // skip if not approved
                        }

                        // Show teacher with approved status
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
                Toast.makeText(StafftActivity.this, "Failed to load Teachers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDetailsDialog(StaffDetails staff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_teacher_details, null);

        TextView tvName = dialogView.findViewById(R.id.dialog_teacher_name);
        TextView tvQualification = dialogView.findViewById(R.id.dialog_teacher_qualification);
        Button btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        tvName.setText("Name: " + staff.getName());
        tvQualification.setText("Qualification: " + staff.getQualification());

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }
}
