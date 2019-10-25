package com.macbitsgoa.bitsgridwatch.rankings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

public class RankingsActivity extends AppCompatActivity {

    TextView scoreTv;
    RecyclerView ranksRv;
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    String uid = "guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

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
                    scoreTv.setText(String.valueOf(score));
                } else if (uid.equals("quest")) {
                    scoreTv.setText("Score not available for guest user.");
                } else {
                    scoreTv.setText("0");
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
