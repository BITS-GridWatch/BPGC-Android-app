package com.macbitsgoa.bitsgridwatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

    //shared preferences for current fragment
    private SharedPreferences current_fragment;

    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView app_title = new TextView(this);
        app_title.setText(getResources().getString(R.string.app_name));
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

        // set bottom navigation menu background colour

        int[][] states = new int[][] {


                new int[] {android.R.attr.state_checked}, // checked
                new int[] {-android.R.attr.state_checked}  // unchecked
        };

        int[] colors = new int[] {

                getResources().getColor(R.color.colorPrimary),
                Color.GRAY
        };



        ColorStateList colorStateList = new ColorStateList(states, colors);

        bottomNav.setItemTextColor(colorStateList);
        bottomNav.setItemIconTintList(colorStateList);


        if (theme == AppCompatDelegate.MODE_NIGHT_NO)
        {
            bottomNav.setBackgroundColor(getResources().getColor(R.color.white));

        }
        else if (theme == AppCompatDelegate.MODE_NIGHT_YES)
        {
            bottomNav.setBackgroundColor(getResources().getColor(R.color.surface));

        }
        else
        {
            PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isPowerSaveMode()) {
                bottomNav.setBackgroundColor(getResources().getColor(R.color.surface));

            } else {
                bottomNav.setBackgroundColor(getResources().getColor(R.color.white));

            }
        }


        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_bottomnav_settings:
                    selectedFragment = SettingsFragment.newInstance();
                    item.setIcon(R.drawable.icon_new_settings_fragment_selected);
                    Menu menu = bottomNav.getMenu();
                    menu.findItem(R.id.item_bottomnav_home).setIcon(R.drawable.icon_new_home_fragment_unselected);
                    break;
                default:                                        //Open Home if nothing is selected somehow.
                    //selectedFragment = HomeFragment.newInstance();
                    selectedFragment = new HomeFragment();
                    item.setIcon(R.drawable.icon_new_home_fragment_selected);
                    Menu menu1 = bottomNav.getMenu();
                    menu1.findItem(R.id.item_bottomnav_settings).setIcon(R.drawable.icon_new_settings_fragment_unselected);
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout_activity_main, selectedFragment);
            transaction.commit();
            //Replace the currently displaying fragment with the one selected and apply the transaction.
            return true;
        });


        //current fragment
        current_fragment = this.getSharedPreferences("current_fragment", MODE_PRIVATE);
        int currentFragmentInt = current_fragment.getInt("fragment", 0);
        if (currentFragmentInt == 1)
        {
            bottomNav.setSelectedItemId(R.id.item_bottomnav_settings);
            Log.e("bottom nav item","settings");
        }
        else
        {
            bottomNav.setSelectedItemId(R.id.item_bottomnav_home);
            Log.e("bottom nav item","home");
        }

        //Initialize WorkManager variables:
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        saveRequest = new PeriodicWorkRequest.Builder(UploadPowerDataWorker.class, 15,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        if (currentFragmentInt == 0)
        {
            Toast.makeText(this, "Marks on Map show presence of power supply.",
                    Toast.LENGTH_SHORT).show();
        }

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomnav_activity_main);

        //current fragment
        current_fragment = this.getSharedPreferences("current_fragment", MODE_PRIVATE);
        int currentFragmentInt = current_fragment.getInt("fragment", 0);
        Log.e("fragment",currentFragmentInt + "");
        if(currentFragmentInt == 1)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.replace(R.id.framelayout_activity_main, HomeFragment.newInstance());
            transaction.replace(R.id.framelayout_activity_main, new SettingsFragment());
            transaction.commit();
            Log.e("entered","settings");
            bottomNav.setSelectedItemId(R.id.item_bottomnav_settings);
        }
        else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.replace(R.id.framelayout_activity_main, HomeFragment.newInstance());
            transaction.replace(R.id.framelayout_activity_main, new HomeFragment());
            transaction.commit();
            Log.e("entered","home");
            bottomNav.setSelectedItemId(R.id.item_bottomnav_home);
        }

        //shared preferences for current fragment
        SharedPreferences current_fragment;
        SharedPreferences.Editor current_fragment_editor;

        //shared preferences for current fragment
        current_fragment = Objects.requireNonNull(this).getSharedPreferences("current_fragment", MODE_PRIVATE);
        current_fragment_editor = current_fragment.edit();
        current_fragment_editor.putInt("fragment", 0);
        current_fragment_editor.apply();

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
