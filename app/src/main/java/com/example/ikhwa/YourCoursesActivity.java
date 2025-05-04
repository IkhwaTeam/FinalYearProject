package com.example.ikhwa;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
public class YourCoursesActivity extends AppCompatActivity {
    LinearLayout containerLayout;
    int[] backgroundColors = { R.drawable.bg_green, R.drawable.bg_blue, R.drawable.bg_red };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_courses);

        containerLayout = findViewById(R.id.your_courses_container);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (currentUser != null) ? currentUser.getUid() : null;

        Set<String> enrolledCourses = EnrolledCoursesManager.getEnrolledCourses(this, uid);
        List<String> courseList = new ArrayList<>(enrolledCourses);

        for (int i = 0; i < courseList.size(); i++) {
            String title = courseList.get(i);
            View courseView = getLayoutInflater().inflate(R.layout.your_course_card, null);

            TextView tvCourseTitle = courseView.findViewById(R.id.tv_course_title);
            TextView tvChapterCount = courseView.findViewById(R.id.tv_chapter_count);
            TextView tvProgress = courseView.findViewById(R.id.tv_progress);

            LinearLayout cardLayout = courseView.findViewById(R.id.card_background_layout);

            tvCourseTitle.setText(title);
            tvChapterCount.setText("40 Days");
            tvProgress.setText("0/40 Completed");

            // Change background color
            int colorResId = backgroundColors[i % backgroundColors.length];
            cardLayout.setBackgroundResource(colorResId);

            containerLayout.addView(courseView);
        }
    }
}

