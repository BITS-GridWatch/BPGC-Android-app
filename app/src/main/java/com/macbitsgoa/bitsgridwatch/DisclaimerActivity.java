package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DisclaimerActivity extends AppCompatActivity {

    private Button acceptButton;
    private Button declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

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
