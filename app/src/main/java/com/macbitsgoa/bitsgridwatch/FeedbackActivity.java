package com.macbitsgoa.bitsgridwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class FeedbackActivity extends AppCompatActivity {

    private ActionBar actionBar;

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

        rating_bar.setNumStars(5);  //number of stars
        rating_bar.setStepSize(1);  //step size

        rating_bar.setRating(3); //default rating

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
