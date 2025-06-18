package com.example.ikhwa;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ikhwa.modules.GroupStudent;

import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<String> groupNames;
    private final Map<String, List<GroupStudent>> groupData;

    public GroupAdapter(List<String> groupNames, Map<String, List<GroupStudent>> groupData) {
        this.groupNames = groupNames;
        this.groupData = groupData;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false); // Make sure you have this layout
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupNames.get(position);
        List<GroupStudent> students = groupData.get(groupName);

        holder.tvGroupName.setText(groupName);
        if (students != null) {
            holder.tvStudentCount.setText("Students: " + students.size());
        } else {
            holder.tvStudentCount.setText("Students: 0");
        }

        // Handle item click to open StudentListActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Student_List.class);
            intent.putExtra("groupName", groupName); // send group name
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupNames.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvStudentCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.txtGroupName);
            tvStudentCount = itemView.findViewById(R.id.txtStudentList);
        }
    }
}
