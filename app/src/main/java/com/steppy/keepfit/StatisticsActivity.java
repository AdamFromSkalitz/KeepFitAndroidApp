package com.steppy.keepfit;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private int mYear;
    private int mMonth;
    private int mDay;
    private Spinner unitsSpin;
    private DBHelper dbHelper;
    private int m;

    private float total=0f;
    private float average=0f;
    private float max=0f;
    private float min=0f;

    private int minUpperBound=0;
    private int maxLowerBound=100;

    private String[] goals={"Goal1","Goal2", "Goal3","Goal4","Goal5","Goal6","Goal7"};
    //private ArrayList<Goal> goals = new ArrayList<>();
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DBHelper(StatisticsActivity.this);


        //Set up initial start and end dates
        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
        mYear =  2017;
        mMonth = 0;
        mDay = 2;

        Date date = new GregorianCalendar(mYear,mMonth,mDay).getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        String dateString1 = sdf.format(date);
        endDate = dateString1;

        //setInitalDates();
        int monthPrev=0;
        int dayPrev=0;
        int yearPrev=mYear;
        if(mDay-7<1){
            //change month to previous month
            if(mMonth-1<0) {
                //month is zero indexed, yes from this dialogue
                yearPrev = mYear - 1;
                monthPrev=0;
                dayPrev=31-(7-mDay);
            }else{
                String month = getMonth(mMonth-1);
                if(month.equals("September") | month.equals("April") | month.equals("June")|month.equals("November")){
                    int fullMonthDays=30;
                    dayPrev=fullMonthDays-(7-mDay);
                }else if(month.equals("February")) {
                    int fullMonthDays=29;
                    dayPrev=fullMonthDays-(7-mDay);
                }else{
                    int fullMonthDays=31;
                    dayPrev=fullMonthDays-(7-mDay);
                }
                monthPrev = mMonth - 1;
            }
        }else{
            dayPrev=mDay-7;
        }

        Toast.makeText(StatisticsActivity.this,"day "+dayPrev+" month "+monthPrev+ " y "+yearPrev,Toast.LENGTH_SHORT).show();

        date = new GregorianCalendar(mYear,mMonth,mDay).getTime();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        dateString1 = sdf.format(date);
        startDate = dateString1;



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
                                //startDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                                Date date = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                                String dateString1 = sdf.format(date);
                                startDate = dateString1;
                                Toast.makeText(StatisticsActivity.this,dateString1,Toast.LENGTH_SHORT).show();
                                butStartDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, mYear, mMonth, mDay);
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
                                //endDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                                Date date = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dateString1 = sdf.format(date);
                                endDate = dateString1;
                                Toast.makeText(StatisticsActivity.this,dateString1,Toast.LENGTH_SHORT).show();
                                butEndDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);


        gridAdapter = new GridAdapter(this,goals);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);

        TableRow tl = (TableRow) findViewById(R.id.tableRow2);
        TextView tv = (TextView) findViewById(R.id.avgNum);

        final TextView tvs = (TextView) findViewById(R.id.seektvtop);

        final SeekBar sbTop = (SeekBar) findViewById(R.id.seekBarTop);
        final SeekBar sbBot = (SeekBar) findViewById(R.id.seekBarBot);

        sbTop.setProgress(100);

        sbTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress<=minUpperBound){
                    m=1;
                    tvs.setText(minUpperBound+"");
                    sbTop.setProgress(minUpperBound);
                    maxLowerBound=minUpperBound;

                }else{
                    tvs.setText(progress+"");
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

        final TextView tvsbbot = (TextView) findViewById(R.id.seektvbot);

        sbBot.setProgress(0);
        sbBot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress>=maxLowerBound){
                    tvsbbot.setText(progress+"");
                    sbBot.setProgress(maxLowerBound);
                    minUpperBound=maxLowerBound;
                }else {
                    tvsbbot.setText(progress+"");

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

                String startPercent="";
                String endPercent="";
                Cursor result = dbHelper.getStatistics(startDate,endDate,startPercent,endPercent);

                //put that result into the table
                //and grid layout
                result.moveToFirst();
                while(!result.isAfterLast()){
                    String name = result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_NAME));
                    boolean active = false;
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
                    //goals.add(new Goal(name,steps,active));
                }
                //average=total/goals.size();

                gridAdapter.notifyDataSetChanged();
            }
        });
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

}
