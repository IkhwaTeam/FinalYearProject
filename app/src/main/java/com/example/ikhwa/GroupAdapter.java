package com.example.ikhwa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.GroupStudent;

import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<String> groupNames;
    private final Map<String, List<GroupStudent>> groupData;
    private final String courseId;
    private Context context;


    private final Map<String, String> teacherNames; // NEW!

    public GroupAdapter(Context context, List<String> groupNames, Map<String, List<GroupStudent>> groupData, String courseId, Map<String, String> teacherNames) {  // NEW param
        this.context = context;
        this.groupNames = groupNames;
        this.groupData = groupData;
        this.courseId = courseId;
        this.teacherNames = teacherNames;
    }



    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false); // Ensure this layout exists
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupNames.get(position);
        List<GroupStudent> students = groupData.get(groupName);
        Button btnAssignTeacher = holder.itemView.findViewById(R.id.btnAssignTeacher);

        // Set group title
        holder.tvGroupName.setText(groupName);

        // Set student count
        int studentCount = (students != null) ? students.size() : 0;
        holder.tvStudentCount.setText("Students: " + studentCount);

        // Set teacher name or "Not assigned"
        String teacherName = teacherNames.get(groupName);
        holder.tvTeacherName.setText(
                (teacherName != null) ? "Teacher: " + teacherName : "Teacher: Not assigned"
        );

        // On click, pass BOTH the groupName AND courseId
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Student_List.class);
            intent.putExtra("groupName", groupName);  // pass group name
            intent.putExtra("courseId", courseId);    // pass course id
            v.getContext().startActivity(intent);
        });
        btnAssignTeacher.setOnClickListener(v -> {
            // Pass both courseId and groupName
            AssignTeacherDialog dialog = AssignTeacherDialog.newInstance(courseId, groupName);
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(),
                    "AssignTeacherDialog");
        });



    }

    @Override
    public int getItemCount() {
        return groupNames.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvStudentCount, tvTeacherName;
        Button btnAssignTeacher;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.txtGroupName);
            tvStudentCount = itemView.findViewById(R.id.txtStudentList);
            tvTeacherName = itemView.findViewById(R.id.txtTeacherName);
            btnAssignTeacher = itemView.findViewById(R.id.btnAssignTeacher);
        }
    }


}
