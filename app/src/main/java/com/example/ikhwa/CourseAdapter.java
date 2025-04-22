package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    // Constructor
    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        // Getting current course
        Course course = courseList.get(position);

        // course data to UI components
        holder.title.setText(course.getTitle());
        holder.duration.setText(course.getDuration());
        holder.progressBar.setProgress(0); // Default progress
        holder.enrollmentStatus.setText("Not enrolled"); // Default status

        // default button text
        holder.viewButton.setText("Enroll Now");

        // Handle View button click
        holder.viewButton.setOnClickListener(v -> {
            // Inflate the custom dialog view
            View dialogView = LayoutInflater.from(context).inflate(R.layout.course_dialog_show, null);

            // Initializing dialog components
            ImageView closeBtn = dialogView.findViewById(R.id.close_btn);
            Button enrollButton = dialogView.findViewById(R.id.crs_reg);

            // Building and showing dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            // Close button behavior
            closeBtn.setOnClickListener(view -> dialog.dismiss());

            // Enroll button behavior
            enrollButton.setOnClickListener(view -> {
                Toast.makeText(context, "Enrolled in: " + course.getTitle(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ViewHolder class to hold references to each view component
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, enrollmentStatus;
        ProgressBar progressBar;
        Button viewButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.course_title);
            duration = itemView.findViewById(R.id.course_duration);
            progressBar = itemView.findViewById(R.id.progressBar);
            enrollmentStatus = itemView.findViewById(R.id.enroll_status);
            viewButton = itemView.findViewById(R.id.main_button);
        }
    }
}
