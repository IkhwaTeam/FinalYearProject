package com.example.ikhwa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.TeacherViewHolder> {

    private List<com.example.ikhwa.TeacherModel> teacherList;

    public TeacherListAdapter(List<com.example.ikhwa.TeacherModel> teacherList) {
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        com.example.ikhwa.TeacherModel teacher = teacherList.get(position);

        holder.nameText.setText("Name: " + teacher.getName());
        holder.emailText.setText("Email: " + teacher.getEmail());
        holder.phoneText.setText("Phone: " + teacher.getPhone());
        holder.statusText.setText("Status: " + teacher.getStatus());
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, phoneText, statusText;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.teacher_name);
            emailText = itemView.findViewById(R.id.teacher_email);
            phoneText = itemView.findViewById(R.id.teacher_phone);
            statusText = itemView.findViewById(R.id.teacher_status);
        }
    }
}
