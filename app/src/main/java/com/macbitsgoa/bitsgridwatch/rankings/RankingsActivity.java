package com.macbitsgoa.bitsgridwatch.rankings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.bitsgridwatch.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class RankingsActivity extends AppCompatActivity {

    TextView scoreTv;
    RecyclerView ranksRv;
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    String uid = "guest";

    private ActionBar actionBar;

    //shared preferences for current fragment
    private SharedPreferences current_fragment;
    private SharedPreferences.Editor current_fragment_editor;

    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);


        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText("Rankings");
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
            ScrollView background_layout = decor.findViewById(R.id.background_layout);
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
                ScrollView background_layout = decor.findViewById(R.id.background_layout);
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



        scoreTv = findViewById(R.id.your_score);
        ranksRv = findViewById(R.id.rank_list_rv);
        ranksRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));

        GoogleSignInAccount googleSignInAccount;


        if ((googleSignInAccount = GoogleSignIn.getLastSignedInAccount(
                getApplicationContext())) != null) {
            uid = googleSignInAccount.getId();
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!uid.equals("guest") && !uid.equals("debug") &&
                        dataSnapshot.child(uid).child("Score").getValue(Integer.class) != null) {
                    int score = dataSnapshot.child(uid).child("Score").getValue(Integer.class);
                    scoreTv.setText("Your Score: " + String.valueOf(score));
                    scoreTv.setTextSize(20);
                } else if (uid.equals("guest")) {
                    scoreTv.setText("Score not available for guest user.");
                    scoreTv.setTextSize(15);
                } else {
                    scoreTv.setText("Your Score: " + "0");
                    scoreTv.setTextSize(20);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Rankings", databaseError.toString());
            }
        });

        ArrayList<Integer> scores = new ArrayList<>();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.child("Score").getValue(Integer.class) != null) {
                        scores.add(childSnapshot.child("Score").getValue(Integer.class));
                    }
                }
                Collections.sort(scores, Collections.reverseOrder());
                Log.e("Rankings", scores.toString());
                ArrayList<RankingModel> rankingModels = new ArrayList<>();
                for (int i = 0; i < scores.size() && i < 10; i++) {
                    rankingModels.add(new RankingModel(scores.get(i), i + 1));

                }


                ranksRv.setAdapter(new RankingAdapter(rankingModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Rankings", databaseError.toString());
            }
        });

    }
}
