package com.example.ikhwa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class LectureDetailActivity extends AppCompatActivity {

    private String courseId, lectureId;
    private TextView tvVideoTitle, tvVideoLink, tvFinalResult;
    private LinearLayout quizContainer;
    private ProgressBar quizProgressBar;
    private Button btnSubmitAll;

    private final List<QuizItem> quizItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);

        courseId = getIntent().getStringExtra("courseId");
        lectureId = getIntent().getStringExtra("lectureId");

        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        tvVideoLink = findViewById(R.id.tvVideoLink);
        quizContainer = findViewById(R.id.quizContainer);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        tvFinalResult = findViewById(R.id.percentageText);
        btnSubmitAll = findViewById(R.id.btnSubmitAll);

        btnSubmitAll.setOnClickListener(v -> evaluateQuiz());

        loadLectureDetails();
    }

    private void loadLectureDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Courses")
                .child(courseId).child("lectures").child(lectureId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String videoUrl = snapshot.child("videoUrl").getValue(String.class);

                tvVideoTitle.setText(title);
                tvVideoLink.setText(videoUrl);
                tvVideoLink.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(intent);
                });

                DataSnapshot quizzesSnap = snapshot.child("quizzes");
                for (DataSnapshot quizSnap : quizzesSnap.getChildren()) {
                    String question = quizSnap.child("question").getValue(String.class);
                    List<String> options = new ArrayList<>();
                    for (DataSnapshot opt : quizSnap.child("options").getChildren()) {
                        options.add(opt.getValue(String.class));
                    }
                    String correctAnswer = quizSnap.child("correctAnswer").getValue(String.class);

                    // Show Question
                    TextView qView = new TextView(LectureDetailActivity.this);
                    qView.setText("❓ " + question);
                    qView.setTextSize(16);
                    quizContainer.addView(qView);

                    // Show Options
                    RadioGroup rg = new RadioGroup(LectureDetailActivity.this);
                    for (String opt : options) {
                        RadioButton rb = new RadioButton(LectureDetailActivity.this);
                        rb.setText(opt);
                        rg.addView(rb);
                    }
                    quizContainer.addView(rg);

                    // Result view below each question
                    TextView resultView = new TextView(LectureDetailActivity.this);
                    resultView.setPadding(10, 10, 10, 20);
                    quizContainer.addView(resultView);

                    // Store all for evaluation
                    quizItemList.add(new QuizItem(rg, correctAnswer, resultView));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LectureDetailActivity.this, "Error loading lecture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void evaluateQuiz() {
        int total = quizItemList.size();
        int correct = 0;

        for (QuizItem item : quizItemList) {
            int selectedId = item.radioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selected = findViewById(selectedId);
                String selectedText = selected.getText().toString();

                if (selectedText.equals(item.correctAnswer)) {
                    item.resultView.setText("✅ Correct!");
                    item.resultView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    correct++;
                } else {
                    item.resultView.setText("❌ Wrong. Correct: " + item.correctAnswer);
                    item.resultView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            } else {
                item.resultView.setText("❌ Not Answered. Correct: " + item.correctAnswer);
                item.resultView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            // Disable options
            for (int i = 0; i < item.radioGroup.getChildCount(); i++) {
                item.radioGroup.getChildAt(i).setEnabled(false);
            }
        }

        int percentage = (int) ((correct * 100.0f) / total);
        tvFinalResult.setText(percentage + "%");
        quizProgressBar.setProgress(percentage);
    }

    private static class QuizItem {
        RadioGroup radioGroup;
        String correctAnswer;
        TextView resultView;

        QuizItem(RadioGroup radioGroup, String correctAnswer, TextView resultView) {
            this.radioGroup = radioGroup;
            this.correctAnswer = correctAnswer;
            this.resultView = resultView;
        }
    }
}
