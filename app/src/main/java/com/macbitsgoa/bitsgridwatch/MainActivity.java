package com.macbitsgoa.bitsgridwatch;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNav;
    private FrameLayout frameLayout;
    private Fragment selectedFragment = null;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private Boolean signedInStatus;
    private GoogleSignInAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Initialize last signed in user
        userAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(userAccount != null)
            signedInStatus = true;
        else
            signedInStatus = false;

        bottomNav = findViewById(R.id.bottomnav_activity_main);
        frameLayout = findViewById(R.id.framelayout_activity_main);

        bottomNav.setSelectedItemId(R.id.item_bottomnav_home);  //Set Home as default selected.
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_bottomnav_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.item_bottomnav_settings:
                    selectedFragment = SettingsFragment.newInstance();
                    break;
                default:                                        //Open Home if nothing is selected somehow.
                    selectedFragment = HomeFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout_activity_main, selectedFragment);
            transaction.commit();
            //Replace the currently displaying fragment with the one selected and apply the transaction.
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_activity_main, HomeFragment.newInstance());
        transaction.commit();

        if(!signedInStatus){
            userSignIn();
        }
    }

    // [START signin]
    public void userSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            userAccount = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed In as " + userAccount.getDisplayName().toString(), Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            userAccount = null;
        }
    }

    public Boolean getSignedInStatus(){
        return signedInStatus;
    }

    public void setSignedInStatus(Boolean newStatus){
        signedInStatus = newStatus;
    }

    public GoogleSignInAccount getUserAccount() {
        return userAccount;
    }

    public void userSignOut(){
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,"Successfully Signed Out!",Toast.LENGTH_SHORT).show();
            }
        });
        setSignedInStatus(false);
        userAccount = null;
    }
}
