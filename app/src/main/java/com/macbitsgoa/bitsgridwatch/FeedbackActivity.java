package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    private ActionBar actionBar;

    //shared preferences for current fragment
    private SharedPreferences current_fragment;
    private SharedPreferences.Editor current_fragment_editor;


    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        EditText feedback_edit_text = findViewById(R.id.feedback_edit_text);
        RatingBar rating_bar = findViewById(R.id.rating_bar);
        Button submit_button = findViewById(R.id.submit_button);


        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText("Feedback");
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


        //shared preferences for current fragment
        current_fragment = Objects.requireNonNull(this).getSharedPreferences("current_fragment", MODE_PRIVATE);
        current_fragment_editor = current_fragment.edit();
        current_fragment_editor.putInt("fragment", 1);
        current_fragment_editor.apply();



        rating_bar.setNumStars(5);  //number of stars
        rating_bar.setStepSize(1);  //step size

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                Log.d("rating changed to", "" + v);
            }
        });

        feedback_edit_text.setImeOptions(EditorInfo.IME_ACTION_DONE);
        feedback_edit_text.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        feedback_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    Log.d("ime option status", "success");
                    return true;
                }
                return false;
            }
        });


        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rating_bar.getRating() == 0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please provide a rating.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                Date currentTime = Calendar.getInstance().getTime();
                String timevalue = currentTime.toString();

                GoogleSignInAccount googleSignInAccount;

                String uid = "guest";

                if ((googleSignInAccount = (GoogleSignInAccount) GoogleSignIn.getLastSignedInAccount(getApplicationContext())) != null) {
                    uid = googleSignInAccount.getId();
                }

                final DatabaseReference star_rating = myRef.child("Feedback").child(timevalue).child("Rating");
                final DatabaseReference text_feedback = myRef.child("Feedback").child(timevalue).child("Details");
                final DatabaseReference user_id = myRef.child("Feedback").child(timevalue).child("User ID");

                star_rating.setValue(rating_bar.getRating());
                text_feedback.setValue(feedback_edit_text.getText().toString());
                user_id.setValue(uid);


                Log.d("rating","" + rating_bar.getRating());
                Log.d("feedback", feedback_edit_text.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(),"Your feedback has been sent.", Toast.LENGTH_SHORT);
                toast.show();

                onBackPressed();
            }
        });
    }
}
