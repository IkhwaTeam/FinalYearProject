package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimingsActivity extends AppCompatActivity {
    Button back_button, addButton;
    EditText titleEditText;
    TimePicker timePicker;
    Spinner classSpinner;
    String selectedClass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timings);

        // Initialize Views
        back_button = findViewById(R.id.back_button);
        addButton = findViewById(R.id.addButton);
        titleEditText = findViewById(R.id.titleEditText);
        timePicker = findViewById(R.id.timePicker);
        classSpinner = findViewById(R.id.classSpinner);

        // Initialize Spinner options
        String[] classOptions = {"Morning Class", "Evening Class", "Other"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(spinnerAdapter);

        // Manage EditText visibility based on Spinner selection
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = classOptions[position];
                titleEditText.setVisibility("Other".equals(selectedClass) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                titleEditText.setVisibility(View.GONE);
            }
        });

        // Back Button: Finish Activity
        back_button.setOnClickListener(view -> finish());

        // Add Button: Add Timing
        addButton.setOnClickListener(view -> {
            String title = selectedClass.equals("Other") ? titleEditText.getText().toString().trim() : selectedClass;

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Convert to 12-hour format and determine AM/PM
            String amPm = (hour >= 12) ? "PM" : "AM";
            int hourIn12Format = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);

            // Create the formatted time
            String time = String.format("%s - %d:%02d %s", title, hourIn12Format, minute, amPm);

            // Display a toast message
            Toast.makeText(this, "New timing added: " + time, Toast.LENGTH_SHORT).show();

            // Clear the input field if visible
            if (selectedClass.equals("Other")) {
                titleEditText.setText("");
            }
        });
    }
}
