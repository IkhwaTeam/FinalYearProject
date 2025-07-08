package com.example.ikhwa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LectureListActivity extends AppCompatActivity {

    private LinearLayout lectureListContainer;
    private String courseId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);

        courseId = getIntent().getStringExtra("courseId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        lectureListContainer = findViewById(R.id.lectureListContainer);

        if (courseId != null) {
            loadLectures();
        }
    }

    private void loadLectures() {
        DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("Courses")
                .child(courseId).child("lectures");

        DatabaseReference attemptRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("attemptedLectures").child(courseId);

        attemptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot attemptSnapshot) {
                lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 1;
                        for (DataSnapshot lectureSnap : snapshot.getChildren()) {
                            String lectureId = lectureSnap.getKey();
                            String title = lectureSnap.child("title").getValue(String.class);

                            boolean isAttempted = attemptSnapshot.hasChild(lectureId);

                            // Create styled lecture row
                            TextView tv = new TextView(LectureListActivity.this);
                            tv.setTextSize(18);
                            tv.setPadding(32, 40, 32, 40);
                            tv.setBackgroundColor(Color.WHITE);

                            // Title with attempted if done
                            String displayText = "📘 Lecture " + count + ": " + title;
                            if (isAttempted) {
                                displayText += "      (Attempted)";
                            }

                            SpannableString spannable = new SpannableString(displayText);

                            if (isAttempted) {
                                int start = displayText.indexOf(" (Attempted)");
                                int end = start + " (Attempted)".length();

                                spannable.setSpan(
                                        new android.text.style.ForegroundColorSpan(Color.parseColor("#66BB6A")), // Light green
                                        start,
                                        end,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                );
                            }

                            tv.setText(spannable);
                            tv.setTextColor(Color.BLACK); // Keep all black (Attempted part is colored separately)

                            // Set click action
                            tv.setOnClickListener(v -> {
                                Intent intent = new Intent(LectureListActivity.this, LectureDetailActivity.class);
                                intent.putExtra("courseId", courseId);
                                intent.putExtra("lectureId", lectureId);
                                startActivity(intent);
                            });

                            // Margin bottom
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 2);
                            tv.setLayoutParams(layoutParams);

                            // Add to container
                            lectureListContainer.addView(tv);

                            // Add divider
                            View divider = new View(LectureListActivity.this);
                            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
                            divider.setLayoutParams(dividerParams);
                            divider.setBackgroundColor(Color.LTGRAY);
                            lectureListContainer.addView(divider);

                            count++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LectureListActivity.this, "Error loading lectures", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LectureListActivity.this, "Error loading attempt info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
