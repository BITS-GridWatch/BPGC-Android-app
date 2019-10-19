package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        actionBar = getSupportActionBar();
        actionBar.hide();

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

                    boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete",false);

                    if (!onboarding_complete)
                    {
                        onboarding_editor = onboarding_shared_preferences.edit();

                        onboarding_editor.putBoolean("Onboarding Complete", true);
                        onboarding_editor.apply();
                        Intent mainIntent;
                        if(!prefs.getBoolean("first_time",true) && prefs.getBoolean("disclaimer",false)){
                            mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
                        }
                        else {
                            mainIntent = new Intent(OnboardingActivity.this, DisclaimerActivity.class);
                        }
                        OnboardingActivity.this.startActivity(mainIntent);

                        OnboardingActivity.this.finish();
                    }
                    else
                    {
                        OnboardingActivity.this.finish();

                    }

                }
                else {
                    slide_pager.setCurrentItem(currentpage + 1);
                }

            }
        });


        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //shared preferences for onboarding
                onboarding_shared_preferences = getSharedPreferences("Onboarding", MODE_PRIVATE);

                boolean onboarding_complete = onboarding_shared_preferences.getBoolean("Onboarding Complete",false);

                if (!onboarding_complete)
                {
                    onboarding_editor = onboarding_shared_preferences.edit();

                    onboarding_editor.putBoolean("Onboarding Complete", true);
                    onboarding_editor.apply();

                    Intent mainIntent;
                    if(!prefs.getBoolean("first_time",true) && prefs.getBoolean("disclaimer",false)){
                        mainIntent = new Intent(OnboardingActivity.this, MainActivity.class);
                    }
                    else {
                        mainIntent = new Intent(OnboardingActivity.this, DisclaimerActivity.class);
                    }
                    OnboardingActivity.this.startActivity(mainIntent);
                    OnboardingActivity.this.finish();
                }
                else
                {
                    OnboardingActivity.this.finish();

                }
            }
        });
    }



    public void addDotsIndicator(int position)
    {
        mdots = new TextView[3];
        dot_layout.removeAllViews();

        for (int i=0; i<mdots.length; i++)
        {
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.colorAccent));

            dot_layout.addView(mdots[i]);
        }

        if (mdots.length > 0)
        {
            mdots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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

            if (position == mdots.length - 1)
            {
                next_button.setText("LET'S GO!");
                skip_button.setEnabled(false);
                skip_button.setText("");
            }
            else
            {
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
