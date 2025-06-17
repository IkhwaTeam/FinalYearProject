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

    private List<com.example.ikhwa.TeacherModel> teacherList;
    private Context context;

    public TeacherAdapter(List<com.example.ikhwa.TeacherModel> teacherList, Context context) {
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
        com.example.ikhwa.TeacherModel teacher = teacherList.get(position);

        holder.name.setText(teacher.getName());
        holder.email.setText(teacher.getEmail());
        holder.phone.setText(teacher.getPhone());
        holder.qualification.setText(teacher.getQualification());

        holder.btnApprove.setOnClickListener(v -> updateTeacherStatus(teacher.getTeacherId(), "approved"));
        holder.btnReject.setOnClickListener(v -> updateTeacherStatus(teacher.getTeacherId(), "rejected"));

        // View button click
        holder.btnView.setOnClickListener(v -> showTeacherDetailsDialog(teacher));
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    private void updateTeacherStatus(String teacherId, String newStatus) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teachers").child(teacherId);
        ref.child("status").setValue(newStatus);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Teacher has been " + newStatus)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showTeacherDetailsDialog(com.example.ikhwa.TeacherModel teacher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Teacher Details");

        String message = "Name: " + teacher.getName() + "\n"
                + "Father Name: " + teacher.getFatherName() + "\n"
                + "Email: " + teacher.getEmail() + "\n"
                + "Phone: " + teacher.getPhone() + "\n"
                + "Qualification: " + teacher.getQualification() + "\n"
                + "Address: " + teacher.getAddress() + "\n"
                + "Services: " + teacher.getServices() + "\n"
                + "Interested: " + teacher.getWhyInterested() + "\n"
                + "Status: " + teacher.getStatus();

        builder.setMessage(message);
        builder.setPositiveButton("Close", null);
        builder.show();
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
            btnView = itemView.findViewById(R.id.btn_view); // Added view button
        }
    }
}
