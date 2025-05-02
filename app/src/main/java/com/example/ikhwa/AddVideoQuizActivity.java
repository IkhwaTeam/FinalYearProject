package com.example.ikhwa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddVideoQuizActivity extends AppCompatActivity {

    private EditText etVideoTitle, etVideoUrl;
    private EditText etQuizQuestion, etQuizOption1, etQuizOption2, etQuizOption3, etQuizOption4, etQuizCorrectAnswer;
    private Button btnAddVideo, btnAddQuiz;
    private DatabaseReference courseRef;
    private String courseId;
    private String currentLectureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_quiz);

        courseId = getIntent().getStringExtra("courseId");
        courseRef = FirebaseDatabase.getInstance().getReference("Courses").child(courseId).child("lectures");

        etVideoTitle = findViewById(R.id.et_video_lecture_name);
        etVideoUrl = findViewById(R.id.et_video_url);
        etQuizQuestion = findViewById(R.id.et_quiz_question);
        etQuizOption1 = findViewById(R.id.et_quiz_option_1);
        etQuizOption2 = findViewById(R.id.et_quiz_option_2);
        etQuizOption3 = findViewById(R.id.et_quiz_option_3);
        etQuizOption4 = findViewById(R.id.et_quiz_option_4);
        etQuizCorrectAnswer = findViewById(R.id.et_quiz_correct_answer);
        btnAddVideo = findViewById(R.id.btn_add_video);
        btnAddQuiz = findViewById(R.id.btn_add_quiz);

        btnAddVideo.setOnClickListener(v -> addVideo());
        btnAddQuiz.setOnClickListener(v -> addQuiz());
    }

    private void addVideo() {
        String videoTitle = etVideoTitle.getText().toString().trim();
        String videoUrl = etVideoUrl.getText().toString().trim();

        if (TextUtils.isEmpty(videoTitle) || TextUtils.isEmpty(videoUrl)) {
            Toast.makeText(this, "Enter both title and URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate new lecture node
        currentLectureId = courseRef.push().getKey();
        if (currentLectureId != null) {
            Map<String, Object> lectureData = new HashMap<>();
            lectureData.put("title", videoTitle);
            lectureData.put("videoUrl", videoUrl);

            courseRef.child(currentLectureId).setValue(lectureData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Video added!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add video", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addQuiz() {
        if (currentLectureId == null) {
            Toast.makeText(this, "Add video first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String question = etQuizQuestion.getText().toString().trim();
        String option1 = etQuizOption1.getText().toString().trim();
        String option2 = etQuizOption2.getText().toString().trim();
        String option3 = etQuizOption3.getText().toString().trim();
        String option4 = etQuizOption4.getText().toString().trim();
        String correctAnswer = etQuizCorrectAnswer.getText().toString().trim();

        if (TextUtils.isEmpty(question) || TextUtils.isEmpty(option1) || TextUtils.isEmpty(option2)
                || TextUtils.isEmpty(option3) || TextUtils.isEmpty(option4) || TextUtils.isEmpty(correctAnswer)) {
            Toast.makeText(this, "Fill all quiz fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference quizRef = courseRef.child(currentLectureId).child("quizzes").push();

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("question", question);
        quizData.put("correctAnswer", correctAnswer);
        quizData.put("options", Arrays.asList(option1, option2, option3, option4));

        quizRef.setValue(quizData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Quiz added!", Toast.LENGTH_SHORT).show();
                clearQuizFields();
            } else {
                Toast.makeText(this, "Failed to add quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearQuizFields() {
        etQuizQuestion.setText("");
        etQuizOption1.setText("");
        etQuizOption2.setText("");
        etQuizOption3.setText("");
        etQuizOption4.setText("");
        etQuizCorrectAnswer.setText("");
    }
}
