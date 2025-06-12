package com.example.ikhwa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class PendingTeacherAdapter extends ArrayAdapter<TeacherRequest> {

    Context context;
    ArrayList<TeacherRequest> requests;

    public PendingTeacherAdapter(Context context, ArrayList<TeacherRequest> requests) {
        super(context, 0, requests);
        this.context = context;
        this.requests = requests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.pending_teacher_item, parent, false);
        }

        TeacherRequest request = requests.get(position);

        TextView nameTv = listItem.findViewById(R.id.tvTeacherName);
        TextView emailTv = listItem.findViewById(R.id.tvTeacherEmail);
        Button approveBtn = listItem.findViewById(R.id.btnApprove);
        Button rejectBtn = listItem.findViewById(R.id.btnReject);

        nameTv.setText(request.getName());
        emailTv.setText(request.getEmail());

        approveBtn.setOnClickListener(v -> {
            if (context instanceof AdminApprovalActivity) {
                ((AdminApprovalActivity) context).approveTeacher(request);
            }
        });

        rejectBtn.setOnClickListener(v -> {
            if (context instanceof AdminApprovalActivity) {
                ((AdminApprovalActivity) context).rejectTeacher(request);
            }
        });

        return listItem;
    }
}
