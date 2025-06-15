package com.example.ikhwa;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ikhwa.modules.OnboardItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardActivity extends AppCompatActivity {

    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        onboardingViewPager = findViewById(R.id.onboardViewPager);
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);

        List<OnboardItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardItem("Welcome to Ikhwa", "Smarter education starts here", R.drawable.first));
        onboardingItems.add(new OnboardItem("Learn, Grow and Stay Connected", "Track your courses, stay updated and progress with ease", R.drawable.second));

        onboardingViewPager.setAdapter(new OnboardAdapter(onboardingItems));

        setupIndicators(onboardingItems.size());
        setCurrentIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.indicator_inactive));
            }
        }
    }
}
