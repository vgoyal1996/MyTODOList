package com.dudeonfireandCO.vipul.mytodolist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.vipul.mytodolist.R;


public class EmptyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Grid view")){
            startActivity(new Intent(this,GridViewActivity.class));
        }
        else if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Condensed Listview")){
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Grid view")){
            startActivity(new Intent(this,GridViewActivity.class));
        }
        else if(preferences.getString(getString(R.string.pref_key_view),"condensed_listview").equals("Condensed Listview")){
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }
}
