package com.macbitsgoa.bitsgridwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private ActionBar actionBar;

    TextView app_name, version_name, version_code, copyright_text;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText("About");
        app_title.setTextSize(20);

        Typeface typeface_medium = getResources().getFont(R.font.montserrat_medium);
        Typeface typeface_regular = getResources().getFont(R.font.montserrat_regular);

        app_title.setTypeface(typeface_medium);

        actionBar.setCustomView(app_title);

        app_name = findViewById(R.id.app_name);
        version_name = findViewById(R.id.version_name);
        version_code = findViewById(R.id.version_code);
        copyright_text = findViewById(R.id.copyright_text);

        app_name.setTypeface(typeface_medium);
        version_name.setTypeface(typeface_regular);
        version_code.setTypeface(typeface_regular);
        copyright_text.setTypeface(typeface_regular);

        copyright_text.setText("\u00a9" + "BITS Pilani K K Birla Goa Campus");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version_name.setText("Version Name: " + pInfo.versionName);
            version_code.setText("Version Code: " + pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



    }
}
