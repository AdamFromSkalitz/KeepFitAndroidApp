package com.steppy.keepfit;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    SwitchPreference testMode;
    //http://stackoverflow.com/questions/39200846/how-to-add-toolbar-in-preferenceactivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.fragment, new MyPreferenceFragment()).commit();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                // finish();
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//
//        testMode = (SwitchPreference) findViewById(R.id.testMode);
//
//
//        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
//                SharedPreferences.OnSharedPreferenceChangeListener() {
//                    @Override
//                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                                          String key) {
//                        // your stuff here
//                    }
//                };
//    }
}
