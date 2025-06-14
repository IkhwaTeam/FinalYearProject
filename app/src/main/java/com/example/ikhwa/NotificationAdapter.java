package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationModel> list;
    boolean isAdmin;

    public NotificationAdapter(Context context, List<NotificationModel> list, boolean isAdmin) {
        this.context = context;
        this.list = list;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        NotificationModel model = list.get(i);
        h.tvTitle.setText(model.getTitle());
        h.tvDescription.setText(model.getDescription());

        // Split timestamp into date and time
        String[] parts = model.getTimestamp().split(" ", 2);
        if (parts.length == 2) {
            h.tvDate.setText(parts[0]);
            h.tvTime.setText(parts[1]);
        } else {
            h.tvDate.setText("N/A");
            h.tvTime.setText("N/A");
        }

        if (isAdmin) {
            h.btnEdit.setVisibility(View.VISIBLE);
            h.btnDelete.setVisibility(View.VISIBLE);
        } else {
            h.btnEdit.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);
        }

        h.btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Notifications")
                    .child(model.getId()).removeValue();
        });

        h.btnEdit.setOnClickListener(v -> showEditDialog(model));
    }

    private void showEditDialog(NotificationModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Notification");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_notification, null);
        EditText etTitle = view.findViewById(R.id.editTitle);
        EditText etDesc = view.findViewById(R.id.editDescription);

        etTitle.setText(model.getTitle());
        etDesc.setText(model.getDescription());

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", etTitle.getText().toString().trim());
            map.put("description", etDesc.getText().toString().trim());

            FirebaseDatabase.getInstance().getReference("Notifications")
                    .child(model.getId()).updateChildren(map);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate, tvTime;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);       // new
            tvTime = itemView.findViewById(R.id.tvTime);       // new
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
