package com.macbitsgoa.bitsgridwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView usernameTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        //-------------------------------
        //Initialize layout elements here
        //-------------------------------
        usernameTextView = view.findViewById(R.id.textview_username_settings);
        Button signOutButton = view.findViewById(R.id.button_signout_settings);
        Button signInButton = view.findViewById(R.id.button_signin_settings);




        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((MainActivity)getActivity()).getSignedInStatus()){
                    ((MainActivity)getActivity()).userSignIn();
                    updateUser();
                }
                else
                    Toast.makeText(getContext(),"Already Signed In!",Toast.LENGTH_SHORT).show();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).userSignOut();
                usernameTextView.setText(R.string.guest);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Get last signed in user
        updateUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUser();
    }

    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }

    private void updateUser(){
        GoogleSignInAccount userAccount = ((MainActivity) getActivity()).getUserAccount();
        if(userAccount !=null)
            usernameTextView.setText(userAccount.getDisplayName());
        else
            usernameTextView.setText(R.string.guest);
    }
}