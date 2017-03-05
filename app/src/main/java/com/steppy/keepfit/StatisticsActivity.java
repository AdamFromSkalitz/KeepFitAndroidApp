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
    private int m;

    private float total=0f;
    private float average=0f;
    private float max=Float.MIN_VALUE;
    private float min=Float.MAX_VALUE;

    private int minUpperBound=0;
    private int maxLowerBound=100;

    //private String[] goals={"Goal1","Goal2", "Goal3","Goal4","Goal5","Goal6","Goal7"};
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

        dbHelper = new DBHelper(StatisticsActivity.this);

        setDefaultDates();

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

        unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);

        gridAdapter = new GridAdapter(this,goals);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);

        final TextView tvAvg = (TextView) findViewById(R.id.avgNum);
        final TextView tvTot = (TextView) findViewById(R.id.totalNum);
        final TextView tvMax = (TextView) findViewById(R.id.maxNum);
        final TextView tvMin = (TextView) findViewById(R.id.minNum);

        final TextView tvGoalTitleNum = (TextView) findViewById(R.id.goalsTitleNum);

        final TextView tvstop = (TextView) findViewById(R.id.seektvtop);
        final SeekBar sbTop = (SeekBar) findViewById(R.id.seekBarTop);
        final SeekBar sbBot = (SeekBar) findViewById(R.id.seekBarBot);

        sbTop.setProgress(100);
        sbTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress<=minUpperBound){
                    m=1;
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

        final TextView tvsbot = (TextView) findViewById(R.id.seektvbot);

        sbBot.setProgress(0);
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
                String unitsSpinString = unitsSpin.getSelectedItem().toString();
                goals.clear();
                total=0f;
                max=Float.MIN_VALUE;
                min=Float.MAX_VALUE;
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
                    try {
                        steps = Float.parseFloat(result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    total+=steps;
                    if(steps>max){
                        max=steps;
                    }
                    if(steps<min){
                        min=steps;
                    }

                    goals.add(new Goal(name,steps,active,units));
                    result.moveToNext();
                }
                if(goals.size()==0){
                    average=0f;
                }else {
                    average = total / goals.size();
                }
                gridAdapter.notifyDataSetChanged();
                //updateTable()
                tvGoalTitleNum.setText(""+goals.size());

                DecimalFormat df = new DecimalFormat("##.##");

                String convertedAverage= df.format(convertToUnits(unitsSpinString,average));
                String convertedMax= df.format(convertToUnits(unitsSpinString,max));
                if(min==Float.MAX_VALUE){
                    min=0f;
                }
                String convertedMin= df.format(convertToUnits(unitsSpinString,min));
                String convertedTotal= df.format(convertToUnits(unitsSpinString,total));


                tvAvg.setText(convertedAverage);
                tvMax.setText(""+convertedMax);
                tvMin.setText(""+convertedMin);
                tvTot.setText(""+convertedTotal);

            }
        });
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }


    public float convertToUnits(String unitsSpinString,float progress){

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);;

        switch (unitsSpinString){
            case "Kilometres":
                float cmMap = Float.parseFloat(SP.getString("mappingMet","75"));
                int progressStepsCM = (int)progress*(int)cmMap;
                progress = (float)progressStepsCM/100000;
                break;
            case "Miles":
                float inch = Float.parseFloat(SP.getString("mappingImp","30"));
                float progressStepsINC = progress*inch;
                progress = progressStepsINC/(36*1760);
                break;
            case "Steps":
                break;
        }
        return progress;
    }

    public void setDefaultDates(){
        final Calendar c = Calendar.getInstance();
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        String array = SP.getString("pastLength","1");

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

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultDates();
    }
}
