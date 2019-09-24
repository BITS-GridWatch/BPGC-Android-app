package com.macbitsgoa.bitsgridwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashScreen extends AppCompatActivity {


    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //set theme
        //shared preferences for theme
        SharedPreferences theme_shared_preferences = this.getSharedPreferences("ThemeOptions", MODE_PRIVATE);
        int theme = theme_shared_preferences.getInt("Theme", 0);

        AppCompatDelegate.setDefaultNightMode(theme);


        //shared preferences for onboarding
        SharedPreferences onboarding_shared_preferences = this.getSharedPreferences("Onboarding",MODE_PRIVATE);
        boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete",false);


        actionBar = getSupportActionBar();
        actionBar.hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //if onboarding not completed i.e. first time user
                if (onboarding_complete == false)
                {
                    Intent mainIntent = new Intent(SplashScreen.this, OnboardingActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
                //if onboarding already completed
                else
                {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);




    }

}
