package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class YourCoursesActivity extends AppCompatActivity {
    LinearLayout containerLayout;
    int[] backgroundColors = { R.drawable.bg_green, R.drawable.bg_blue, R.drawable.bg_red };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_courses);

        containerLayout = findViewById(R.id.your_courses_container);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EnrolledCourses").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int index = 0;
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseTitle = courseSnapshot.getKey();

                    View courseView = getLayoutInflater().inflate(R.layout.your_course_card, null);

                    TextView tvCourseTitle = courseView.findViewById(R.id.tv_course_title);
                    TextView tvChapterCount = courseView.findViewById(R.id.tv_chapter_count);
                    TextView tvProgress = courseView.findViewById(R.id.tv_progress);
                    RelativeLayout cardLayout = courseView.findViewById(R.id.card_background_layout);

                    tvCourseTitle.setText(courseTitle);
                    tvChapterCount.setText("40 Days");
                    tvProgress.setText("0/40 Completed");

                    int colorResId = backgroundColors[index % backgroundColors.length];
                    cardLayout.setBackgroundResource(colorResId);

                    containerLayout.addView(courseView);
                    index++;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}
