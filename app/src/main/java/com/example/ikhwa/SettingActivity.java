package com.example.ikhwa;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class SettingActivity extends AppCompatActivity {

    private SwitchCompat notificationsSwitch;
    private View layoutPrivacyPolicy, layoutTerms, layoutShare, layoutAbout, layoutHelp, layoutRate;
    private FloatingActionButton fabLogout;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); // Use your layout file here

        authProfile = FirebaseAuth.getInstance();

        // Initialize UI elements
        notificationsSwitch = findViewById(R.id.switch_notifications);
        layoutPrivacyPolicy = findViewById(R.id.layout_privacy_policy);
        layoutTerms = findViewById(R.id.layout_terms);
        layoutShare = findViewById(R.id.layout_share);
        layoutAbout = findViewById(R.id.layout_about);
        layoutHelp = findViewById(R.id.layout_help);
        layoutRate = findViewById(R.id.layout_rate);
        fabLogout = findViewById(R.id.fab_logout);

        setupListeners();
        loadNotificationPreference();
    }

    private void setupListeners() {
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationPreference(isChecked);
                showToast(isChecked ? "Notifications enabled" : "Notifications disabled");
            }
        });

        layoutPrivacyPolicy.setOnClickListener(v -> navigateToPage("privacy_policy"));
        layoutTerms.setOnClickListener(v -> navigateToPage("terms_conditions"));
        layoutShare.setOnClickListener(v -> shareApp());
        layoutAbout.setOnClickListener(v -> navigateToPage("about_us"));
        layoutHelp.setOnClickListener(v -> navigateToPage("help"));
        layoutRate.setOnClickListener(v -> rateApp());

        fabLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void saveNotificationPreference(boolean enabled) {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("notifications_enabled", enabled)
                .apply();
    }

    private void loadNotificationPreference() {
        boolean enabled = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("notifications_enabled", true);
        notificationsSwitch.setChecked(enabled);
    }

    private void navigateToPage(String page) {
        switch (page) {
            case "privacy_policy":
                showToast("Opening Privacy Policy");
                startActivity(new Intent(this, PrivacyPolicyActivity4.class));
                break;
            case "terms_conditions":
                showToast("Opening Terms and Conditions");
                startActivity(new Intent(this, TermsActivity.class));
                break;
            case "about_us":
                showToast("Opening About Us");
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this amazing app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey! I'm using this awesome app. Check it out: https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void rateApp() {
        showToast("Opening app in Play Store for rating");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear all relevant SharedPreferences on logout
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit().clear().apply();

                    // Also clear the user role preferences used in SplashActivity
                    getSharedPreferences(SplashActivity.PREF_NAME, MODE_PRIVATE).edit().clear().apply();

                    // Sign out from Firebase
                    authProfile.signOut();

                    // Show Toast message
                    showToast("Logged out successfully.");

                    // Navigate to the SelectionActivity
                    Intent intent = new Intent(this, SelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
