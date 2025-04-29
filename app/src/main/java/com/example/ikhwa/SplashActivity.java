package com.example.ikhwa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    private static final String PREF_NAME = "onboard_pref";
    private static final String IS_FIRST_TIME = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);
        animateFunc();
    }

    private void animateFunc() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade);
        imageView = findViewById(R.id.animlogo);
        anim.reset();
        imageView.clearAnimation();
        imageView.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean(IS_FIRST_TIME, true);

                Intent intent;
                if (isFirstTime) {
                    // Save first-time flag to false
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(IS_FIRST_TIME, false);
                    editor.apply();

                    intent = new Intent(SplashActivity.this, OnboardActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, SelectionActivity.class);
                }

                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
