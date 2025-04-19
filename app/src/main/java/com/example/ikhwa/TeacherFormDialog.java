package com.example.ikhwa;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TeacherFormDialog extends DialogFragment {

    private RadioGroup rgTeacherType;
    private RadioButton rbNewTeacher, rbExistingTeacher;
    private LinearLayout layoutNewTeacher, layoutExistingTeacher;

    private EditText etName, etFatherName, etEmail, etId, etPhoneNo, etJoinDate;
    private Button btnAdd, newBtnCancel, btnSearch, btnUpdate, btnCancle;

    private EditText etSearchId, prName, prFatherName, prEmail, prId, prPhoneNo, prJoinDate;
    private TextView tvTeacherInfo;

    private TeacherDataListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_teacher_form_dialog, container, false);

        rgTeacherType = view.findViewById(R.id.rg_teacher_type);
        rbNewTeacher = view.findViewById(R.id.rb_new_teacher);
        rbExistingTeacher = view.findViewById(R.id.rb_existing_teacher);

        layoutNewTeacher = view.findViewById(R.id.layout_new_teacher);
        layoutExistingTeacher = view.findViewById(R.id.layout_existing_teacher);

        etName = view.findViewById(R.id.et_name);
        etFatherName = view.findViewById(R.id.et_father_name);
        etEmail = view.findViewById(R.id.et_email);
        etId = view.findViewById(R.id.et_id);
        etPhoneNo = view.findViewById(R.id.et_phone_no);
        etJoinDate = view.findViewById(R.id.et_join_date);
        btnAdd = view.findViewById(R.id.btn_add);
        newBtnCancel = view.findViewById(R.id.new_btn_cancel);

        etSearchId = view.findViewById(R.id.et_search_id);
        btnSearch = view.findViewById(R.id.btn_search);
        tvTeacherInfo = view.findViewById(R.id.tv_teacher_info);
        prName = view.findViewById(R.id.pr_name);
        prFatherName = view.findViewById(R.id.pr_father_name);
        prEmail = view.findViewById(R.id.pr_email);
        prId = view.findViewById(R.id.pr_id);
        prPhoneNo = view.findViewById(R.id.pr_phone_no);
        prJoinDate = view.findViewById(R.id.pr_join_date);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnCancle = view.findViewById(R.id.update_btn_cancel);

        prName.setEnabled(false);
        prFatherName.setEnabled(false);
        prEmail.setEnabled(false);
        prId.setEnabled(false);
        prPhoneNo.setEnabled(false);
        prJoinDate.setEnabled(false);

        rgTeacherType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_new_teacher) {
                layoutNewTeacher.setVisibility(View.VISIBLE);
                layoutExistingTeacher.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_existing_teacher) {
                layoutNewTeacher.setVisibility(View.GONE);
                layoutExistingTeacher.setVisibility(View.VISIBLE);
            }
        });

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String fatherName = etFatherName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String id = etId.getText().toString().trim();
            String phone = etPhoneNo.getText().toString().trim();
            String joinDate = etJoinDate.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(fatherName) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(id)) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mListener.getTeacherDetailsById(id) != null) {
                Toast.makeText(getContext(), "Teacher with this ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            TeacherDetails teacher = new TeacherDetails(name, fatherName, email, id, phone, joinDate);
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("teacherProfiles");
            databaseRef.child(id).setValue(teacher).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mListener.onTeacherDataEntered(teacher);
                    Toast.makeText(getContext(), "Teacher added successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to add teacher", Toast.LENGTH_SHORT).show();
                }
            });
        });

        newBtnCancel.setOnClickListener(v -> dismiss());

        // ✅ Updated btnSearch logic to always fetch from Firebase
        btnSearch.setOnClickListener(v -> {
            String searchId = etSearchId.getText().toString().trim();
            if (!searchId.isEmpty()) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("teacherProfiles");
                databaseRef.child(searchId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            TeacherDetails found = snapshot.getValue(TeacherDetails.class);
                            if (found != null) {
                                prName.setText(found.getName());
                                prFatherName.setText(found.getFatherName());
                                prEmail.setText(found.getEmail());
                                prId.setText(found.getId());
                                prPhoneNo.setText(found.getPhone());
                                prJoinDate.setText(found.getJoinDate());

                                prName.setEnabled(true);
                                prFatherName.setEnabled(true);
                                prEmail.setEnabled(true);
                                prPhoneNo.setEnabled(true);
                                prJoinDate.setEnabled(true);
                                prId.setEnabled(false);

                                tvTeacherInfo.setText("Teacher Info Found for ID: " + searchId);
                            }
                        } else {
                            tvTeacherInfo.setText("No Teacher Found with ID: " + searchId);
                        }
                        tvTeacherInfo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(v -> {
            String updatedName = prName.getText().toString().trim();
            String updatedFatherName = prFatherName.getText().toString().trim();
            String updatedEmail = prEmail.getText().toString().trim();
            String updatedPhone = prPhoneNo.getText().toString().trim();
            String updatedJoinDate = prJoinDate.getText().toString().trim();
            String updatedId = prId.getText().toString().trim();

            if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedId)) {
                Toast.makeText(getContext(), "Name and ID are required", Toast.LENGTH_SHORT).show();
                return;
            }

            TeacherDetails updatedTeacher = new TeacherDetails(
                    updatedName, updatedFatherName, updatedEmail, updatedId, updatedPhone, updatedJoinDate
            );

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("teacherProfiles");
            databaseRef.child(updatedId).setValue(updatedTeacher).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mListener.onTeacherDataUpdated(updatedTeacher);
                    Toast.makeText(getContext(), "Teacher info updated!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to update teacher info", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancle.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TeacherDataListener) {
            mListener = (TeacherDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TeacherDataListener");
        }
    }

    public interface TeacherDataListener {
        void onTeacherDataEntered(TeacherDetails teacher);
        void onTeacherDataUpdated(TeacherDetails updatedTeacher);
        TeacherDetails getTeacherDetailsById(String id);
    }
}
