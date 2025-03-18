package com.example.ikhwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ✅ Initialize Firebase
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
                Intent intent = new Intent(SplashActivity.this, SelectionActivity.class);
                startActivity(intent);
                finish(); // Close splash screen
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
