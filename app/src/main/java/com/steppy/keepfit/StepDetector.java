package com.steppy.keepfit;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Turkleton's on 05/03/2017.
 */

public class StepDetector extends Service implements SensorEventListener {
    private  SensorManager sm;
    private DBHelper dbHelper;
    private int stepCount;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper=new DBHelper(getApplicationContext());

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sm.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Toast.makeText(StepDetector.this,"stepDetected",Toast.LENGTH_SHORT).show();

        float stepNew = event.values[0];
        stepCount+=stepNew;

        Cursor existingSteps = dbHelper.getDayProgress();
        existingSteps.moveToFirst();

        float stepsOld= existingSteps.getFloat(existingSteps.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
        existingSteps.close();
        float updateStep = stepsOld+stepCount;
        try {
           dbHelper.updateDayProgress(1);
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


}
