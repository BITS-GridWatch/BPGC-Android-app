package com.macbitsgoa.bitsgridwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        //-------------------------------
        //Initialize layout elements here
        //-------------------------------

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static SettingsFragment newInstance(){
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
}