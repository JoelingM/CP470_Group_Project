package com.group.project.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.group.project.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A fragment to show settings
 *
 * @author Aaron Exley
 */
public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;

    public static final String ALLOW_PUSH_NOTIFICATIONS = "allowPushNotifications";
    public static final String NOTIFICATION_TIME_POS = "notTimePos";
    public static final String NOTIFICATION_TIME = "notTime";
    public static final String PREFS_NAME = "CalenderPrefs";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch pushNotificatoins = root.findViewById(R.id.allow_notifications);
        Spinner notTime = root.findViewById(R.id.notification_time);

        if (getActivity() != null) {
            // Set the ui elements to match the saved settings
            sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            pushNotificatoins.setChecked(sharedPreferences.getBoolean(ALLOW_PUSH_NOTIFICATIONS, true));
            notTime.setSelection(sharedPreferences.getInt(NOTIFICATION_TIME_POS, 0));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ALLOW_PUSH_NOTIFICATIONS, pushNotificatoins.isChecked());
            editor.putInt(NOTIFICATION_TIME_POS, notTime.getSelectedItemPosition());
            editor.putInt(NOTIFICATION_TIME, Integer.parseInt(notTime.getSelectedItem().toString()));
            editor.apply();
        }

        pushNotificatoins.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ALLOW_PUSH_NOTIFICATIONS, pushNotificatoins.isChecked());
            editor.apply();
        });

        notTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(NOTIFICATION_TIME_POS, i);
                editor.putInt(NOTIFICATION_TIME, Integer.parseInt(notTime.getSelectedItem().toString()));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        return root;
    }
}