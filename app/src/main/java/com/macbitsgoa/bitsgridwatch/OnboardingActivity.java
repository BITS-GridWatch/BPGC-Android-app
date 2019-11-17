package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;


public class OnboardingActivity extends AppCompatActivity {


    ViewPager slide_pager;
    LinearLayout dot_layout;

    private TextView[] mdots;

    private OnboardingSliderAdapter sliderAdapter;

    Button skip_button, next_button;

    private int currentpage;

    private ActionBar actionBar;

    //shared preferences for onboarding
    private SharedPreferences onboarding_shared_preferences;
    private SharedPreferences.Editor onboarding_editor;
    private SharedPreferences prefs;


    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        actionBar = getSupportActionBar();
        actionBar.hide();

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
            RelativeLayout background_layout = decor.findViewById(R.id.background_layout);
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
                RelativeLayout background_layout = decor.findViewById(R.id.background_layout);
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


        slide_pager = findViewById(R.id.slide_pager);
        dot_layout = findViewById(R.id.dot_layout);


        next_button = findViewById(R.id.next_button);
        skip_button = findViewById(R.id.skip_button);

        sliderAdapter = new OnboardingSliderAdapter(this);

        slide_pager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        slide_pager.addOnPageChangeListener(viewListener);

        prefs = getSharedPreferences("AllowMoniSharedPref", MODE_PRIVATE);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentpage == 2) {

                    //shared preferences for onboarding
                    onboarding_shared_preferences = getSharedPreferences("Onboarding", MODE_PRIVATE);

                    boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete", false);

                    if (!onboarding_complete) {
                        onboarding_editor = onboarding_shared_preferences.edit();

                        onboarding_editor.putBoolean("Onboarding Complete", true);
                        onboarding_editor.apply();
                        Intent mainIntent;
                        if (!prefs.getBoolean("first_time", true) && prefs.getBoolean("disclaimer", false)) {
                            mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
                        } else {
                            mainIntent = new Intent(OnboardingActivity.this, DisclaimerActivity.class);
                        }
                        OnboardingActivity.this.startActivity(mainIntent);

                        OnboardingActivity.this.finish();
                    } else {
                        OnboardingActivity.this.finish();

                    }

                } else {
                    slide_pager.setCurrentItem(currentpage + 1);
                }

            }
        });


        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //shared preferences for onboarding
                onboarding_shared_preferences = getSharedPreferences("Onboarding", MODE_PRIVATE);

                boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete", false);

                if (!onboarding_complete) {
                    onboarding_editor = onboarding_shared_preferences.edit();

                    onboarding_editor.putBoolean("Onboarding Complete", true);
                    onboarding_editor.apply();

                    Intent mainIntent;
                    if (!prefs.getBoolean("first_time", true) && prefs.getBoolean("disclaimer", false)) {
                        mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
                    } else {
                        mainIntent = new Intent(OnboardingActivity.this, DisclaimerActivity.class);
                    }
                    OnboardingActivity.this.startActivity(mainIntent);
                    OnboardingActivity.this.finish();
                } else {
                    OnboardingActivity.this.finish();

                }
            }
        });
    }


    public void addDotsIndicator(int position) {
        mdots = new TextView[3];
        dot_layout.removeAllViews();

        for (int i = 0; i < mdots.length; i++) {
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.colorAccent, getTheme()));

            dot_layout.addView(mdots[i]);
        }

        if (mdots.length > 0) {
            mdots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);

            currentpage = position;

            if (position == mdots.length - 1) {
                next_button.setText("LET'S GO!");
                skip_button.setEnabled(false);
                skip_button.setText("");
            } else {
                next_button.setText("NEXT");
                skip_button.setEnabled(true);
                skip_button.setText("SKIP");
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
