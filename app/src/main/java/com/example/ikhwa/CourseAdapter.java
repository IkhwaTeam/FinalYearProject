package com.example.ikhwa;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
        Course course = courseList.get(position);

        holder.title.setText(course.getTitle());
        holder.duration.setText(course.getDuration());
        holder.progressBar.setProgress(0);
        holder.enrollmentStatus.setText("Not enrolled");
        holder.viewButton.setText("Enroll Now");

        holder.viewButton.setOnClickListener(v -> {
            Course selectedCourse = courseList.get(position);
            showBottomSheet(context, selectedCourse);
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
    private void showBottomSheet(Context context, Course course) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.course_bottom_sheet, null);

        TextView title = view.findViewById(R.id.bottom_sheet_course_title);
        TextView duration = view.findViewById(R.id.bottom_sheet_course_duration);
        TextView description = view.findViewById(R.id.bottom_sheet_course_description);
        Button confirmBtn = view.findViewById(R.id.confirm_enroll_btn);


        title.setText(course.getTitle());
        duration.setText(course.getDuration());
        description.setText(course.getDescription());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You have enrolled successfully!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
    }

}
