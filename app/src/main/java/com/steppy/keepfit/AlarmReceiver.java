package com.steppy.keepfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Turkleton's on 12/02/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    DBHelper dbHelper;
    int i=0;
    @Override
    public void onReceive(Context context, Intent intent) {

        dbHelper = new DBHelper(context);

        Cursor resGoal = dbHelper.getActiveGoal();
        resGoal.moveToFirst();
        String goalName="";
        float goalValue=0f;
        String goalDate="";
        String goalUnits="";
        try {
            goalName= resGoal.getString(resGoal.getColumnIndex(DBHelper.COLUMN_NAME));
            goalValue = resGoal.getFloat(resGoal.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
            goalDate = resGoal.getString(resGoal.getColumnIndex(DBHelper.COLUMN_DATE));
            goalUnits = resGoal.getString(resGoal.getColumnIndex(DBHelper.COLUMN_UNITS));
        }catch (Exception e){
            e.printStackTrace();
        }
        resGoal.close();

        Cursor resProgress = dbHelper.getDayProgress();
        resProgress.moveToFirst();
        String goalProgressString="";
        float goalProgress=0;
        float percentage=0;
        try {
            goalProgressString = resProgress.getString(resProgress.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
            goalProgress= Integer.parseInt(goalProgressString);
            percentage = ((float)goalProgress/goalValue)*100;
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!goalName.equals("")) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            Date date = new GregorianCalendar(mYear,mMonth,mDay-1).getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(date);

            dbHelper.insertOldGoal(goalName, (int) goalValue, (int)goalProgress, Float.toString(percentage), goalDate, goalUnits);
            dbHelper.resetDayProgress();
        }
        resProgress.close();
        dbHelper.close();

        // For our recurring task, we'll just display a message
        //Toast.makeText(context, "I'm running"+i++, Toast.LENGTH_SHORT).show();

    }
}