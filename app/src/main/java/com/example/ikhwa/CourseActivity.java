package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CourseActivity extends AppCompatActivity {

    private LinearLayout currentCourseContainer, previousCourseContainer;
    private DatabaseReference courseRef;
    private FloatingActionButton btnAddCourse;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Listen for new course additions
        courseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String title = "New Course Uploaded";
                String description = "";

                if (snapshot.child("title").exists()) {
                    description = snapshot.child("title").getValue(String.class) + " course has been added.";
                }

                Notificationclass.showNotificationDesignActivity(CourseActivity.this, title, description);
            }

            @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot snapshot) {}
            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivity.this, AdminHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // This clears the current activity from the back stack
        });

        currentCourseContainer = findViewById(R.id.current_course_container);
        previousCourseContainer = findViewById(R.id.previous_course_container);
        btnAddCourse = findViewById(R.id.btn_add_course);

        btnAddCourse.setOnClickListener(v -> showAddCourseDialog());

        loadCoursesFromFirebase();
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_course_form_dialog, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.et_name);
        EditText etDesc = view.findViewById(R.id.et_id);
        EditText etDuration = view.findViewById(R.id.et_job_title);
        EditText etStart = view.findViewById(R.id.et_join_date);
        EditText etEnd = view.findViewById(R.id.et_end_date);
        TextView btnSave = view.findViewById(R.id.btn_save);
        TextView btnCancel = view.findViewById(R.id.btn_cancel);

        // Date pickers
        etStart.setOnClickListener(v -> showDatePickerDialog(etStart));
        etEnd.setOnClickListener(v -> showDatePickerDialog(etEnd));

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = etName.getText().toString();
            String description = etDesc.getText().toString();
            String duration = etDuration.getText().toString();
            String startDate = etStart.getText().toString();
            String endDate = etEnd.getText().toString();

            String id = courseRef.push().getKey();
            Course model = new Course(title, description, duration, startDate, endDate);

            // Determine if course is current or previous
            boolean isCurrent = isCurrentCourse(endDate);
            String category = isCurrent ? "currentCourse" : "previousCourse";

            if (id != null) {
                courseRef.child(category).child(id).setValue(model); // Save under appropriate category
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CourseActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadCoursesFromFirebase() {
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentCourseContainer.removeAllViews();
                previousCourseContainer.removeAllViews();

                // Load current courses
                DataSnapshot currentCoursesSnapshot = snapshot.child("currentCourse");
                for (DataSnapshot data : currentCoursesSnapshot.getChildren()) {
                    Course model = data.getValue(Course.class);
                    if (model != null) {
                        addCourseToLayout(model, true);
                    }
                }

                // Load previous courses
                DataSnapshot previousCoursesSnapshot = snapshot.child("previousCourse");
                for (DataSnapshot data : previousCoursesSnapshot.getChildren()) {
                    Course model = data.getValue(Course.class);
                    if (model != null) {
                        addCourseToLayout(model, false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private boolean isCurrentCourse(String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date end = sdf.parse(endDate);
            return new Date().before(end);
        } catch (ParseException e) {
            return true;
        }
    }

    private void addCourseToLayout(Course model, boolean isCurrent) {
        FrameLayout frame = new FrameLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 300);
        params.setMargins(16, 16, 16, 16);
        frame.setLayoutParams(params);
        frame.setBackgroundResource(R.drawable.std_shape1); // replace with your drawable

        TextView text = new TextView(this);
        text.setText(model.getTitle());
        text.setTextColor(Color.parseColor("#3F51B5"));
        text.setTextSize(20);
        text.setPadding(5,5,5,5);
        text.setTypeface(null, Typeface.BOLD);
        text.setGravity(Gravity.CENTER);
        text.setMaxLines(2);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setHorizontallyScrolling(false);


        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        text.setLayoutParams(textParams);

        frame.addView(text);
        if (isCurrent)
            currentCourseContainer.addView(frame);
        else
            previousCourseContainer.addView(frame);
    }
}
