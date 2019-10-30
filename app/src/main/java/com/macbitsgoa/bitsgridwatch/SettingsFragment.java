package com.macbitsgoa.bitsgridwatch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.macbitsgoa.bitsgridwatch.rankings.RankingsActivity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private TextView usernameTextView, username_field, allow_monitoring, allow_monitoring_display, theme_select, rankingButton, rankings_display,
                theme_display;
    private SharedPreferences.Editor editor;

    private ImageView allow_monitoring_image, rankings_image, theme_image, help_image, feedback_image, about_image;

    private String TAG = "Settings Fragment";

    //shared preferences for theme
    private SharedPreferences theme_shared_preferences;
    private SharedPreferences.Editor theme_editor;

    private Switch allowSwitch;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //-------------------------------
        //Initialize layout elements here
        //-------------------------------
        usernameTextView = view.findViewById(R.id.textview_username_settings);
        username_field = view.findViewById(R.id.textview_username);
        allow_monitoring = view.findViewById(R.id.allow_monitoring);
        theme_select = view.findViewById(R.id.theme_select);
        rankingButton = view.findViewById(R.id.rankings);
        Button signOutButton = view.findViewById(R.id.button_signout_settings);
        Button signInButton = view.findViewById(R.id.button_signin_settings);
        allowSwitch = view.findViewById(R.id.switch_monitor_settings);
        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("AllowMoniSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        allow_monitoring_image = view.findViewById(R.id.allow_monitoring_image);
        allow_monitoring_display = view.findViewById(R.id.allow_monitoring_display);
        rankings_image = view.findViewById(R.id.rankings_image);
        rankings_display = view.findViewById(R.id.rankings_display);
        theme_image = view.findViewById(R.id.theme_image);
        theme_display = view.findViewById(R.id.theme_display);
        help_image = view.findViewById(R.id.help_image);
        feedback_image = view.findViewById(R.id.feedback_image);
        about_image = view.findViewById(R.id.about_image);

        //shared preferences for theme
        theme_shared_preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeOptions", MODE_PRIVATE);
        theme_editor = theme_shared_preferences.edit();

        rankingButton.setOnClickListener(view16 -> {
            Intent rankIntent = new Intent(getContext(), RankingsActivity.class);
            getContext().startActivity(rankIntent);
        });

        signInButton.setOnClickListener(v -> {
            if (!((MainActivity) Objects.requireNonNull(getActivity())).getSignedInStatus()) {
                ((MainActivity) getActivity()).userSignIn();
                updateUser();
            } else
                Toast.makeText(getContext(), "Already Signed In!", Toast.LENGTH_SHORT).show();
        });
        signOutButton.setOnClickListener(v -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).userSignOut();
            usernameTextView.setText(R.string.guest);
        });

        allowSwitch.setOnClickListener(v -> {
            Log.d(TAG, "entered onClick allowSwitch");
            boolean switchState = allowSwitch.isChecked();
            //editor.clear();

            if (switchState) {
                Log.d(TAG, "allowSwitch = TRUE");
                //Display dialog box to check if verified.
                //Start background work only if user accepts.
                //Reset to false state if user declines.
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(true);
                ((MainActivity) Objects.requireNonNull(getActivity())).startBackgroundWork();
                updateAllowSwitchState();
                allow_monitoring_display.setText("Monitoring enabled");
                //launchAllowMonitoringDialog();
            } else {
                updateAllowSwitchState();
                Log.d(TAG, "allowSwitch = FALSE");
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(false);
                ((MainActivity) Objects.requireNonNull(getActivity())).cancelBackgroundWork();
                updateAllowSwitchState();
                allow_monitoring_display.setText("Monitoring disabled");
            }
            //Log.d(TAG, "Updating switch state");
        });

        boolean switchState = sharedPreferences.getBoolean("allow", true);
        ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(switchState);
        allowSwitch.setChecked(switchState);


        //theme option

        CharSequence[] app_themes = {"Light", "Dark", "Set by Batter Saver"};

        int theme = theme_shared_preferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);

        if (theme == AppCompatDelegate.MODE_NIGHT_NO)
            theme_display.setText("Light");
        else if (theme == AppCompatDelegate.MODE_NIGHT_YES)
            theme_display.setText("Dark");
        else if (theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            theme_display.setText("Set by Battery Saver");

        theme_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AlertDialog alertDialog = null;


                int checked_item = 0;

                if (theme_shared_preferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO) - 1 >= 0)
                    checked_item = theme_shared_preferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO) - 1;

                AlertDialog alertDialog = null;

                LinearLayout alert_dialog_layout = new LinearLayout(getContext());
                alert_dialog_layout.setOrientation(LinearLayout.VERTICAL);
                alert_dialog_layout.setPadding(20, 20, 20, 20);

                TextView alert_dialog_title = new TextView(alert_dialog_layout.getContext());
                alert_dialog_title.setText("Choose theme");
                alert_dialog_title.setTextSize(25);
                alert_dialog_title.setPadding(40, 20, 20, 20);

                Typeface typeface_medium = getResources().getFont(R.font.montserrat_medium);
                alert_dialog_title.setTypeface(typeface_medium);

                RadioGroup radioGroup = new RadioGroup(alert_dialog_layout.getContext());
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                radioGroup.setPadding(20, 20, 20, 40);

                RadioButton radio_button_light = new RadioButton(alert_dialog_layout.getContext());
                RadioButton radio_button_dark = new RadioButton(alert_dialog_layout.getContext());
                RadioButton radio_button_set_by_battery_saver = new RadioButton(alert_dialog_layout.getContext());

                radio_button_light.setText("Light");
                radio_button_dark.setText("Dark");
                radio_button_set_by_battery_saver.setText("Set by Battery Saver");

                radio_button_light.setTextSize(17);
                radio_button_dark.setTextSize(17);
                radio_button_set_by_battery_saver.setTextSize(17);

                Typeface typeface_regular = getResources().getFont(R.font.montserrat_regular);
                radio_button_light.setTypeface(typeface_regular);
                radio_button_dark.setTypeface(typeface_regular);
                radio_button_set_by_battery_saver.setTypeface(typeface_regular);

                radio_button_light.setPadding(20, 20, 20, 20);
                radio_button_dark.setPadding(20, 20, 20, 20);
                radio_button_set_by_battery_saver.setPadding(20, 20, 20, 20);

                radioGroup.addView(radio_button_light);
                radioGroup.addView(radio_button_dark);
                radioGroup.addView(radio_button_set_by_battery_saver);

                if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
                    alert_dialog_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    alert_dialog_title.setTextColor(getResources().getColor(R.color.black));
                } else if (theme == AppCompatDelegate.MODE_NIGHT_YES) {
                    alert_dialog_layout.setBackgroundColor(getResources().getColor(R.color.tv_back));
                    alert_dialog_title.setTextColor(getResources().getColor(R.color.white));
                } else {
                    PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                    if (powerManager.isPowerSaveMode()) {
                        alert_dialog_layout.setBackgroundColor(getResources().getColor(R.color.tv_back));
                        alert_dialog_title.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        alert_dialog_title.setTextColor(getResources().getColor(R.color.black));
                    }
                }

                if (checked_item == 0)
                    radio_button_light.setChecked(true);
                else if (checked_item == 1)
                    radio_button_dark.setChecked(true);
                else
                    radio_button_set_by_battery_saver.setChecked(true);


                alert_dialog_layout.addView(alert_dialog_title);
                alert_dialog_layout.addView(radioGroup);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(alert_dialog_layout);
                alertDialog = builder.create();
                alertDialog.show();

                AlertDialog finalAlertDialog = alertDialog;

                radio_button_light.setOnClickListener(view17 -> {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
                    theme_editor.apply();
                    finalAlertDialog.dismiss();
                    theme_display.setText("Light");

                });

                radio_button_dark.setOnClickListener(view1 -> {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES);
                    theme_editor.apply();
                    finalAlertDialog.dismiss();
                    theme_display.setText("Dark");

                });


                radio_button_set_by_battery_saver.setOnClickListener(view12 -> {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    theme_editor.apply();
                    finalAlertDialog.dismiss();
                    theme_display.setText("Set by Batter Saver");

                });


