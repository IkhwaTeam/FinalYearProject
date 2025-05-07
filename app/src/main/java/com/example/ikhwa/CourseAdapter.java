package com.example.ikhwa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dynamically inflate the layout based on course type (general or enrolled)
        View view = LayoutInflater.from(context).inflate(R.layout.course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Set general course information
        holder.courseTitle.setText(course.getTitle());
        holder.courseType.setText(course.getType()); // Display course type (e.g., "Beginner", "Advanced")
        holder.duration.setText(course.getDuration());
        holder.progressBar.setProgress(0); // Set initial progress

        // Check if the course is enrolled by the user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If the course is already enrolled
            if (course.isEnrolled()) {
                holder.enrollmentStatus.setText("Enrolled");
                holder.viewButton.setText("View Course Details");
                holder.viewButton.setOnClickListener(v -> {
                    // Show course details (e.g., videos, quizzes) for enrolled courses
                    showCourseDetails(course);
                });
            } else {
                // If the course is not enrolled yet
                holder.enrollmentStatus.setText("Not Enrolled");
                holder.viewButton.setText("Enroll Now");
                holder.viewButton.setOnClickListener(v -> {
                    // Show bottom sheet for enrollment
                    showBottomSheet(context, course);
                });
            }
        } else {
            holder.enrollmentStatus.setText("Not logged in");
            holder.viewButton.setText("Log in to Enroll");
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, enrollmentStatus;
        TextView courseTitle, courseType; // Added
        ProgressBar progressBar;
        Button viewButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.course_title);
            duration = itemView.findViewById(R.id.course_duration);
            enrollmentStatus = itemView.findViewById(R.id.enroll_status);
            progressBar = itemView.findViewById(R.id.progressBar);
            viewButton = itemView.findViewById(R.id.main_button);

            // Initialize new fields (make sure these IDs exist in course_card.xml)
            courseTitle = itemView.findViewById(R.id.course_title);
            courseType = itemView.findViewById(R.id.course_type);
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

        confirmBtn.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = (currentUser != null) ? currentUser.getUid() : null;

            if (uid != null) {
                // Enroll the user in the course
                EnrolledCoursesManager.enrollCourse(context, uid, course.getTitle());
                Toast.makeText(context, "You have enrolled successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog.dismiss();
        });
    }

    private void showCourseDetails(Course course) {
        // Logic to display the course content (e.g., videos and quizzes)
        Toast.makeText(context, "Course details for: " + course.getTitle(), Toast.LENGTH_SHORT).show();
        // You can implement the functionality to navigate to a CourseDetailActivity
    }
}
