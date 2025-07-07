package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LectureListActivity extends AppCompatActivity {
    private LinearLayout lectureListContainer;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);

        courseId = getIntent().getStringExtra("courseId");
        lectureListContainer = findViewById(R.id.lectureListContainer);

        if (courseId != null) {
            loadLectures();
        }
    }

    private void loadLectures() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Courses")
                .child(courseId).child("lectures");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 1;
                for (DataSnapshot lectureSnap : snapshot.getChildren()) {
                    String lectureId = lectureSnap.getKey();
                    String title = lectureSnap.child("title").getValue(String.class);

                    TextView tv = new TextView(LectureListActivity.this);
                    tv.setText("Lecture " + count + ": " + title);
                    tv.setTextSize(18);
                    tv.setPadding(16, 20, 16, 20);
                    tv.setOnClickListener(v -> {
                        Intent intent = new Intent(LectureListActivity.this, LectureDetailActivity.class);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("lectureId", lectureId);
                        startActivity(intent);
                    });

                    lectureListContainer.addView(tv);
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LectureListActivity.this, "Error loading lectures", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
