package com.steppy.keepfit;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Target;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.text.DateFormatSymbols;

public class StatisticsActivity extends AppCompatActivity {
    private String startDate;
    private String endDate;

    private int endYear;
    private int endMonth;
    private int endDay;

    private int startYear;
    private int startMonth;
    private int startDay;

    private Spinner unitsSpin;
    private DBHelper dbHelper;
    private TextView tvAvgV;
    private TextView tvTotV;
    private TextView tvMaxV;
    private TextView tvMinV;
    private TextView tvAvgP;
    private TextView tvTotP;
    private TextView tvMaxP;
    private TextView tvMinP;
    private SeekBar sbTop;
    private SeekBar sbBot;
    private TextView tvGoalTitleNum;
    private TextView tvstop;
    private TextView tvsbot;

    private String unitsSpinString="";

    //private float total=0f;
    //private float average=0f;
    //private float max=Float.MIN_VALUE;
    //private float min=Float.MAX_VALUE;

    private int minUpperBound=0;
    private int maxLowerBound=100;
     private ArrayList<Goal> goals = new ArrayList<>();
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Statistics");
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

        putWarning();
        dbHelper = new DBHelper(StatisticsActivity.this);

        tvsbot = (TextView) findViewById(R.id.seektvbot);
        tvstop = (TextView) findViewById(R.id.seektvtop);
        unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);
        sbTop = (SeekBar) findViewById(R.id.seekBarTop);
        sbBot = (SeekBar) findViewById(R.id.seekBarBot);
        setDefaults();

        final Button butStartDate = (Button) findViewById(R.id.buttonStartDate);
        butStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(24)
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(StatisticsActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startDay=dayOfMonth;
                                startMonth=monthOfYear;
                                startYear=year;
                                //Toast.makeText(StatisticsActivity.this,dateString1,Toast.LENGTH_SHORT).show();
                                butStartDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, startYear, startMonth, startDay);
                dpd.show();
            }
        });
        final Button butEndDate = (Button) findViewById(R.id.buttonEndDate);
        butEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(24)
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(StatisticsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endDay=dayOfMonth;
                                endMonth=monthOfYear;
                                endYear=year;
                               //Toast.makeText(StatisticsActivity.this,dateString1,Toast.LENGTH_SHORT).show();
                                butEndDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, endYear, endMonth, endDay  );
                dpd.show();
            }
        });



//        gridAdapter = new GridAdapter(this,goals);
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        gridView.setAdapter(gridAdapter);
//
        tvAvgV = (TextView) findViewById(R.id.avgNum);
        tvTotV = (TextView) findViewById(R.id.totalNum);
        tvMaxV = (TextView) findViewById(R.id.maxNum);
        tvMinV = (TextView) findViewById(R.id.minNum);
        tvAvgP = (TextView) findViewById(R.id.avgPerNum);
        tvMaxP = (TextView) findViewById(R.id.maxPerNum);
        tvMinP = (TextView) findViewById(R.id.minPerNum);


