package com.macbitsgoa.bitsgridwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private ActionBar actionBar;

    TextView app_name, version;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        actionBar = getSupportActionBar();
        actionBar.hide();

        app_name = findViewById(R.id.app_name);
        version = findViewById(R.id.version);

        Typeface typeface_medium = getResources().getFont(R.font.montserrat_medium);
        Typeface typeface_regular = getResources().getFont(R.font.montserrat_regular);

        app_name.setTypeface(typeface_medium);
        version.setTypeface(typeface_regular);



    }
}
