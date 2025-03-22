package com.example.ikhwa;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TeacherAttendanceActivity extends AppCompatActivity {

    private TextView tvDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        TableLayout tableLayout = findViewById(R.id.table_layout);
        tvDate = findViewById(R.id.attendance_date);
        calendar = Calendar.getInstance();


        updateDateLabel();


        tvDate.setOnClickListener(v -> showDatePickerDialog());

        // Sample Data
        List<String> students = new ArrayList<>();
        students.add("Ume Hani");
        students.add("Hareem Fatima");
        students.add("Zeniab");
        students.add("Eashal Fatima");

        // Colors for radio buttons
        int blueColor = getResources().getColor(android.R.color.holo_blue_light);
        int yellowColor = getResources().getColor(android.R.color.holo_orange_light);
        int redColor = getResources().getColor(android.R.color.holo_red_light);
        int grayColor = getResources().getColor(android.R.color.darker_gray);

        // Header Row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));

        TextView tvHeaderSNo = new TextView(this);
        tvHeaderSNo.setText("S. No.");
        setHeaderStyle(tvHeaderSNo);
        headerRow.addView(tvHeaderSNo);

        TextView tvHeaderName = new TextView(this);
        tvHeaderName.setText("Name");
        setHeaderStyle(tvHeaderName);
        headerRow.addView(tvHeaderName);

        TextView tvHeaderStatus = new TextView(this);
        tvHeaderStatus.setText("Status");
        setHeaderStyle(tvHeaderStatus);
        headerRow.addView(tvHeaderStatus);

        tableLayout.addView(headerRow);

        for (int i = 0; i < students.size(); i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(getResources().getColor(android.R.color.white));
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvSNo = new TextView(this);
            tvSNo.setText(String.valueOf(i + 1));
            setCellStyle(tvSNo);
            row.addView(tvSNo);

            TextView tvName = new TextView(this);
            tvName.setText(students.get(i));
            setCellStyle(tvName);
            row.addView(tvName);

            TableRow.LayoutParams radioParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            radioParams.setMargins(10, 10, 10, 10);

            RadioButton rbPresent = new RadioButton(this);
            rbPresent.setButtonTintList(ColorStateList.valueOf(grayColor));
            rbPresent.setTag("Present_" + i);
            rbPresent.setLayoutParams(radioParams);
            row.addView(rbPresent);

            TextView tvPresentLabel = new TextView(this);
            tvPresentLabel.setText("P");
            setCellStyle(tvPresentLabel);
            row.addView(tvPresentLabel);

            RadioButton rbAbsent = new RadioButton(this);
            rbAbsent.setButtonTintList(ColorStateList.valueOf(grayColor));
            rbAbsent.setTag("Absent_" + i);
            rbAbsent.setLayoutParams(radioParams);
            row.addView(rbAbsent);

            TextView tvAbsentLabel = new TextView(this);
            tvAbsentLabel.setText("A");
            setCellStyle(tvAbsentLabel);
            row.addView(tvAbsentLabel);

            RadioButton rbLeave = new RadioButton(this);
            rbLeave.setButtonTintList(ColorStateList.valueOf(grayColor));
            rbLeave.setTag("Leave_" + i);
            rbLeave.setLayoutParams(radioParams);
            row.addView(rbLeave);

            TextView tvLeaveLabel = new TextView(this);
            tvLeaveLabel.setText("L");
            setCellStyle(tvLeaveLabel);
            row.addView(tvLeaveLabel);

            rbPresent.setOnClickListener(v -> {
                rbPresent.setButtonTintList(ColorStateList.valueOf(blueColor));
                rbAbsent.setButtonTintList(ColorStateList.valueOf(grayColor));
                rbLeave.setButtonTintList(ColorStateList.valueOf(grayColor));

                rbAbsent.setChecked(false);
                rbLeave.setChecked(false);
            });

            rbAbsent.setOnClickListener(v -> {
                rbPresent.setButtonTintList(ColorStateList.valueOf(grayColor));
                rbAbsent.setButtonTintList(ColorStateList.valueOf(redColor));
                rbLeave.setButtonTintList(ColorStateList.valueOf(grayColor));

                rbPresent.setChecked(false);
                rbLeave.setChecked(false);
            });

            rbLeave.setOnClickListener(v -> {
                rbPresent.setButtonTintList(ColorStateList.valueOf(grayColor));
                rbAbsent.setButtonTintList(ColorStateList.valueOf(grayColor));
                rbLeave.setButtonTintList(ColorStateList.valueOf(yellowColor));

                rbPresent.setChecked(false);
                rbAbsent.setChecked(false);
            });

            tableLayout.addView(row);
        }
    }

    // Method to set common styles for header cells
    private void setHeaderStyle(TextView textView) {
        textView.setPadding(16, 16, 16, 16);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setGravity(Gravity.CENTER);
    }

    // Method to set common styles for regular cells
    private void setCellStyle(TextView textView) {
        textView.setPadding(16, 16, 16, 16);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setGravity(Gravity.CENTER);
    }


    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        datePickerDialog.show();
    }

    }

