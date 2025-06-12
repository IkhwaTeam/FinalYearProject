package com.example.ikhwa;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class TeacherAdapterRe extends BaseAdapter {

    Activity context;
    List<TeacherModelRe> list;
    List<String> keys;
    public TeacherAdapterRe(Activity context, List<TeacherModelRe> list, List<String> keys) {


        this.context = context;
        this.list = list;
        this.keys = keys;
    }

    @Override public int getCount() {
        return list.size();
    }

    @Override public Object getItem(int position) {
        return list.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_teacher_re, parent, false);

        TextView name = convertView.findViewById(R.id.tea_name);
        TextView email = convertView.findViewById(R.id.tea_email);
        TextView phone = convertView.findViewById(R.id.tea_phone);
        TextView qua = convertView.findViewById(R.id.tea_qualification);
        TextView fathername = convertView.findViewById(R.id.tea_fathername);
        TextView address = convertView.findViewById(R.id.tea_address);
        TextView inter = convertView.findViewById(R.id.tea_services);
        TextView why = convertView.findViewById(R.id.tea_why);
        TextView pass = convertView.findViewById(R.id.tea_password);

        Button approveBtn = convertView.findViewById(R.id.buttonReject);
        Button rejectBtn = convertView.findViewById(R.id.buttonApprove);

        TeacherModelRe model = list.get(position);
        name.setText("Name: " + model.name);
        email.setText("Email: " + model.email);
        phone.setText("Phone: " + model.phone);
       // qua.setText("Qualification: " + model.qua);
        //fathername.setText("Father's Name: " + model.fathername);
        //address.setText("Address: " + model.address);
        //pass.setText("Password: " + model.password);
        //inter.setText("Interest: " + model.interest);
       // why.setText("Why: " + model.why);

        String currentKey = keys.get(position);

        approveBtn.setOnClickListener(v -> {
            DatabaseReference approvedRef = FirebaseDatabase.getInstance().getReference("ApprovedTeachers");
            approvedRef.child(currentKey).setValue(model);
            FirebaseDatabase.getInstance().getReference("PendingTeacherRequests").child(currentKey).removeValue();
            Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();
        });

        rejectBtn.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("PendingTeacherRequests").child(currentKey).removeValue();
            Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
