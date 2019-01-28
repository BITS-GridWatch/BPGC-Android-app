package com.macbitsgoa.bitsgridwatch;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameLayout;
    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = (BottomNavigationView) findViewById(R.id.bottomnav_activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout_activity_main);

        bottomNav.setSelectedItemId(R.id.item_bottomnav_home);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_bottomnav_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.item_bottomnav_settings:
                        selectedFragment = SettingsFragment.newInstance();
                        break;
                    default:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                }
                FragmentTransaction transacion = getSupportFragmentManager().beginTransaction();
                transacion.replace(R.id.framelayout_activity_main, selectedFragment);
                transacion.commit();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_activity_main, HomeFragment.newInstance());
        transaction.commit();
    }
}
