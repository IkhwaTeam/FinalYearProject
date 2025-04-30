package com.example.ikhwa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    // IMPORTANT: Match these constants with what's used in your login activities
    public static final String PREF_NAME = "user_role_pref"; // Must match login activities
    public static final String ROLE_KEY = "role"; // Must match login activities

    private ImageView imageView;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Your splash screen layout file

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Start the animation for the splash screen
        animateFunc();
    }

    private void animateFunc() {
        // Load fade animation from resources
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade); // Replace with your actual fade animation file
        imageView = findViewById(R.id.animlogo); // Make sure you have an ImageView with this ID in your layout
        anim.reset();
        imageView.clearAnimation();
        imageView.startAnimation(anim);

        // Set an AnimationListener to handle the logic after the animation ends
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                navigateToAppropriateScreen();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void navigateToAppropriateScreen() {
        // Check if the user is logged in via Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            // User is logged in, check the saved role
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String role = sharedPreferences.getString(ROLE_KEY, ""); // Retrieve the saved role
            Log.d(TAG, "Current user ID: " + currentUser.getUid() + ", Role: " + role);

            // Navigate based on the role
            switch (role) {
                case "admin":
                    // Admin is logged in, go to the Admin Home
                    Log.d(TAG, "Redirecting to AdminHomeActivity");
                    intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                    // Clear task and make new task to remove back stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "teacher":
                    // Teacher is logged in, go to the Teacher Home
                    Log.d(TAG, "Redirecting to Teacher_home");
                    intent = new Intent(SplashActivity.this, Teacher_home.class);
                    // Clear task and make new task to remove back stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "student":
                    // Student is logged in, go to the Student Home
                    Log.d(TAG, "Redirecting to StudentHome2");
                    intent = new Intent(SplashActivity.this, StudentHome2.class);
                    // Clear task and make new task to remove back stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                default:
                    // If no valid role found, go to the selection screen
                    Log.d(TAG, "No role found, redirecting to SelectionActivity");
                    intent = new Intent(SplashActivity.this, SelectionActivity.class);
                    break;
            }
        } else {
            // If not logged in, go to the selection screen
            Log.d(TAG, "No user logged in, redirecting to SelectionActivity");
            intent = new Intent(SplashActivity.this, SelectionActivity.class);
        }

        // Start the appropriate activity
        startActivity(intent);
        // Close the splash activity so it doesn't remain in the back stack
        finish();
    }
}