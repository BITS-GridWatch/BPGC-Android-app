package com.macbitsgoa.bitsgridwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 434;
    private DatabaseReference database;
    private final LatLng centre = new LatLng(15.389557, 73.876974);
    private Date currentTime = Calendar.getInstance().getTime();
    private FloatingActionButton fabA, fabB;
    private FloatingActionsMenu floatingActionsMenu;
    private int thresholdTime;


    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //-------------------------------
        //Initialize layout elements here
        //-------------------------------

        database = FirebaseDatabase.getInstance().getReference().child("Status");
        mMapView = view.findViewById(R.id.mapView);
        fabA = view.findViewById(R.id.action_a);
        fabB = view.findViewById(R.id.action_b);
        floatingActionsMenu = view.findViewById(R.id.fab_switch);
        mMapView.onCreate(savedInstanceState);
        thresholdTime = 60;
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                //set theme
                PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

                theme_shared_preferences  = getActivity().getSharedPreferences("ThemeOptions",MODE_PRIVATE);
                int theme = theme_shared_preferences.getInt("Theme",0);

                if (theme == AppCompatDelegate.MODE_NIGHT_YES || (theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY && powerManager.isPowerSaveMode()) )
                {
                    googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style_json)));

                }


                // For showing a move to my location button
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(getContext(), "please provide the permission",
                                Toast.LENGTH_SHORT).show();
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
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




                    googleMap.setMyLocationEnabled(true);
                }



                /*check if location is enabled or not*/

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                SettingsClient client = LocationServices.getSettingsClient(getActivity());
                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // All location settings are satisfied. The client can initialize
                        // location requests here.
                        // ...
                    }
                });

                task.addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(getActivity(),
                                        0);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }
                });

                googleMap.setMyLocationEnabled(true);

                mapData(googleMap);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 0));


                /*LatLng BITS = new LatLng(15.39, 73.88);
                googleMap.addMarker(new MarkerOptions().position(BITS).title("BITS Pilani Goa")
                        .snippet("Test Power Markings"));*/

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(centre)
                        .zoom(16f).tilt(10).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        fabSettings();

        return view;
    }

    private void fabSettings() {
        floatingActionsMenu.setEnabled(true);

        fabA.setTitle("power supply in the last 5 mins");
        fabA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("HomeFragment", "fab5" + thresholdTime);
                thresholdTime = 5;
                googleMap.clear();
                mapData(googleMap);

            }
        });
        fabB.setTitle("power supply in the last 1 hour");
        fabB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("HomeFragment", "fab60" + thresholdTime);
                thresholdTime = 60;
                googleMap.clear();
                mapData(googleMap);

            }
        });
    }

    private void mapData(GoogleMap googleMap) {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("HomeFragment", "record");
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    long lastChild = child.getChildrenCount();
                    //int i = 0;
                    Log.e("HomeFragment", "record" + child.getKey());

                    for (DataSnapshot grandChild : child.getChildren()) {

                        Log.e("HomeFragment", "record" + grandChild.getKey());

                        String time = grandChild.child("Time").getValue(String.class);
                        int diffMins = 0;
                        int diffHours = 0;
                        int diffDays = 0;
                        if (time == null) {
                            continue;
                        }
                        try {
                            Date dateFb = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy",
                                    Locale.ENGLISH).parse(time);
                            diffDays = printDifference(dateFb, currentTime)[0];
                            diffHours = printDifference(dateFb, currentTime)[1];
                            diffMins = printDifference(dateFb, currentTime)[2];
                            /*Log.e("HomeFragment", "dates" + currentTime + " " + dateFb +
                                    " " + diffMins+" "+diffHours );*/
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (diffMins <= thresholdTime && diffMins >= 0 && diffDays == 0
                                && diffHours <= 1) {
                            String type = grandChild.child("Type").getValue(String.class);

                            if (type != null && type.equalsIgnoreCase("AC")) {
                                String lat = grandChild.child("Latitude").getValue(String.class);
                                String lon = grandChild.child("Longitude").getValue(String.class);
                                Boolean chargingState = grandChild.child("ChargingState")
                                        .getValue(Boolean.class);

                                Log.e("HomeFragment", "record" + lat + " " + lon + " " + chargingState);
                                if (chargingState != null && chargingState && lat != null && lon != null) {
                                    // For dropping a marker at a point on the Map
                                    LatLng powerMarker = new LatLng(Double.parseDouble(lat),
                                            Double.parseDouble(lon));

                                    googleMap.addMarker(new MarkerOptions().position(powerMarker)
                                            .title("Power supply detected")
                                            .snippet("at " + time.substring(0, 16) + ""));
                                    Log.e("HomeFragment", "fab? " + thresholdTime + " diff " + diffMins + " " + time);
                                    Log.e("HomeFragment", "marked" + lat + " " + lon + " " + chargingState);
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Home Fragment", "error" + databaseError.toString());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("HomeFragment", "Permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("HomeFragment", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
            default:
                break;
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        //------------------------
        //Add code to be run here.
        //------------------------

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    // function to supply the difference in date time etc.
    private int[] printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        int[] diffs = {(int) elapsedDays, (int) elapsedHours, (int) elapsedMinutes};
        return diffs;
    }
}
