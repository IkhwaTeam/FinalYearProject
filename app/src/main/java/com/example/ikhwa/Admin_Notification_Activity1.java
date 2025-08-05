package com.example.ikhwa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Admin_Notification_Activity1 extends AppCompatActivity {

    EditText titleEditText, descEditText;
    Button sendBtn;

    // 🔴 Replace with your actual FCM server key from Firebase Console
    private final String SERVER_KEY = "AAAAXXXXXX:APA91b....";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification1);

        titleEditText = findViewById(R.id.etTitle);
        descEditText = findViewById(R.id.etDescription);
        sendBtn = findViewById(R.id.btnsend);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String desc = descEditText.getText().toString();

                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(Admin_Notification_Activity1.this, "Fill both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendNotificationToTopic(title, desc);
            }
        });
    }

    private void sendNotificationToTopic(String title, String desc) {
        Thread thread = new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                JSONObject notification = new JSONObject();
                JSONObject data = new JSONObject();

                payload.put("to", "/topics/Teacher");  // ✅ Change to Student if needed

                notification.put("title", title);
                notification.put("body", desc);
                notification.put("sound", "default");
                payload.put("notification", notification);

                data.put("title", title);
                data.put("description", desc);
                payload.put("data", data);

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(Admin_Notification_Activity1.this, "Notification sent!", Toast.LENGTH_SHORT).show();
                        saveToFirebase(title, desc);
                    } else {
                        Toast.makeText(Admin_Notification_Activity1.this, "Sending failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void saveToFirebase(String title, String desc) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TeacherNotifications");
        String key = ref.push().getKey();
        ref.child(key).child("title").setValue(title);
        ref.child(key).child("description").setValue(desc);
    }
}
