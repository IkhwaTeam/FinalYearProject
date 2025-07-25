package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);

        // Get data
        courseId = getIntent().getStringExtra("courseId");
        lectureId = getIntent().getStringExtra("lectureId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize views
        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        tvVideoLink = findViewById(R.id.tvVideoLink);
        quizContainer = findViewById(R.id.quizContainer);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        tvFinalResult = findViewById(R.id.percentageText);
        btnSubmitAll = findViewById(R.id.btnSubmitAll);

        // Submit action
        btnSubmitAll.setOnClickListener(v -> evaluateQuiz());

        // Load lecture content
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

                if (videoUrl != null && !videoUrl.isEmpty()) {
                    tvVideoLink.setText(Html.fromHtml("<u>" + videoUrl + "</u>"));
                    tvVideoLink.setTextColor(Color.BLUE);
                    tvVideoLink.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(intent);
                    });
                } else {
                    tvVideoLink.setText("Video not available.");
                    tvVideoLink.setTextColor(Color.RED);
                }

                // Load quiz questions
                DataSnapshot quizzesSnap = snapshot.child("quizzes");
                int questionNumber = 1;

                for (DataSnapshot quizSnap : quizzesSnap.getChildren()) {
                    String question = quizSnap.child("question").getValue(String.class);
                    List<String> options = new ArrayList<>();
                    for (DataSnapshot opt : quizSnap.child("options").getChildren()) {
                        options.add(opt.getValue(String.class));
                    }
                    String correctAnswer = quizSnap.child("correctAnswer").getValue(String.class);

                    // Add Question TextView
                    TextView qView = new TextView(LectureDetailActivity.this);
                    qView.setText(questionNumber + ") " + question);
                    qView.setTextSize(18);
                    qView.setTextColor(Color.BLACK);
                    qView.setPadding(0, 24, 0, 8);
                    quizContainer.addView(qView);

                    // Add options as RadioGroup
                    RadioGroup rg = new RadioGroup(LectureDetailActivity.this);
                    rg.setPadding(0, 0, 0, 16);
                    for (String opt : options) {
                        RadioButton rb = new RadioButton(LectureDetailActivity.this);
                        rb.setText(opt);
                        rb.setTextColor(Color.BLACK);
                        rb.setTextSize(16);
                        rb.setButtonDrawable(R.drawable.radio_dark_selector); // Optional: Custom selector
                        rg.addView(rb);
                    }
                    quizContainer.addView(rg);

                    // Add result text below each question
                    TextView resultView = new TextView(LectureDetailActivity.this);
                    resultView.setPadding(10, 8, 10, 20);
                    resultView.setTextColor(Color.GRAY);
                    resultView.setTextSize(15);
                    quizContainer.addView(resultView);

                    // Store question for evaluation
                    quizItemList.add(new QuizItem(rg, correctAnswer, resultView));
                    questionNumber++;
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
                item.resultView.setText("❌ Not Answered. Correct Answer: " + item.correctAnswer);
                item.resultView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            // Disable options
            for (int i = 0; i < item.radioGroup.getChildCount(); i++) {
                item.radioGroup.getChildAt(i).setEnabled(false);
            }
        }

        // Calculate percentage
        int percentage = (int) ((correct * 100.0f) / total);
        tvFinalResult.setText(percentage + "%");
        quizProgressBar.setProgress(percentage);

        // ✅ Save attempt status in Firebase
        DatabaseReference attemptRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("attemptedLectures")
                .child(courseId)
                .child(lectureId);

        attemptRef.setValue(true)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "✅ Quiz marked as attempted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "⚠ Failed to mark as attempted", Toast.LENGTH_SHORT).show());
    }

    // Helper class for storing each question's view
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
