package com.macbitsgoa.bitsgridwatch;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import androidx.core.app.ActivityCompat;


public class PowerConnectionReceiver extends BroadcastReceiver implements LocationListener {

    //get firebase realtime database references
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    //declare location variables
    private LocationManager locationManager;
    private String latitude, longitude;
    private Location loc;


    //declare and initialise time variables
    Date currentTime = Calendar.getInstance().getTime();
    String timevalue = currentTime.toString();
    public Criteria criteria;
    public String bestProvider;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    public void onReceive(Context context, Intent intent) {

        //check for location permission
        //intentThatCalled = this.getIntent();
        //voice2text = intentThatCalled.getStringExtra("v2txt");

        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context.getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);


        }

        //locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        //criteria = new Criteria();
        //bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        //get location details

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                            // Logic to handle location object
                        }

                    }
                });


        //get charging state 1

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;


        //get user id

        GoogleSignInAccount googleSignInAccount;

        String uid = "guest";

        if ((googleSignInAccount = (GoogleSignInAccount) GoogleSignIn.getLastSignedInAccount(context)) != null) {
            uid = googleSignInAccount.getId();
        }

        //set firebase database variables
        String key = myRef.push().getKey();

        final DatabaseReference chargingstate = myRef.child("Status").child(uid).child(key).child("ChargingState");
        final DatabaseReference Latitude = myRef.child("Status").child(uid).child(key).child("Latitude");
        final DatabaseReference Longitude = myRef.child("Status").child(uid).child(key).child("Longitude");
        final DatabaseReference Time = myRef.child("Status").child(uid).child(key).child("Time");


        final DatabaseReference name = myRef.child("Users").child(uid).child("Name");
        final DatabaseReference email = myRef.child("Users").child(uid).child("Email");

        if (googleSignInAccount != null) {
            name.setValue(googleSignInAccount.getDisplayName());
            email.setValue(googleSignInAccount.getEmail());
        } else {
            name.setValue("Guest user name");
            email.setValue("Guest user email");
        }

//        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
//        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;


        // push data to firebase
        if (latitude != null && longitude != null) {
            chargingstate.setValue(isCharging);
            Latitude.setValue(latitude);
            Longitude.setValue(longitude);
            Time.setValue(timevalue);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);

        //open the map:
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        //Toast.makeText(, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        //searchNearestPlace(voice2text);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
