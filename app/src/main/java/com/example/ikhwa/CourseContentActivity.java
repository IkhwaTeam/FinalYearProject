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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
            if (courseId != null) {
                Intent intent = new Intent(CourseContentActivity.this, AddVideoQuizActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Course ID not found!", Toast.LENGTH_SHORT).show();
            }
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
                    String videoTitle = lectureSnap.child("title").getValue(String.class);
                    String videoUrl = lectureSnap.child("videoUrl").getValue(String.class);

                    // Create a new LinearLayout for each lecture section with rounded corners
                    LinearLayout lectureSection = new LinearLayout(CourseContentActivity.this);
                    lectureSection.setOrientation(LinearLayout.VERTICAL);
                    lectureSection.setPadding(16, 16, 16, 16);
                    lectureSection.setBackground(createRoundedBackground());
                    contentLayout.addView(lectureSection);

                    // Video Title
                    TextView videoTitleView = new TextView(CourseContentActivity.this);
                    videoTitleView.setText("Lecture " + lectureNumber + ": " + videoTitle);
                    videoTitleView.setTextSize(18);
                    videoTitleView.setTypeface(null, Typeface.BOLD);
                    videoTitleView.setTextColor(Color.BLACK);
                    lectureSection.addView(videoTitleView);

                    // Clickable URL for the video
                    TextView videoLinkView = new TextView(CourseContentActivity.this);
                    videoLinkView.setText(videoUrl);
                    videoLinkView.setTextColor(Color.BLUE);
                    videoLinkView.setPaintFlags(videoLinkView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    videoLinkView.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(browserIntent);
                    });
                    lectureSection.addView(videoLinkView);

                    // Add a gap above the "Quiz Section" title
                    View topGap = new View(CourseContentActivity.this);
                    topGap.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 20)); // 16px gap above
                    lectureSection.addView(topGap);

                    // Adding "Quiz Section" label below video link (Bold)
                    TextView quizSectionTitle = new TextView(CourseContentActivity.this);
                    quizSectionTitle.setText("Quiz Section");
                    quizSectionTitle.setTextSize(18);
                    quizSectionTitle.setTypeface(null, Typeface.BOLD); // Make the text bold
                    quizSectionTitle.setTextColor(Color.BLACK);
                    lectureSection.addView(quizSectionTitle);

                    // Add a small gap after "Quiz Section"
                    View gap = new View(CourseContentActivity.this);
                    gap.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 10)); // 16px gap below
                    lectureSection.addView(gap);

                    // Quizzes for this lecture
                    DataSnapshot quizzesSnap = lectureSnap.child("quizzes");
                    for (DataSnapshot quizSnap : quizzesSnap.getChildren()) {
                        String question = quizSnap.child("question").getValue(String.class);
                        String correctAnswer = quizSnap.child("correctAnswer").getValue(String.class);
                        List<String> options = new ArrayList<>();
                        for (DataSnapshot opt : quizSnap.child("options").getChildren()) {
                            options.add(opt.getValue(String.class));
                        }

                        // Show Question
                        TextView qView = new TextView(CourseContentActivity.this);
                        qView.setText("Q: " + question);
                        qView.setTypeface(null, Typeface.BOLD);
                        qView.setTextColor(Color.DKGRAY);
                        lectureSection.addView(qView);

                        // Show Options
                        for (String opt : options) {
                            TextView optView = new TextView(CourseContentActivity.this);
                            optView.setText("- " + opt);
                            if (opt.equals(correctAnswer)) {
                                optView.setTextColor(Color.parseColor("#4CAF50")); // Green for correct answer
                            } else {
                                optView.setTextColor(Color.parseColor("#FF5722")); // Red for incorrect options
                            }
                            lectureSection.addView(optView);
                        }

                        // Divider between quizzes
                        View divider = new View(CourseContentActivity.this);
                        divider.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        divider.setBackgroundColor(Color.LTGRAY);
                        lectureSection.addView(divider);
                    }

                    // Spacer between lectures
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
        // Create a rounded rectangle background with a light gray color
        float[] radii = new float[]{20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f}; // Set radius for rounded corners
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
        shapeDrawable.getPaint().setColor(Color.parseColor("#f0f0f0")); // Light gray background color
        return shapeDrawable;
    }
}
