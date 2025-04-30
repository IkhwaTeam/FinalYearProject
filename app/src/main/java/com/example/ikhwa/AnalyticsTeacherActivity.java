
package com.example.ikhwa;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsTeacherActivity extends AppCompatActivity {

    CircularProgressView customProgress1, customProgress2, customProgress3, customProgress4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_teacher);

        customProgress1 = findViewById(R.id.custom_progress1);
        customProgress2 = findViewById(R.id.custom_progress2);
        customProgress3 = findViewById(R.id.custom_progress3);
        customProgress4 = findViewById(R.id.custom_progress4);

        // Set Progress and Colors
        customProgress1.setProgress(65);
        customProgress1.setProgressColor(0xFF3F51B5); // Blue

        customProgress2.setProgress(80);
        customProgress2.setProgressColor(0xFF4CAF50); // Green

        customProgress3.setProgress(45);
        customProgress3.setProgressColor(0xFFF44336); // Red

        customProgress4.setProgress(20);
        customProgress4.setProgressColor(0xFF009688); // Teal
    }
}