//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                //set font
//                Typeface typeface_regular = getResources().getFont(R.font.montserrat_medium);
//                TextView alert_dialog_title = new TextView(getContext());
//                alert_dialog_title.setPadding(50,20,10,10);
//                alert_dialog_title.setTextSize(22);
//                alert_dialog_title.setTextColor(getResources().getColor(R.color.black));
//                alert_dialog_title.setText("Choose theme");
//                alert_dialog_title.setTypeface(typeface_regular);
//                builder.setCustomTitle(alert_dialog_title);
////                builder.setTitle("Choose theme");
//                builder.setSingleChoiceItems(app_themes, checked_item, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int item) {
//
//                        switch (item) {
//                            case 0:
//
////                                Toast.makeText(getContext(), "Light Theme Selected", Toast.LENGTH_LONG).show();
//                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                                theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
//                                theme_editor.apply();
//                                break;
//                            case 1:
//
////                                Toast.makeText(getContext(), "Dark Theme Selected", Toast.LENGTH_LONG).show();
//                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                                theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES);
//                                theme_editor.apply();
//                                break;
//                            case 2:
//
////                                Toast.makeText(getContext(), "Theme Set by Battery Saver Selected", Toast.LENGTH_LONG).show();
//                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
//                                theme_editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
//                                theme_editor.apply();
//                                break;
//                        }
//                        dialog.dismiss();
//                    }
//                });
//                alertDialog = builder.create();
//                alertDialog.show();


            }
        });


        //Help option

        TextView help_option = view.findViewById(R.id.help_option);

        help_option.setOnClickListener(view13 -> {

            Intent mainIntent = new Intent(getContext(), OnboardingActivity.class);
            getContext().startActivity(mainIntent);
        });

        TextView about_option = view.findViewById(R.id.about_option);

        about_option.setOnClickListener(view14 -> {

            Intent mainIntent = new Intent(getContext(), AboutActivity.class);
            getContext().startActivity(mainIntent);
        });

        TextView feedback_option = view.findViewById(R.id.feedback_option);

        feedback_option.setOnClickListener(view15 -> {

            Intent intent = new Intent(getContext(), FeedbackActivity.class);
            getContext().startActivity(intent);
        });

        if (allowSwitch.isChecked())
            allow_monitoring_display.setText("Monitoring enabled");
        else
            allow_monitoring_display.setText("Monitoring disabled");

        allow_monitoring.setOnClickListener(view18 -> allowSwitch.performClick());
        allow_monitoring_image.setOnClickListener(view19 -> allowSwitch.performClick());
        allow_monitoring_display.setOnClickListener(view110 -> allowSwitch.performClick());

        rankings_image.setOnClickListener(view111 -> rankingButton.performClick());
        rankings_display.setOnClickListener(view112 -> rankingButton.performClick());

        theme_image.setOnClickListener(view113 -> theme_select.performClick());
        theme_display.setOnClickListener(view114 -> theme_select.performClick());

        help_image.setOnClickListener(view115 -> help_option.performClick());

        feedback_image.setOnClickListener(view116 -> feedback_option.performClick());

        about_image.setOnClickListener(view117 -> about_option.performClick());


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

    static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private void updateUser() {
        GoogleSignInAccount userAccount = ((MainActivity) getActivity()).getUserAccount();
        if (userAccount != null)
            usernameTextView.setText(userAccount.getDisplayName());
        else
            usernameTextView.setText(R.string.guest);
    }

    private void updateAllowSwitchState() {
        Boolean switchState = ((MainActivity) Objects.requireNonNull(getActivity())).getAllowMonitoring();
        allowSwitch.setChecked(switchState);
        editor.putBoolean("allow", switchState);
        editor.commit();
    }

    /*private void launchAllowMonitoringDialog() {
        //Display dialog box to check if verified.
        //Start background work only if user accepts.
        //Reset to false state if user declines.

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(R.string.disclaimer);
        alertDialogBuilder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(true);
                ((MainActivity) Objects.requireNonNull(getActivity())).startBackgroundWork();
                updateAllowSwitchState();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(false);
                ((MainActivity) Objects.requireNonNull(getActivity())).cancelBackgroundWork();
                updateAllowSwitchState();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/
}