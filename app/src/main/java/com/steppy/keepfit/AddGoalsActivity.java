package com.steppy.keepfit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddGoalsActivity extends Activity {
    private int mYear, mMonth, mDay = 0;
    private DBHelper dbHelper;
    private boolean testMode;
    private ArrayList<Goal> ItemGoalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goals);

        //ItemGoalList = (ArrayList<Goal>) getIntent().getSerializableExtra("list");

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        testMode = SP.getBoolean("enableTest", false);

        if(testMode){
            EditText steps = (EditText) findViewById(R.id.stepsText); //new EditText(this);
            steps.setVisibility(View.VISIBLE);
            steps.setHint("Steps Taken");

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            final Button dateBut = (Button) findViewById(R.id.addDateBut);//new Button(this);
            dateBut.setVisibility(View.VISIBLE);
            dateBut.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    DatePickerDialog dpd = new DatePickerDialog(AddGoalsActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    mYear=year;
                                    mMonth=monthOfYear;
                                    mDay = dayOfMonth;
                                    dateBut.setText(mDay+"/"+(mMonth+1+"/"+mYear));
                                }
                            }, mYear, mMonth, mDay);
                    dpd.show();
                }
            });
        }

        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText goalValue = (EditText) findViewById(R.id.goalsText);
                Spinner unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);
                EditText name = (EditText) findViewById(R.id.nameText);

                String nameString = (name.getText().toString()).trim();
                String goalString = goalValue.getText().toString().trim();
                String units = unitsSpin.getSelectedItem().toString();

                if((nameString.equals("")) || (nameString.equals("0"))){
                    name.setError("Field cannot be empty nor just 0");
                    return;
                }
                try{
                    float goalValueFloat = Float.parseFloat(goalString);
                }catch (Exception e){
                    goalValue.setError("Field cannot be blank nor contain special characters");
                    return;
                }

                if(testMode){
                    dbHelper = new DBHelper(AddGoalsActivity.this);
                    EditText stepsValue = (EditText) findViewById(R.id.stepsText);
                    String stepsString = stepsValue.getText().toString();
                    float stepsStringFloat=0f;
                    try {
                        stepsStringFloat = Float.parseFloat(stepsString);
                    }catch (Exception e){
                        stepsValue.setError("Field cannot be blank nor contain special characters");
                        return;
                    }

                    Date date = new GregorianCalendar(mYear,mMonth,mDay).getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString1 = sdf.format(date);

                    float i =Float.parseFloat(goalString);
                    float goalValueInt = turnIntoSteps(i);
                    float stepsProgressInt = turnIntoSteps(stepsStringFloat);

                    float percent = ((float)stepsProgressInt/(float)goalValueInt) *100;
                    int percentInt = (int) percent;
                    String percentString = Integer.toString(percentInt);

                    //Toast.makeText(AddGoalsActivity.this,"OLDGOAL:"+goalValueInt+" "+stepsProgressInt,Toast.LENGTH_SHORT).show();

                    if(dbHelper.insertOldGoal(nameString,(int)goalValueInt,(int)stepsProgressInt,percentString,dateString1,units)){
                        Toast.makeText(AddGoalsActivity.this, "Test Goal Added Successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddGoalsActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                    dbHelper.close();

                }else {
                    dbHelper = new DBHelper(AddGoalsActivity.this);
                    Cursor allGoals = dbHelper.getAllGoals();
                    allGoals.moveToFirst();
                    while(!allGoals.isAfterLast()) {
                        String nameCheck = allGoals.getString(allGoals.getColumnIndex(DBHelper.COLUMN_NAME));
                        if(nameString.equals(nameCheck)){
                            name.setError("Goal cannot have the same name as another goal");
                            return;
                        }
                        allGoals.moveToNext();
                    }
                    allGoals.close();

                    float stepsGoal = turnIntoSteps(Float.parseFloat(goalString));
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = sdf.format(date);
//                    try {
//                        //ItemGoalList.add(new Goal(nameString, (float) stepsGoal, false, units));
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
                    if (dbHelper.insertGoal(nameString,(int)stepsGoal, "false", dateString,units)) {
                        Toast.makeText(AddGoalsActivity.this, "Goal Added Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddGoalsActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                    dbHelper.close();
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void backToPrevFrag(){
        Intent fragBack = new Intent(this, MainActivity.class);
        fragBack.putExtra("openPrevFrag",true);
        startActivity(fragBack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public float turnIntoSteps(float goalInSomeUnits){

        Spinner unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);
        String units = unitsSpin.getSelectedItem().toString();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(AddGoalsActivity.this);

        float stepsInt = goalInSomeUnits;
        float goalFloat = (float) goalInSomeUnits;
        float steps=0;
        float inches=0;
        float cms=0;
        float inch=0;
        float cm=0;
        switch (units){
            case "Metres":
//              // 1 meters = 100cm
                cms = goalFloat*100;
                cm = Float.parseFloat(SP.getString("mappingMet","75"));
                steps = cms/cm;
                stepsInt = (int) steps;
                break;
            case "Kilometres":
                // 1 kilometer = 1000 metres
                float m = goalFloat*1000;
                // 1 meters = 100cm
                cms = m*100;
                cm = Float.parseFloat(SP.getString("mappingMet","75"));
                steps = cms/cm;
                stepsInt = (int) steps;
                break;
            case "Yards":
                //1 yard = 36 inches
                inches = goalFloat*36;
                inch = Float.parseFloat(SP.getString("mappingImp","30"));
                steps = inches/inch;
                stepsInt = (int) steps;
                break;
            case "Miles":
                //1 mile = 1760 yards
                float yards = goalFloat*1760;
                //1 yard = 36 inches
                inches = yards*36;
                inch = Float.parseFloat(SP.getString("mappingImp","30"));
                steps = inches/inch;
                // steps in inches atm
                stepsInt = (int) steps;
                break;
        }
        return stepsInt;
    }
}
