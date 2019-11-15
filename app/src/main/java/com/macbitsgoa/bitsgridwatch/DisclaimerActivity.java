package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class DisclaimerActivity extends AppCompatActivity {

    private Button acceptButton;
    private Button declineButton;

    private ActionBar actionBar;

    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText("Privacy Policy");
        app_title.setTextSize(20);

        Typeface typeface_medium = getResources().getFont(R.font.montserrat_medium);

        app_title.setTypeface(typeface_medium);

        actionBar.setCustomView(app_title);

        //shared preferences for theme
        theme_shared_preferences = Objects.requireNonNull(this).getSharedPreferences("ThemeOptions", MODE_PRIVATE);
        int theme = theme_shared_preferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);

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


        SharedPreferences prefs = getSharedPreferences("AllowMoniSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if(!prefs.getBoolean("first_time",true) && prefs.getBoolean("disclaimer",false)){
            //User has already allowed earlier.
            //Open main activity
            Intent mainIntent = new Intent(DisclaimerActivity.this, MainActivity.class);
            DisclaimerActivity.this.startActivity(mainIntent);
            DisclaimerActivity.this.finish();
        }
        acceptButton = findViewById(R.id.button_accept_disclaimer);
        declineButton = findViewById(R.id.button_decline_disclaimer);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set some boolean
                editor.putBoolean("disclaimer",true);
                editor.putBoolean("first_time",false);
                editor.apply();
                Intent mainIntent = new Intent(DisclaimerActivity.this, MainActivity.class);
                DisclaimerActivity.this.startActivity(mainIntent);
                DisclaimerActivity.this.finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DisclaimerActivity.this, "You must Accept in order to proceed!", Toast.LENGTH_LONG).show();
                editor.putBoolean("disclaimer",false);
                editor.putBoolean("first_time",false);
                editor.apply();
            }
        });
    }
}
