package com.example.ikhwa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);

        // ✅ Current Course Dialog Open
        findViewById(R.id.t1).setOnClickListener(view -> showCurrentCourseDialogTea());

        // ✅ Previous Course Dialog Open
        findViewById(R.id.t5).setOnClickListener(view -> showPreviousCourseDialogTea());
    }

    // ✅ Current Course Dialog Function (Mark Attendance)
    private void showCurrentCourseDialogTea() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.currentcoursedialogtea);
        dialog.setCancelable(true);

        // ✅ Close Button
        ImageView closeBtn = dialog.findViewById(R.id.close_btn1);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dialog.dismiss());
        }

        // ✅ Mark Attendance Button
        Button btnMarkAtt = dialog.findViewById(R.id.mark_att);
        if (btnMarkAtt != null) {
            btnMarkAtt.setOnClickListener(view -> {
                dialog.dismiss(); // Dialog close کرو
                startActivity(new Intent(TeacherCourseActivity.this, TeacherAttendanceActivity.class));
            });
        }

        dialog.show();
    }

    // ✅ Previous Course Dialog Function (View Attendance)
    private void showPreviousCourseDialogTea() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.previouscoursedialogtea);
        dialog.setCancelable(true);

        // ✅ Close Button
        ImageView closeBtn = dialog.findViewById(R.id.teacher_close_btn_pre);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dialog.dismiss());
        }



        dialog.show();
    }
}
