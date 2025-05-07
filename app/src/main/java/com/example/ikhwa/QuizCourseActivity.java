package com.example.ikhwa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.List;
import java.util.Map;

public class QuizCourseActivity extends AppCompatActivity {

    private LinearLayout lectureContainer;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_course);

        lectureContainer = findViewById(R.id.lectureContainer);
        courseId = getIntent().getStringExtra("courseId");

        loadLectures();
    }

    private void loadLectures() {
        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("Courses")
                .child(courseId).child("lectures");

        lecturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lectureContainer.removeAllViews();

                for (DataSnapshot lectureSnap : snapshot.getChildren()) {
                    String title = lectureSnap.child("title").getValue(String.class);
                    String videoUrl = lectureSnap.child("videoUrl").getValue(String.class);

                    // Lecture Title
                    TextView titleView = new TextView(QuizCourseActivity.this);
                    titleView.setText("🎥 " + title);
                    titleView.setTextSize(18);
                    titleView.setPadding(10, 20, 10, 10);
                    lectureContainer.addView(titleView);

                    // Play Button
                    Button playButton = new Button(QuizCourseActivity.this);
                    playButton.setText("Play Video");
                    playButton.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(intent);
                    });
                    lectureContainer.addView(playButton);

                    // Load Quizzes
                    if (lectureSnap.hasChild("quizzes")) {
                        for (DataSnapshot quizSnap : lectureSnap.child("quizzes").getChildren()) {
                            Map<String, Object> quizData = (Map<String, Object>) quizSnap.getValue();

                            String question = (String) quizData.get("question");
                            List<String> options = (List<String>) quizData.get("options");
                            String correctAnswer = (String) quizData.get("correctAnswer");

                            // Question
                            TextView questionView = new TextView(QuizCourseActivity.this);
                            questionView.setText("❓ " + question);
                            questionView.setPadding(10, 20, 10, 5);
                            questionView.setTextSize(16);
                            lectureContainer.addView(questionView);

                            // RadioGroup for options
                            RadioGroup radioGroup = new RadioGroup(QuizCourseActivity.this);
                            for (String opt : options) {
                                RadioButton radioButton = new RadioButton(QuizCourseActivity.this);
                                radioButton.setText(opt);
                                radioButton.setTextSize(14);
                                radioGroup.addView(radioButton);
                            }
                            lectureContainer.addView(radioGroup);

                            // Submit Button
                            Button submitButton = new Button(QuizCourseActivity.this);
                            submitButton.setText("Submit Answer");
                            lectureContainer.addView(submitButton);

                            TextView resultView = new TextView(QuizCourseActivity.this);
                            resultView.setPadding(10, 10, 10, 20);
                            lectureContainer.addView(resultView);

                            submitButton.setOnClickListener(v -> {
                                int selectedId = radioGroup.getCheckedRadioButtonId();
                                if (selectedId == -1) {
                                    Toast.makeText(QuizCourseActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                                } else {
                                    RadioButton selectedOption = findViewById(selectedId);
                                    String selectedText = selectedOption.getText().toString();

                                    if (selectedText.equals(correctAnswer)) {
                                        resultView.setText("✅ Correct!");
                                        resultView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                    } else {
                                        resultView.setText("❌ Incorrect. Correct Answer: " + correctAnswer);
                                        resultView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    }

                                    // Disable options and button
                                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                        radioGroup.getChildAt(i).setEnabled(false);
                                    }
                                    submitButton.setEnabled(false);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizCourseActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
