package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ikhwa.R;
import com.example.ikhwa.modules.Student;

import java.util.List;

public class StudentAdapter extends BaseAdapter {

    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int i) {
        return studentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        TextView students_name;
        Button btn_see_details_stu;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
            holder = new ViewHolder();
            holder.students_name = convertView.findViewById(R.id.student_name);
            holder.btn_see_details_stu = convertView.findViewById(R.id.btn_see_details_student);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = studentList.get(position);
        holder.students_name.setText(student.getStudent_name());

        holder.btn_see_details_stu.setOnClickListener(view -> {
            // Show dialog with details
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Student Details");
            builder.setMessage("Name: " + student.getStudent_name() + "\n"
                    + "Father Name: " + student.getFather_name() + "\n"
                    + "Email: " + student.getEmail() + "\n"
                    + "Phone: " + student.getPhone() + "\n"
                    + "Age: " + student.getAge() + "\n"
                    + "Address: " + student.getAddress());
            builder.setPositiveButton("OK", null);
            builder.show();
        });

        return convertView;
    }
}
