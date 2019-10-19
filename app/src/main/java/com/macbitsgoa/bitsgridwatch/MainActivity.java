package com.macbitsgoa.bitsgridwatch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Fragment selectedFragment = null;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private Boolean signedInStatus = false;
    private Boolean allowMonitoring = false;
    private GoogleSignInAccount userAccount;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 434;

    private static PeriodicWorkRequest saveRequest;

    private ActionBar actionBar;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText("UJALA");
        app_title.setTextSize(20);

        Typeface typeface_medium = getResources().getFont(R.font.montserrat_medium);
        app_title.setTypeface(typeface_medium);

        actionBar.setCustomView(app_title);

//        //set theme
//        //shared preferences for theme
//        SharedPreferences theme_shared_preferences = this.getSharedPreferences("ThemeOptions", MODE_PRIVATE);
//        int theme = theme_shared_preferences.getInt("Theme", 0);
//
//        AppCompatDelegate.setDefaultNightMode(theme);


        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "please provide the permission",
                        Toast.LENGTH_SHORT).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        } else {
            // Permission has already been granted
            Log.e("MainActivity", "Permission granted");

            //   googleMap.setMyLocationEnabled(true);
        }


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
        signedInStatus = userAccount != null;

        BottomNavigationView bottomNav = findViewById(R.id.bottomnav_activity_main);
        FrameLayout frameLayout = findViewById(R.id.framelayout_activity_main);

        bottomNav.setSelectedItemId(R.id.item_bottomnav_home);  //Set Home as default selected.
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_bottomnav_settings:
                    selectedFragment = SettingsFragment.newInstance();
                    break;
                default:                                        //Open Home if nothing is selected somehow.
                    //selectedFragment = HomeFragment.newInstance();
                    selectedFragment = new HomeFragment();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout_activity_main, selectedFragment);
            transaction.commit();
            //Replace the currently displaying fragment with the one selected and apply the transaction.
            return true;
        });

        //activating the PowerConnectionReceiver
/*

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver();
        powerConnectionReceiver.onReceive(getApplicationContext(), batteryStatus);

        registerReceiver(powerConnectionReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
*/
        //Initialize WorkManager variables:
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        saveRequest = new PeriodicWorkRequest.Builder(UploadPowerDataWorker.class, 15,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        Toast.makeText(this, "Marks on Map show presence of power supply.",
                Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("AllowMoniSharedPref", MODE_PRIVATE);
        if (prefs.getBoolean("allow", true)) {
            startBackgroundWork();
        } else {
            Toast.makeText(this, "Please Allow Monitoring to help the community",
                    Toast.LENGTH_SHORT).show();
            Log.e("workM", "Allow Monitoring: no");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.framelayout_activity_main, HomeFragment.newInstance());
        transaction.replace(R.id.framelayout_activity_main, new HomeFragment());
        transaction.commit();

        if (!signedInStatus) {
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
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            userAccount = completedTask.getResult(ApiException.class);
            assert userAccount != null;
            Toast.makeText(MainActivity.this, "Signed In as " + userAccount.getDisplayName(), Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code = " + e.getStatusCode());
            userAccount = null;
        }
    }

    public Boolean getSignedInStatus() {
        return signedInStatus;
    }

    public void setSignedInStatus(Boolean newStatus) {
        signedInStatus = newStatus;
    }

    public GoogleSignInAccount getUserAccount() {
        return userAccount;
    }

    public void userSignOut() {
        if (signedInStatus) {
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "Successfully Signed Out!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Sign-Out not possible without a Sign-In", Toast.LENGTH_SHORT).show();
        }
        setSignedInStatus(false);
        userAccount = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location Permission granted");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.e(TAG, "Location Permission denied");
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }// other 'case' lines to check for other
        // permissions this app might request.
    }

    public Boolean getAllowMonitoring() {
        return allowMonitoring;
    }

    public void setAllowMonitoring(Boolean allowMonitoring) {
        this.allowMonitoring = allowMonitoring;
    }

    public void startBackgroundWork() {
        //worker
        Log.e("workM", "worker started");

        /*OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(UploadPowerDataWorker.class)
                .build();
        */
        WorkManager.getInstance(this).enqueue(saveRequest);
    }

    public void cancelBackgroundWork() {
        WorkManager.getInstance(this).cancelWorkById(saveRequest.getId());
        Log.d("workM", "Background Work cancelled");
    }
}
