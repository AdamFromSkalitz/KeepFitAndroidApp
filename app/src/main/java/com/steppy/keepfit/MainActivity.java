package com.steppy.keepfit;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private BottomNavigationView bottomBar;
    private MainActivity mai;
    Toolbar toolbar;
    View dateButtonMain;
    ArrayList<Goal> ItemGoalList = new ArrayList<Goal>();

    private float stepCount;
    DBHelper dbHelper;

    private PendingIntent pendingIntent;
    private PendingIntent mainPendingIntent;
    private AlarmManager manager;

    public Context getContext() {
        return mai;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getIntent().hasExtra("openPrevFrag")) {
            getFragmentManager().popBackStack();
        }
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        startAlarm();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(MainActivity.this);


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");

        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.progressMiddle, mainFragment);
        fragmentTransaction.commit();


        bottomBar = (BottomNavigationView) findViewById(R.id.bottomBar);
        bottomBar.setId(R.id.tab_main);
        bottomBar.getMenu().findItem(R.id.tab_main).setChecked(true);

        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentManager fragmentManager;// = getFragmentManager();
                        FragmentTransaction fragmentTransaction;// = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.tab_goals:
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                toolbar.removeView(dateButtonMain);
                                ViewGoalsFragment goalsFragment = new ViewGoalsFragment();
                                fragmentTransaction.replace(R.id.progressMiddle, goalsFragment);
                                fragmentTransaction.commit();
                                break;
                            case R.id.tab_graph:
                                toolbar.removeView(dateButtonMain);
                                GraphFragment graphFragment = new GraphFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.progressMiddle, graphFragment);
                                fragmentTransaction.commit();
                                break;
                            case R.id.tab_main:
                                try {
                                    toolbar.addView(dateButtonMain);
                                } catch (Exception e) {

                                }
                                MainFragment mainFragment = new MainFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.progressMiddle, mainFragment);
                                fragmentTransaction.commit();
                                break;
                        }
                        return true;
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
        } else if (id == R.id.deleteHistory) {
            Intent intent = new Intent(MainActivity.this, DeleteHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.statistics) {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }



    public void startAlarm() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.AM_PM, Calendar.AM);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //Toast.makeText(this, "Alarm Set for" + c.get(Calendar.HOUR_OF_DAY), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float stepNew = event.values[0];

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);

        Cursor existingSteps = dbHelper.getDayProgress();
        existingSteps.moveToFirst();

        //need to see if notifications is off
        float stepsOld = 0;
        try {
            stepsOld = existingSteps.getFloat(existingSteps.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        existingSteps.close();

        float updateStep = stepsOld + stepNew;

        Cursor res = dbHelper.getActiveGoal();
        res.moveToFirst();
        String units ="Steps";
        try{
            units = res.getString(res.getColumnIndex(DBHelper.COLUMN_UNITS));
        }catch (Exception e){
            e.printStackTrace();
        }
        float unitsStep = convertToUnits(units,updateStep);
        boolean counter = SP.getBoolean("enableCounter", false);

        TextView progressTV = (TextView) findViewById(R.id.tvprogress);
        PieChart chart = (PieChart) findViewById(R.id.chartPie);
        try {
            if (counter) {
                progressTV.setText(""+unitsStep);
                chart.invalidate();
                dbHelper.updateDayProgress((int) stepNew);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        int goalValue = 666;
        try {
            goalValue = res.getInt(res.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
        } catch (Exception e) {

        }


        boolean notify = SP.getBoolean("enableNotify", false);
        if (notify) {
            if ( updateStep>=goalValue && updateStep <= (goalValue+5)) {

                Intent mainIntent = new Intent(this, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainIntent);
                 mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Cursor result=null;

                try {
                    result = dbHelper.getActiveGoal();
                    result.moveToFirst();
                    units = result.getString(result.getColumnIndex(DBHelper.COLUMN_UNITS));
                }catch (Exception e){
                    e.printStackTrace();
                }


                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.step)
                                .setContentTitle("Goal Reached!")
                                .setContentText("You have walked " + updateStep + " " + units + ", Well done.");
                mBuilder.setContentIntent(mainPendingIntent);
                int mNotId = 1;
                mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;

                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotId, mBuilder.build());
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // mSensorManager.unregisterListener(this);
    }

    public float convertToUnits(String unitsSpinString,float progress){

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        float cmMap = Float.parseFloat(SP.getString("mappingMet","75"));
        float inchMap = Float.parseFloat(SP.getString("mappingImp","30"));
        switch (unitsSpinString){
            case "Kilometres":
                float progressStepsCM = progress*cmMap;
                progress = progressStepsCM/100000;
                break;
            case "Metres":
                float cmMetres = progress*cmMap;
                progress=cmMetres/100;
                break;
            case "Miles":
                float progressStepsINC = progress*inchMap;
                progress = progressStepsINC/(36*1760);
                break;
            case "Yards":
                float inchesYards = progress*inchMap;
                progress=inchesYards/36;
                break;
        }
        return progress;
    }


}


