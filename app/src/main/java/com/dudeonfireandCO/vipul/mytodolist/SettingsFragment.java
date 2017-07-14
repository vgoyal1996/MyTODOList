package com.dudeonfireandCO.vipul.mytodolist;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.vipul.mytodolist.R;

import java.util.Calendar;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences preferences;
    private String chosenRingtone;
    private String defaultChosenRingtone=null;
    private Preference pref;
    private ListPreference viewPreference;
    private final String[] daysList = {"second(s)","minute(s)","hour(s)","day(s)","week(s)","month(s)","year(s)"};
    private int countDay=1;
    private String rep;

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.prefs);
        PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.prefs, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        viewPreference = (ListPreference)findPreference(getString(R.string.pref_key_view));
        if(preferences.getBoolean(getString(R.string.pref_key_vibrate),true)){
            pref = findPreference(getString(R.string.pref_key_vibrate));
            pref.setSummary("enabled");
        }
        else{
            pref = findPreference(getString(R.string.pref_key_vibrate));
            pref.setSummary("disabled");
        }
        pref = findPreference(getString(R.string.pref_key_ringtone));
        String uri = preferences.getString(getString(R.string.pref_key_ringtone),defaultChosenRingtone);
        if(uri==null) {
            pref.setSummary("None");
        }
        else{
            Uri ringtone = Uri.parse(uri);
            Ringtone ringtone1 = RingtoneManager.getRingtone(getActivity().getApplicationContext(),ringtone);
            chosenRingtone = ringtone1.getTitle(getActivity().getApplicationContext());
            pref.setSummary(chosenRingtone);
        }
        if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Condensed Listview")){
            viewPreference.setSummary("Condensed Listview");
        }
        else if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Grid view")){
            viewPreference.setSummary("Grid View");
        }
        pref = findPreference(getString(R.string.pref_key_quiet_hours_vibrate_and_sound));
        if(preferences.getBoolean(getString(R.string.pref_key_quiet_hours_vibrate_and_sound),true)){
            pref.setSummary("enabled");
        }
        else{
            pref.setSummary("disabled");
        }
        viewPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                viewPreference.setValue(newValue.toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.pref_key_view),viewPreference.getEntry().toString()).apply();
                viewPreference.setSummary(viewPreference.getEntry().toString());
                return true;
            }
        });
        String value = preferences.getString(getString(R.string.pref_key_snooze),""+10+" "+"minute(s)");
        final Preference snoozePref = findPreference(getString(R.string.pref_key_snooze));
        snoozePref.setSummary(value);
        snoozePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_snooze);
                dialog.setTitle("Change Snooze Time");
                dialog.show();
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel_pref_button);
                Button okButton = (Button) dialog.findViewById(R.id.ok_pref_button);
                Button countUpButton = (Button) dialog.findViewById(R.id.countup_pref_button);
                Button countDownButton = (Button) dialog.findViewById(R.id.countdown_pref_button);
                final EditText countEditText = (EditText) dialog.findViewById(R.id.count_pref_text);
                Spinner daylist = (Spinner) dialog.findViewById(R.id.daylist_pref);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, daysList);
                daylist.setAdapter(adapter);
                countUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDay++;
                        countEditText.setText("" + countDay);
                    }
                });
                countDownButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (countDay > 1) {
                            countDay--;
                            countEditText.setText("" + countDay);
                        }
                    }
                });
                daylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        rep = daysList[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.pref_key_snooze), "" + countDay + " " + rep).apply();
                        snoozePref.setSummary("" + countDay + " " + rep);

                        dialog.dismiss();
                    }
                });
                return true;
            }
        });

        pref = findPreference(getString(R.string.pref_key_quiet_hours_vibrate_and_sound));
        if(!preferences.getBoolean(getString(R.string.pref_key_quiet_hours_vibrate_and_sound),true)){
            pref.setSummary("Silent notifications disabled");
            Preference startPref = findPreference(getString(R.string.pref_key_quiet_hours_start));
            startPref.setEnabled(false);
            startPref = findPreference(getString(R.string.pref_key_quiet_hours_end));
            startPref.setEnabled(false);
        }
        else{
            pref.setSummary("Silent notifications enabled");
            Preference startPref = findPreference(getString(R.string.pref_key_quiet_hours_start));
            startPref.setEnabled(true);
            startPref = findPreference(getString(R.string.pref_key_quiet_hours_end));
            startPref.setEnabled(true);
        }

        value =  preferences.getString(getString(R.string.pref_key_quiet_hours_start),"12:00");
        final Preference startPref = findPreference(getString(R.string.pref_key_quiet_hours_start));
        startPref.setSummary(value);

        value = preferences.getString(getString(R.string.pref_key_quiet_hours_end),"15:00");
        final Preference endPref = findPreference(getString(R.string.pref_key_quiet_hours_end));
        endPref.setSummary(value);

        final Preference quietPref = findPreference(getString(R.string.pref_key_quiet_hours_vibrate_and_sound));
        quietPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(getString(R.string.pref_key_quiet_hours_vibrate_and_sound), (boolean) newValue).apply();
                if ((boolean) newValue) {
                    quietPref.setSummary("Silent notifications enabled");
                    startPref.setEnabled(true);
                    endPref.setEnabled(true);
                }
                else {
                    quietPref.setSummary("Silent notifications disabled");
                    startPref.setEnabled(false);
                    endPref.setEnabled(false);
                }
                return true;
            }
        });


        startPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.pref_key_quiet_hours_start), "" + hourOfDay + ":" + minute).apply();
                        startPref.setSummary("" + hourOfDay + ":" + minute);
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
                return true;
            }
        });

        endPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.pref_key_quiet_hours_end),""+hourOfDay+":"+minute).apply();
                        endPref.setSummary(""+hourOfDay+":"+minute);
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
                return true;
            }
        });


        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_key_ringtone))){
            pref = findPreference(getString(R.string.pref_key_ringtone));
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            getActivity().startActivityForResult(intent, 100);
            //pref.setEnabled(true);
        }
        if(key.equals(getString(R.string.pref_key_vibrate))){
            pref = findPreference(getString(R.string.pref_key_vibrate));
            if(preferences.getBoolean(key,true)){
                Vibrator vibrator = (Vibrator)getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(key,true).apply();
                pref.setSummary("enabled");
            }
            else{
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(key,false).apply();
                pref.setSummary("disabled");
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri!=null) {
                Uri ringtone = Uri.parse(uri.toString());
                Ringtone ringtone1 = RingtoneManager.getRingtone(getActivity().getApplicationContext(),ringtone);
                chosenRingtone = ringtone1.getTitle(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.pref_key_ringtone),uri.toString());
                editor.apply();
            }
            else{
                chosenRingtone="None";
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.pref_key_ringtone),null);
                editor.apply();
            }
        }
        pref = findPreference(getString(R.string.pref_key_ringtone));
        pref.setSummary(chosenRingtone);
    }



   @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }



}



