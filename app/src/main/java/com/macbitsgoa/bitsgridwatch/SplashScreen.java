package com.macbitsgoa.bitsgridwatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashScreen extends AppCompatActivity {


    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //set theme
        //shared preferences for theme
        SharedPreferences theme_shared_preferences = this.getSharedPreferences("ThemeOptions", MODE_PRIVATE);
        int theme = theme_shared_preferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);

        AppCompatDelegate.setDefaultNightMode(theme);


        //shared preferences for onboarding
        SharedPreferences onboarding_shared_preferences = this.getSharedPreferences("Onboarding", MODE_PRIVATE);
        boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete", false);


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
            //change status bar colour
            window.setStatusBarColor(getResources().getColor(R.color.white));
            //change action bar colour
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
            // set status bar contrast
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (theme == AppCompatDelegate.MODE_NIGHT_YES) {
            //change status bar colour
            window.setStatusBarColor(getResources().getColor(R.color.surface));
            //change action bar colour
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.surface)));
            // change window background colour
            View decor = window.getDecorView();
            LinearLayout background_layout = decor.findViewById(R.id.background_layout);
            background_layout.setBackgroundColor(getResources().getColor(R.color.surface));
        } else {
            PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isPowerSaveMode()) {
                //change status bar colour
                window.setStatusBarColor(getResources().getColor(R.color.surface));
                //change action bar colour
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.surface)));
                // change window background colour
                View decor = window.getDecorView();
                LinearLayout background_layout = decor.findViewById(R.id.background_layout);
                background_layout.setBackgroundColor(getResources().getColor(R.color.surface));
            } else {
                //change status bar colour
                window.setStatusBarColor(getResources().getColor(R.color.white));
                //change action bar colour
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                // set status bar contrast
                View decor = window.getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //if onboarding not completed i.e. first time user
                if (!onboarding_complete) {
                    Intent intent = new Intent(SplashScreen.this, OnboardingActivity.class);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();
                }
                //if onboarding already completed
                else {
                    Intent intent = new Intent(SplashScreen.this, DisclaimerActivity.class);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);


    }

}
