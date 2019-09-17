package com.macbitsgoa.bitsgridwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadPowerDataWorker extends Worker implements LocationListener {

    private static final String TAG = UploadPowerDataWorker.class.getSimpleName();
    //get firebase realtime database references
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    //declare location variables
    private LocationManager locationManager;
    private String latitude, longitude;
    private Location loc;
    private String type = "";

    //declare and initialise time variables
    private Date currentTime = Calendar.getInstance().getTime();
    private String timevalue = currentTime.toString();

    private FusedLocationProviderClient mFusedLocationClient;

    public UploadPowerDataWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.e("workM", "doWork");

        // Do the work here--in this case, upload the images.
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        // Are we charging / charged?
        assert batteryStatus != null;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        Log.e("workM", "charging status " + status + " " + isCharging);

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (usbCharge) {
            type = "USB";
        }
        if (acCharge) {
            type = "AC";
        }

        Log.e("workM", "charging type " + usbCharge + " " + acCharge);

        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext().getApplicationContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);


        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


        //get user id

        GoogleSignInAccount googleSignInAccount;

        String uid = "guest";

        if ((googleSignInAccount = GoogleSignIn.getLastSignedInAccount(
                getApplicationContext())) != null) {
            uid = googleSignInAccount.getId();
        }

        //set firebase database variables
        String key = myRef.push().getKey();

        assert key != null;
        assert uid != null;
        DatabaseReference chargingState = myRef.child("Status").child(uid).child(key).child("ChargingState");
        DatabaseReference latitudeFb = myRef.child("Status").child(uid).child(key).child("Latitude");
        DatabaseReference longitudeFb = myRef.child("Status").child(uid).child(key).child("Longitude");
        DatabaseReference timeFb = myRef.child("Status").child(uid).child(key).child("Time");
        DatabaseReference typeFb = myRef.child("Status").child(uid).child(key).child("Type");


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


        Log.e("workM", "fb details" + uid + " " + key);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            // Got last known location. In some rare situations this can be null.
            if (location != null) {

                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                Log.e("workM", "locations " + latitude + " " + longitude);

                // Logic to handle location object

                if (latitude != null && longitude != null) {
                    //GeoCoding
                    String geoCode=getAddressFromLocation(Double.parseDouble(latitude),Double.parseDouble(longitude));

                    chargingState.setValue(isCharging);
                    latitudeFb.setValue(latitude);
                    longitudeFb.setValue(longitude);
                    timeFb.setValue(timevalue);
                    if (isCharging)
                        typeFb.setValue(type);
                    else
                        typeFb.setValue("");
                    Log.e("workM", "sent to fb ");

                } else {
                    Log.e("workM", "retry");
                }

            }

        });
        return Result.success();
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

    private String getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
        String geoCodeString="";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                String postalCode=fetchedAddress.getPostalCode();
                String locality=fetchedAddress.getLocality();

                geoCodeString=locality;
                Log.e("workM","Geo CODE"+geoCodeString);


            } else {
                geoCodeString="";
                Log.e("workM","address<=0"+geoCodeString);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("workM","catch: "+e.toString());
            //printToast("Could not get address..!");
        }
        return geoCodeString;
    }
}
