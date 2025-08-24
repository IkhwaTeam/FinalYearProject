package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<TeacherModel> teacherList;
    private Context context;

    public TeacherAdapter(List<TeacherModel> teacherList, Context context) {
        this.teacherList = teacherList;
        this.context = context;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_item, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = teacherList.get(position);

        holder.name.setText(teacher.getName());
        holder.email.setText(teacher.getEmail());
        holder.phone.setText(teacher.getPhone());
        holder.qualification.setText(teacher.getQualification());

        holder.btnApprove.setOnClickListener(v -> updateTeacherStatus(teacher.getTeacherId(), "approved"));
        holder.btnReject.setOnClickListener(v -> updateTeacherStatus(teacher.getTeacherId(), "rejected"));
        holder.btnView.setOnClickListener(v -> showTeacherDetailsDialog(teacher));
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    private void updateTeacherStatus(String teacherId, String newStatus) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teachers").child(teacherId);
        ref.child("status").setValue(newStatus);

        new AlertDialog.Builder(context)
                .setMessage("Teacher has been " + newStatus)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showTeacherDetailsDialog(com.example.ikhwa.TeacherModel teacher) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_teacher_details, null);

        TextView tvName = dialogView.findViewById(R.id.dialog_teacher_name);
        TextView tvEmail = dialogView.findViewById(R.id.dialog_teacher_email);
        TextView tvPhone = dialogView.findViewById(R.id.dialog_teacher_phone);
        TextView tvQualification = dialogView.findViewById(R.id.dialog_teacher_qualification);
        TextView tvAddress = dialogView.findViewById(R.id.dialog_teacher_address);
        TextView tvFatherName = dialogView.findViewById(R.id.dialog_teacher_father_name);
        TextView tvServices = dialogView.findViewById(R.id.dialog_teacher_services);
        TextView tvInterested = dialogView.findViewById(R.id.dialog_teacher_interested);
        TextView tvStatus = dialogView.findViewById(R.id.dialog_teacher_status);
        Button   btnClose       = dialogView.findViewById(R.id.btn_close_dialog);


        tvName.setText(teacher.getName());
        tvFatherName.setText(teacher.getFatherName());
        tvEmail.setText(teacher.getEmail());
        tvPhone.setText(teacher.getPhone());
        tvQualification.setText(teacher.getQualification());
        tvAddress.setText(teacher.getAddress());
        tvServices.setText(teacher.getServices());
        tvInterested.setText(teacher.getExperience());
        tvStatus.setText(teacher.getStatus());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, qualification;
        Button btnApprove, btnReject, btnView;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_teacher_name);
            email = itemView.findViewById(R.id.tv_teacher_email);
            phone = itemView.findViewById(R.id.tv_teacher_phone);
            qualification = itemView.findViewById(R.id.tv_teacher_qualification);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnView = itemView.findViewById(R.id.btn_view);
        }
    }
}
