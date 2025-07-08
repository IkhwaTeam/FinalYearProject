package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CourseContentActivity extends AppCompatActivity {

    private LinearLayout contentLayout;
    private String courseId;
    private FloatingActionButton btnaddvideoquiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        contentLayout = findViewById(R.id.contentLayout);
        btnaddvideoquiz = findViewById(R.id.btnaddvideoquiz);
        courseId = getIntent().getStringExtra("courseId");

        if (courseId != null) {
            loadLecturesWithQuizzes();
        }

        btnaddvideoquiz.setOnClickListener(v -> {
            Intent intent = new Intent(CourseContentActivity.this, AddVideoQuizActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
    }

    private void loadLecturesWithQuizzes() {
        DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("Courses")
                .child(courseId)
                .child("lectures");

        lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contentLayout.removeAllViews();
                int lectureNumber = 1;

                for (DataSnapshot lectureSnap : snapshot.getChildren()) {
                    String lectureId = lectureSnap.getKey();
                    String videoTitle = lectureSnap.child("title").getValue(String.class);
                    String videoUrl = lectureSnap.child("videoUrl").getValue(String.class);

                    LinearLayout lectureSection = new LinearLayout(CourseContentActivity.this);
                    lectureSection.setOrientation(LinearLayout.VERTICAL);
                    lectureSection.setPadding(16, 16, 16, 16);
                    lectureSection.setBackground(createRoundedBackground());
                    contentLayout.addView(lectureSection);

                    // Title row
                    LinearLayout titleRow = new LinearLayout(CourseContentActivity.this);
                    titleRow.setOrientation(LinearLayout.HORIZONTAL);
                    titleRow.setPadding(0, 0, 0, 6);

                    TextView videoTitleView = new TextView(CourseContentActivity.this);
                    videoTitleView.setText("Lecture " + lectureNumber + ": " + videoTitle);
                    videoTitleView.setTextSize(18);
                    videoTitleView.setTypeface(null, Typeface.BOLD);
                    videoTitleView.setTextColor(Color.BLACK);
                    titleRow.addView(videoTitleView);

                    // Edit & Delete Lecture
                    Button editLectureBtn = new Button(CourseContentActivity.this);
                    editLectureBtn.setText("✏️");
                    editLectureBtn.setTextSize(12);
                    editLectureBtn.setBackgroundColor(Color.TRANSPARENT); // removes gray background
                    editLectureBtn.setPadding(0, 0, 0, 0);
                    editLectureBtn.setOnClickListener(v -> showEditDialog("Edit Lecture Title", videoTitle, newVal -> {
                        lectureRef.child(lectureId).child("title").setValue(newVal);
                        loadLecturesWithQuizzes();
                    }));

                    Button deleteLectureBtn = new Button(CourseContentActivity.this);
                    deleteLectureBtn.setText("🗑️");
                    deleteLectureBtn.setTextSize(12);
                    deleteLectureBtn.setBackgroundColor(Color.TRANSPARENT); // removes gray background
                    deleteLectureBtn.setPadding(0, 0, 0, 0);
                    deleteLectureBtn.setOnClickListener(v -> {
                        new AlertDialog.Builder(CourseContentActivity.this)
                                .setTitle("Delete Lecture?")
                                .setMessage("Are you sure you want to delete this lecture?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    lectureRef.child(lectureId).removeValue();
                                    loadLecturesWithQuizzes();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    });

                    titleRow.addView(editLectureBtn);
                    titleRow.addView(deleteLectureBtn);
                    lectureSection.addView(titleRow);

                    // Video Link
                    TextView videoLinkView = new TextView(CourseContentActivity.this);
                    videoLinkView.setText(videoUrl);
                    videoLinkView.setTextColor(Color.BLUE);
                    videoLinkView.setTextSize(16);
                    videoLinkView.setPadding(0, 4, 0, 6);
                    videoLinkView.setPaintFlags(videoLinkView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    videoLinkView.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(browserIntent);
                    });
                    lectureSection.addView(videoLinkView);

                    // Quiz Section Label
                    TextView quizSectionTitle = new TextView(CourseContentActivity.this);
                    quizSectionTitle.setText("Quiz Section");
                    quizSectionTitle.setTextSize(17);
                    quizSectionTitle.setTypeface(null, Typeface.BOLD);
                    quizSectionTitle.setTextColor(Color.BLACK);
                    lectureSection.addView(quizSectionTitle);

                    // Quizzes
                    DataSnapshot quizzesSnap = lectureSnap.child("quizzes");
                    for (DataSnapshot quizSnap : quizzesSnap.getChildren()) {
                        String quizId = quizSnap.getKey();
                        String question = quizSnap.child("question").getValue(String.class);
                        String correctAnswer = quizSnap.child("correctAnswer").getValue(String.class);
                        List<String> options = new ArrayList<>();
                        for (DataSnapshot opt : quizSnap.child("options").getChildren()) {
                            options.add(opt.getValue(String.class));
                        }

                        // Question Row
                        LinearLayout qRow = new LinearLayout(CourseContentActivity.this);
                        qRow.setOrientation(LinearLayout.HORIZONTAL);
                        TextView qView = new TextView(CourseContentActivity.this);
                        qView.setText("Q: " + question);
                        qView.setTypeface(null, Typeface.BOLD);
                        qView.setTextColor(Color.DKGRAY);
                        qView.setTextSize(16);
                        qRow.addView(qView);

                        Button editQBtn = new Button(CourseContentActivity.this);
                        editQBtn.setText("✏️");
                        editQBtn.setTextSize(12);
                        editQBtn.setBackgroundColor(Color.TRANSPARENT); // removes gray background
                        editQBtn.setPadding(0, 0, 0, 0);
                        editQBtn.setOnClickListener(v -> showEditDialog("Edit Question", question, newVal -> {
                            lectureRef.child(lectureId).child("quizzes").child(quizId).child("question").setValue(newVal);
                            loadLecturesWithQuizzes();
                        }));

                        Button deleteQBtn = new Button(CourseContentActivity.this);
                        deleteQBtn.setText("🗑️");
                        deleteQBtn.setTextSize(12);
                        deleteQBtn.setBackgroundColor(Color.TRANSPARENT); // removes gray background
                        deleteQBtn.setPadding(0, 0, 0, 0);
                        deleteQBtn.setOnClickListener(v -> {
                            new AlertDialog.Builder(CourseContentActivity.this)
                                    .setTitle("Delete Quiz?")
                                    .setMessage("Are you sure you want to delete this quiz?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        lectureRef.child(lectureId).child("quizzes").child(quizId).removeValue();
                                        loadLecturesWithQuizzes();
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        });

                        qRow.addView(editQBtn);
                        qRow.addView(deleteQBtn);
                        lectureSection.addView(qRow);

                        // Options with edit
                        for (int i = 0; i < options.size(); i++) {
                            String optText = options.get(i);
                            int finalI = i;

                            LinearLayout optRow = new LinearLayout(CourseContentActivity.this);
                            optRow.setOrientation(LinearLayout.HORIZONTAL);

                            TextView optView = new TextView(CourseContentActivity.this);
                            optView.setText("- " + optText);
                            optView.setTextSize(15);
                            optView.setPadding(16, 4, 16, 4);
                            optView.setTextColor(optText.equals(correctAnswer) ? Color.parseColor("#388E3C") : Color.BLACK);
                            optRow.addView(optView);

                            Button editOptBtn = new Button(CourseContentActivity.this);
                            editOptBtn.setText("✏️");
                            editOptBtn.setTextSize(12);
                            editOptBtn.setBackgroundColor(Color.TRANSPARENT); // removes gray background
                            editOptBtn.setPadding(0, 0, 0, 0);
                            editOptBtn.setOnClickListener(v -> showEditDialog("Edit Option", optText, newText -> {
                                lectureRef.child(lectureId).child("quizzes").child(quizId)
                                        .child("options").child(String.valueOf(finalI)).setValue(newText);
                                loadLecturesWithQuizzes();
                            }));

                            optRow.addView(editOptBtn);
                            lectureSection.addView(optRow);
                        }

                        // Divider
                        View divider = new View(CourseContentActivity.this);
                        divider.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        divider.setBackgroundColor(Color.LTGRAY);
                        lectureSection.addView(divider);
                    }

                    // Space between lectures
                    View space = new View(CourseContentActivity.this);
                    space.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 24));
                    contentLayout.addView(space);

                    lectureNumber++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CourseContentActivity.this, "Failed to load content", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Drawable createRoundedBackground() {
        float[] radii = new float[]{20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f};
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
        shapeDrawable.getPaint().setColor(Color.parseColor("#f0f0f0"));
        return shapeDrawable;
    }

    private interface OnValueSubmitted {
        void onSubmit(String newValue);
    }

    private void showEditDialog(String title, String oldValue, OnValueSubmitted callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(oldValue);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newVal = input.getText().toString().trim();
            if (!newVal.isEmpty()) {
                callback.onSubmit(newVal);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
