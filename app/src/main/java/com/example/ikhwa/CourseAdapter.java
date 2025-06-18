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

import com.example.ikhwa.modules.Course;
import com.example.ikhwa.modules.Student;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        View view = LayoutInflater.from(context).inflate(R.layout.course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.courseTitle.setText(course.getTitle());
        holder.courseType.setText(course.getType());
        holder.duration.setText(course.getDuration());
        holder.progressBar.setProgress(0);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (course.isEnrolled()) {
                holder.enrollmentStatus.setText("Enrolled");
                holder.viewButton.setText("View Course Details");
                holder.viewButton.setOnClickListener(v -> showCourseDetails(course));
            } else {
                holder.enrollmentStatus.setText("Not Enrolled");
                holder.viewButton.setText("Enroll Now");
                holder.viewButton.setOnClickListener(v -> showBottomSheet(context, course, currentUser));
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
        TextView courseTitle, courseType;
        ProgressBar progressBar;
        Button viewButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.course_title);
            duration = itemView.findViewById(R.id.course_duration);
            enrollmentStatus = itemView.findViewById(R.id.enroll_status);
            progressBar = itemView.findViewById(R.id.progressBar);
            viewButton = itemView.findViewById(R.id.main_button);

            courseTitle = itemView.findViewById(R.id.course_title);
            courseType = itemView.findViewById(R.id.course_type);
        }
    }

    private void showBottomSheet(Context context, Course course, FirebaseUser user) {
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
            String uid = user.getUid();
            String email = user.getEmail();

            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("Student")
                    .child(uid);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = "Unnamed Student";
                    if (snapshot.exists() && snapshot.hasChild("student_name")) {
                        name = snapshot.child("student_name").getValue(String.class);
                    }

                    Student student = new Student(uid, name, email);

                    // 🔍 Check if already enrolled
                    DatabaseReference enrollmentRef = FirebaseDatabase.getInstance()
                            .getReference("Enrollments")
                            .child(uid)
                            .child(course.getId());

                    enrollmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot enrollSnap) {
                            if (enrollSnap.exists()) {
                                Toast.makeText(context, "You are already enrolled in this course!", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            } else {
                                enrollCourse(context, course, student);
                                Toast.makeText(context, "You have enrolled successfully!", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Enrollment check failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to load user info: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }


    private void showCourseDetails(Course course) {
        Toast.makeText(context, "Course details for: " + course.getTitle(), Toast.LENGTH_SHORT).show();
        // Add navigation logic if needed
    }

    private void enrollCourse(Context context, Course course, Student student) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance()
                .getReference("Courses/currentCourse")
                .child(course.getId());

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot courseSnap) {
                if (courseSnap.exists()) {
                    String courseId = course.getId();

                    DatabaseReference enrollmentRef = FirebaseDatabase.getInstance()
                            .getReference("Enrollments")
                            .child(student.getStudentId())
                            .child(courseId);

                    enrollmentRef.setValue(true);

                    DatabaseReference enrolledCountRef = courseRef.child("enrolledStudents");

                    enrolledCountRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Long currentValue = currentData.getValue(Long.class);
                            currentData.setValue((currentValue == null) ? 1 : currentValue + 1);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@androidx.annotation.Nullable DatabaseError error, boolean committed, @androidx.annotation.Nullable DataSnapshot currentData) {
                            if (committed) {
                                DatabaseReference groupsRef = courseRef.child("groups");

                                groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean assigned = false;

                                        for (DataSnapshot groupSnap : snapshot.getChildren()) {
                                            String groupId = groupSnap.getKey();
                                            long currentSize = groupSnap.child("students").getChildrenCount();

                                            if (currentSize < 5) {
                                                DatabaseReference groupRef = groupSnap.getRef()
                                                        .child("students")
                                                        .child(student.getStudentId());

                                                Map<String, Object> studentMap = new HashMap<>();
                                                studentMap.put("name", student.getStudent_name());
                                                studentMap.put("email", student.getEmail());
                                                studentMap.put("uid", student.getStudentId());

                                                groupRef.setValue(studentMap)
                                                        .addOnSuccessListener(unused -> Toast.makeText(context, "Enrolled in " + groupId, Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e -> Toast.makeText(context, "Group assignment failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                                assigned = true;
                                                break;
                                            }
                                        }

                                        if (!assigned) {
                                            // ✅ More reliable group number based on enrolledStudents count
                                            Long enrolledCount = currentData != null ? currentData.getValue(Long.class) : null;
                                            long groupNumber = (enrolledCount != null) ? ((enrolledCount - 1) / 5) + 1 : snapshot.getChildrenCount() + 1;

                                            String newGroupId = "group" + groupNumber;

                                            DatabaseReference newGroupRef = groupsRef.child(newGroupId).child("students").child(student.getStudentId());

                                            Map<String, Object> studentMap = new HashMap<>();
                                            studentMap.put("name", student.getStudent_name());
                                            studentMap.put("email", student.getEmail());
                                            studentMap.put("uid", student.getStudentId());

                                            newGroupRef.setValue(studentMap)
                                                    .addOnSuccessListener(unused -> Toast.makeText(context, "Enrolled in " + newGroupId, Toast.LENGTH_SHORT).show())
                                                    .addOnFailureListener(e -> Toast.makeText(context, "New group creation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, "Error loading groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Enrollment failed: " + (error != null ? error.getMessage() : ""), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "Course not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