//        tvGoalTitleNum = (TextView) findViewById(R.id.goalsTitleNum);

        //sbTop.setProgress(100);
        sbTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress<=minUpperBound){
                    tvstop.setText(minUpperBound+"");
                    sbTop.setProgress(minUpperBound);
                    maxLowerBound=minUpperBound;

                }else{
                    tvstop.setText(progress+"");
                    maxLowerBound=progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //sbBot.setProgress(0);
        sbBot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress>=maxLowerBound){

                    tvsbot.setText(progress+"");
                    sbBot.setProgress(maxLowerBound);
                    minUpperBound=maxLowerBound;
                }else {
                    tvsbot.setText(progress+"");

                    minUpperBound=progress;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button confirmBut = (Button) findViewById(R.id.buttonConfirm);
        confirmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            popStats();
            }
        });
        popStats();
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
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

    public void popStats(){
        unitsSpinString = unitsSpin.getSelectedItem().toString();
        goals.clear();
        float totalValue=0f;
        float averageValue=0f;
        float maxValue=Float.MIN_VALUE;
        float minValue=Float.MAX_VALUE;
        float totalPercent=0f;
        float averagePercent=0f;
        float maxPercent=Float.MIN_VALUE;
        float minPercent=Float.MAX_VALUE;
        String startPercent = Integer.toString(sbBot.getProgress());
        String endPercent = Integer.toString(sbTop.getProgress());

        Date date = new GregorianCalendar(startYear,startMonth,startDay).getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        startDate = sdf.format(date);
        date = new GregorianCalendar(endYear,endMonth,endDay).getTime();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf.format(date);

        Cursor result = dbHelper.getStatistics(startDate,endDate,startPercent,endPercent);

        //put that result into the table
        //and grid layout
        result.moveToFirst();
        while(!result.isAfterLast()){
            String name = result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_NAME));
            String units = result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_UNITS));

            boolean active = true;
            float steps=0f;
            float percentage=0f;
            try {
                steps = Float.parseFloat(result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS)));
                percentage = result.getFloat(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PERCENTAGE));
            }catch (Exception e){
                e.printStackTrace();
            }
            totalPercent+=percentage;
            totalValue+=steps;

            if(steps>maxValue){
                maxValue=steps;
            }
            if(steps<minValue){
                minValue=steps;
            }

            if(percentage>maxPercent){
                maxPercent=percentage;
            }
            if(percentage<minPercent){
                minPercent=percentage;
            }

            goals.add(new Goal(name,steps,active,units));
            result.moveToNext();
        }
        if(goals.size()==0){
            averageValue=0f;
            averagePercent=0f;
        }else {
            averagePercent = totalPercent / goals.size();
            averageValue = totalValue / goals.size();
        }

       // gridAdapter.notifyDataSetChanged();
        //updateTable()
      //  tvGoalTitleNum.setText(""+goals.size());

        DecimalFormat df = new DecimalFormat("##.##");

        String convertedAverage= df.format(convertToUnits(unitsSpinString,averageValue));
        String convertedMax= df.format(convertToUnits(unitsSpinString,maxValue));
        if(minValue==Float.MAX_VALUE){
            minValue=0f;
        }
        String convertedMin= df.format(convertToUnits(unitsSpinString,minValue));
        String convertedTotal= df.format(convertToUnits(unitsSpinString,totalValue));

        String convertedAveragePercent= df.format(averagePercent);
        String convertedMaxPercent= df.format(maxPercent);
        if(minPercent==Float.MAX_VALUE){
            minPercent=0f;
        }
        String convertedMinPercent= df.format(minPercent);

        tvAvgV.setText(convertedAverage);
        tvMaxV.setText(convertedMax);
        tvMinV.setText(convertedMin);
        tvTotV.setText(convertedTotal);

        tvAvgP.setText(convertedAveragePercent);
        tvMaxP.setText(convertedMaxPercent);
        tvMinP.setText(convertedMinPercent);

    }

    public void setDefaults(){
        final Calendar c = Calendar.getInstance();
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);

        String lowBound = SP.getString("percentageStatLower","0");
        String highBound = SP.getString("percentageStatUpper","100");
        int low=0;
        int high=0;
        try{
            low = Integer.parseInt(lowBound);
            high = Integer.parseInt(highBound);
        }catch (Exception e){
            e.printStackTrace();
        }
        sbBot.setProgress(low);
        tvsbot.setText(low+"");
        sbTop.setProgress(high);
        tvstop.setText(high+"");

        String unitsValue = SP.getString("defaultUnitStat","1");
        switch (unitsValue){
            case "1":
                unitsSpinString="Steps";
                break;
            case "2":
                unitsSpinString="Kilometres";
                break;
            case "3":
                unitsSpinString="Metres";
                break;
            case "4":
                unitsSpinString="Miles";
                break;
            case "5":
                unitsSpinString="Yards";
                break;
        }
        int unit=1;
        try{
            //Android leaves residual files after uninstall
            //which mess with the current install
           unit= Integer.parseInt(unitsValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        unitsSpin.setSelection(unit-1);
//        switch (unitsValue){
//            case "Steps":
//                units=0;
//                break;
//            case "Kilometres":
//                units=1;
//                break;
//            case "Metres":
//                units=2;
//                break;
//            case "Miles":
//                units=3;
//                break;
//            case "Yards":
//                units=4;
//                break;
//        }
        //unitsSpinString=unitsValue;
 //       unitsSpin.setSelection(units);

        String array = SP.getString("pastLengthStat","1");

        int curMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int backLength=0;
        switch (array){
            case "1":
                backLength=-7;
                break;
            case "2":
                backLength=-14;
                break;
            case "3":
                backLength=-curMonth;
                break;
            case "4":
                backLength=-(curMonth*2);
                break;
        }

        c.add(Calendar.DATE,backLength);
        startYear=c.get(Calendar.YEAR);
        startMonth=c.get(Calendar.MONTH);
        startDay=c.get(Calendar.DAY_OF_MONTH);
    }

    public void putWarning() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        boolean testMode = SP.getBoolean("enableTest", false);
        ImageView iv = (ImageView) findViewById(R.id.alertTestMode);
        if (testMode) {
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        putWarning();
        setDefaults();
    }
}
