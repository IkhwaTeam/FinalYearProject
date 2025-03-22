package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Teacher_home extends AppCompatActivity {
    Button t_staff_see_more,t_course_see_more;
    TextView tv_analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.teacher_home);
        t_staff_see_more=findViewById(R.id.tbtn_see_staff);
        t_course_see_more=findViewById(R.id.tbtn_see_course);
        tv_analytics=findViewById(R.id.tvt_analytics);
        t_course_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Teacher_home.this,TeacherCourseActivity.class);
                startActivity(intent);
            }
        });
        t_staff_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Teacher_home.this,StafftActivity.class);
                startActivity(intent);
            }
        });
        tv_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Teacher_home.this,AnalyticsTeacherActivity.class);
                startActivity(intent);
            }
        });


    }
}