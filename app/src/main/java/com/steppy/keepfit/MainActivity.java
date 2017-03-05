package com.steppy.keepfit;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
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
    private PendingIntent pendingIntent;

    private float stepCount;
    DBHelper dbHelper;

    public Context getContext() {
        return mai;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getIntent().hasExtra("openPrevFrag")){
            getFragmentManager().popBackStack();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper=new DBHelper(MainActivity.this);

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
        //bottomBar.setDefaultTab(R.id.tab_main);
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentManager fragmentManager;// = getFragmentManager();
                        FragmentTransaction fragmentTransaction;// = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            //if (tabId == R.id.tab_goals) {
                            case R.id.tab_goals:
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                toolbar.removeView(dateButtonMain);
                                ViewGoalsFragment goalsFragment = new ViewGoalsFragment();

                                fragmentTransaction.replace(R.id.progressMiddle, goalsFragment);
                                //fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                                //viewGoals();
                                //}else if (tabId == R.id.tab_graph) {
                            case R.id.tab_graph:
                                toolbar.removeView(dateButtonMain);
                                GraphFragment graphFragment = new GraphFragment();
                                fragmentManager = getFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.progressMiddle, graphFragment);
                                //fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                //} else if (tabId == R.id.tab_main) {
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
                                //fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                        }
                        return true;
                    }
                });

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        startAt10();
    }
//    public static class MainFragment extends Fragment {
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            final View mainView = inflater.inflate(R.layout.fragment_main, container, false);
//            FloatingActionButton myFab = (FloatingActionButton) mainView.findViewById(R.id.fab);
//            myFab.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this,"main fab press",Toast.LENGTH_LONG).show();
//                }
//            });
//            return inflater.inflate(R.layout.fragment_main, container, false);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
        }else if(id == R.id.deleteHistory){
            Intent intent = new Intent(MainActivity.this, DeleteHistoryActivity.class);
            startActivity(intent);
        }else if(id == R.id.statistics){
            Intent intent = new Intent(MainActivity.this,StatisticsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

//    public void start() {
//        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        int interval = 8000;
//
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
//        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
//    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void startAt10() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 24);

        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        Toast.makeText(this, mHour+":"+mMin,Toast.LENGTH_SHORT).show();

        /* Repeating on every 20 minutes interval */
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float stepNew = event.values[0];
        stepCount+=stepNew;
       // Toast.makeText(this,"steps"+stepCount,Toast.LENGTH_SHORT).show();
//        if (steps%100==0){
//            save to db
//        }

        Cursor existingSteps = dbHelper.getDayProgress();
        existingSteps.moveToFirst();

        float stepsOld= existingSteps.getFloat(existingSteps.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
        existingSteps.close();
        float updateStep = stepsOld+stepCount;
        TextView progressTV = (TextView) findViewById(R.id.tvprogress);
        try {
            progressTV.setText("" + updateStep);
            dbHelper.updateDayProgress((int)stepNew);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(updateStep==140){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.step)
                            .setContentTitle("Goal Reached!")
                            .setContentText("You have walked "+updateStep+" steps, Well done.");
            int mNotId=001;
            NotificationManager mNotifyMgr =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotId,mBuilder.build());
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
    }


