package com.example.vipul.mytodolist;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsFragment fragment = new SettingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.settings_container,fragment).commit();
    }

}
