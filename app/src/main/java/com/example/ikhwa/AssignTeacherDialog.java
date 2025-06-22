package com.example.ikhwa;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AssignTeacherDialog extends DialogFragment {

    private Spinner spinnerTeachers;
    private ProgressBar progressBar;
    private TextView emptyMessage;

    private List<String> teacherNames = new ArrayList<>();
    private List<String> teacherIds = new ArrayList<>();

    private static final String ARG_GROUP_ID = "ARG_GROUP_ID";
    private static final String ARG_COURSE_ID = "ARG_COURSE_ID";

    private String groupId;
    private String courseId;

    public static AssignTeacherDialog newInstance(String courseId, String groupId) {
        AssignTeacherDialog dialog = new AssignTeacherDialog();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId);
        args.putString(ARG_GROUP_ID, groupId);
        dialog.setArguments(args);
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
            courseId = getArguments().getString(ARG_COURSE_ID); // Get the courseId too
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_assign_teacher, null);

        spinnerTeachers = view.findViewById(R.id.spinnerTeachers);
        progressBar = view.findViewById(R.id.progressBar);
        emptyMessage = view.findViewById(R.id.emptyMessage);
        Button btnAssign = view.findViewById(R.id.btnAssign);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Fetch only approved teachers
        fetchApprovedTeachers();

        btnAssign.setOnClickListener(v -> {
            int position = spinnerTeachers.getSelectedItemPosition();
            if (position == AdapterView.INVALID_POSITION || position < 0) {
                Toast.makeText(getContext(), "Please select a teacher!", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedTeacherId = teacherIds.get(position);
            assignTeacher(selectedTeacherId);
        });

        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }

    /**
     * Fetch teachers with status == "approved"
     */
    private void fetchApprovedTeachers() {
        showLoading(true); // Show progress bar initially

        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        teachersRef.orderByChild("status").equalTo("approved")
                .get()
                .addOnSuccessListener(snapshot -> {
                    teacherNames.clear();
                    teacherIds.clear();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        String teacherId = child.getKey();
                        String teacherName = child.child("name").getValue(String.class);

                        if (teacherName != null) {
                            teacherIds.add(teacherId);
                            teacherNames.add(teacherName);
                        }
                    }

                    showLoading(false); // Hide progress bar

                    if (teacherNames.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    // Populate spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            teacherNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTeachers.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(
                            getContext(),
                            "Error fetching teachers: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    /**
     * Save assigned teacher into the group
     */

    private void assignTeacher(String teacherID) {
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("Teachers").child(teacherID);

        teacherRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String teacherName = snapshot.child("name").getValue(String.class);

                // Build the full path: Courses/<courseId>/groups/<groupId>
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Courses")
                        .child("currentCourse")
                        .child(courseId)
                        .child("groups")
                        .child(groupId);


                groupRef.child("assignedTeacherId").setValue(teacherID);
                groupRef.child("assignedTeacherName").setValue(teacherName)
                        .addOnCompleteListener(assignTask -> {
                            if (assignTask.isSuccessful()) {
                                Toast.makeText(
                                        getContext(),
                                        "Teacher assigned successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();
                                dismiss();
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        "Error assigning teacher!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            } else {
                Toast.makeText(
                        getContext(),
                        "Teacher not found!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(
                    getContext(),
                    "Error fetching teacher: " + e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }


    /**
     * Show or hide the progress spinner
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        spinnerTeachers.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);
    }

    /**
     * Show empty state when no teachers
     */
    private void showEmptyState() {
        emptyMessage.setVisibility(View.VISIBLE); // e.g. "No approved teachers available."
        progressBar.setVisibility(View.GONE);
        spinnerTeachers.setVisibility(View.GONE);
    }
}
