package com.example.ikhwa.modules;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.R;

import java.util.List;

public class TeacherListAdapter1 extends RecyclerView.Adapter<TeacherListAdapter1.TeacherViewHolder> {

    private List<TeachersModels> teacherList;

    public TeacherListAdapter1(List<TeachersModels> teacherList) {
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teachers, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeachersModels teacher = teacherList.get(position);

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
