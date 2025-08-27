package com.example.ikhwa;

import android.content.Context;
import android.graphics.Color;
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
import com.google.firebase.database.*;

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


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference enrollRef = FirebaseDatabase.getInstance()
                    .getReference("Enrollments")
                    .child(uid).child(course.getId());

            enrollRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        course.setEnrolled(true);
                        holder.enrollmentStatus.setText("Enrolled");
                        holder.viewButton.setText("Enrolled");
                        holder.viewButton.setEnabled(false);
                        holder.viewButton.setTextColor(Color.BLUE);
                        holder.viewButton.setBackgroundResource(R.drawable.gray_button);
                    } else {
                        holder.enrollmentStatus.setText("Not Enrolled");
                        holder.viewButton.setText("Enroll Now");
                        holder.viewButton.setEnabled(true);
                        holder.viewButton.setBackgroundResource(R.drawable.blue_button);
                        holder.viewButton.setOnClickListener(v -> showBottomSheet(context, course, currentUser, holder));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to check enrollment", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder.enrollmentStatus.setText("Not logged in");
            holder.viewButton.setText("Log in to Enroll");
            holder.viewButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, duration, courseType, enrollmentStatus;
        Button viewButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.course_title);
            duration = itemView.findViewById(R.id.course_duration);
            courseType = itemView.findViewById(R.id.course_type);
            enrollmentStatus = itemView.findViewById(R.id.enroll_status);
            viewButton = itemView.findViewById(R.id.main_button);
        }
    }

    private void showBottomSheet(Context context, Course course, FirebaseUser user, CourseViewHolder holder) {
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

                    DatabaseReference enrollmentRef = FirebaseDatabase.getInstance()
                            .getReference("Enrollments")
                            .child(uid)
                            .child(course.getId());

                    enrollmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot enrollSnap) {
                            if (enrollSnap.exists()) {
                                Toast.makeText(context, "Already enrolled!", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            } else {
                                enrollCourse(context, course, student);

                                course.setEnrolled(true); // update status
                                notifyItemChanged(holder.getAdapterPosition()); // refresh view

                                Toast.makeText(context, "Enrolled successfully!", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Failed to check enrollment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to load student", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void enrollCourse(Context context, Course course, Student student) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance()
                .getReference("Courses/currentCourse")
                .child(course.getId());

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot courseSnap) {
                if (!courseSnap.exists()) return;

                String courseId = course.getId();

                // Save enrollment under student
                DatabaseReference enrollmentRef = FirebaseDatabase.getInstance()
                        .getReference("Enrollments")
                        .child(student.getStudentId())
                        .child(courseId);
                enrollmentRef.setValue(true);

                // Increase enrolled count
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
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                        if (committed) {
                            // Fetch type safely
                            String type = courseSnap.child("type").getValue(String.class);

                            if (type != null && type.equalsIgnoreCase("Attendance Based")) {
                                // Only Attendance Based courses create groups
                                DatabaseReference groupsRef = courseRef.child("groups");
                                groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean assigned = false;
                                        for (DataSnapshot groupSnap : snapshot.getChildren()) {
                                            long size = groupSnap.child("students").getChildrenCount();
                                            if (size < 5) {
                                                assignToGroup(groupSnap.getRef(), student);
                                                assigned = true;
                                                break;
                                            }
                                        }
                                        if (!assigned) {
                                            long groupNum = (currentData.getValue(Long.class) - 1) / 5 + 1;
                                            String newGroupId = "group" + groupNum;
                                            assignToGroup(groupsRef.child(newGroupId), student);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            } else {
                                // Debug log (to confirm)
                                System.out.println("⚠ Skipping group creation because course type = " + type);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void assignToGroup(DatabaseReference groupRef, Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", student.getStudent_name());
        data.put("email", student.getEmail());
        data.put("uid", student.getStudentId());

        groupRef.child("students").child(student.getStudentId()).setValue(data);
    }
}
