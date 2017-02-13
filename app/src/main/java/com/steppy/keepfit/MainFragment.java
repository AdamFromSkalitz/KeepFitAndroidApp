package com.steppy.keepfit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.entries;
import static android.R.attr.uncertainGestureColor;
import static android.icu.lang.UCharacter.SentenceBreak.SP;

/**
 * Created by Turkleton's on 31/01/2017.
 */

public class MainFragment extends Fragment {
    TextView progress;
    TextView goalValue;
    TextView unitsView;
    DBHelper dbHelper;
    private String stepsProgress="0";
    private String goal="0";
    private String units = "";
    public static final String PREFS_NAME = "MyPrefsFile";
    List<PieEntry> entries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        boolean strUserName = SP.getBoolean("enableTest", false);
//        Toast.makeText(getActivity(), Boolean.toString(strUserName), Toast.LENGTH_LONG).show();

        // Inflate the layout for this fragment
        final View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        dbHelper = new DBHelper(getActivity());

        goalValue = (TextView) mainView.findViewById(R.id.textViewGoalNumber);
        unitsView = (TextView) mainView.findViewById(R.id.textUnits);
        Cursor cursor = dbHelper.getActiveGoal();
        cursor.moveToFirst();
        try {
            units = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_UNITS));
            goal = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
        }catch(Exception e){
            goal="0";
            units="";
            e.printStackTrace();
        }
        unitsView.setText("Units:   "+units);
        goalValue.setText("Goal:   "+goal);
        cursor.close();


        progress = (TextView) mainView.findViewById(R.id.textViewProgressNumber);
        cursor = dbHelper.getDayProgress();
        cursor.moveToFirst();
        try {
            int steps= cursor.getInt(cursor.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
            float stepsFloat=0f;
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
            float stepsCM = SP.getFloat("mappingMet",75);
            float stepsInch = SP.getFloat("MappingImp",30);
            switch (units){
                case "Kilometres":
                    float cm = stepsCM*steps;
                    stepsFloat= (float)cm/ 100000;
                    stepsProgress=""+stepsFloat;
                    break;
                case "Miles":
                    float inches = stepsInch*steps;
                    stepsFloat= (float)inches/(1760*36);
                    stepsProgress=""+stepsFloat;
                    break;
                case "Steps":
                    stepsProgress=""+steps;
            }

        }catch (Exception e){
            stepsProgress = "0";
            e.printStackTrace();
        }
        progress.setText("Progress:   " + stepsProgress);
        cursor.close();
        dbHelper.closeDB();

        PieChart chart = (PieChart) mainView.findViewById(R.id.chartPie);
        chart.setUsePercentValues(true);
        //XAxis xAxis = chart.getXAxis();
        //xAxis.setGranularity(1f);
        //xAxis.setValueFormatter(formatter);

        // enable rotation of the chart by touch
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);
        List<LegendEntry> xVals = new ArrayList<LegendEntry>();
        //xVals.add("progree");
        //xVals.add("goal")
        //new Le
        LegendEntry le = new LegendEntry();
        le.label="Progress";
        LegendEntry le2 = new LegendEntry();
        le2.label="Goal";
        xVals.add(le);
        xVals.add(le2);
        //.add(new LegendEntry("progress"));
        legend.setEntries(xVals);

        //legend.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "Set1", "Set2"});

        float stepsProgressInt = Float.parseFloat(stepsProgress);
        int goalValueInt = Integer.parseInt(goal);
        float percent = ((float)stepsProgressInt/(float)goalValueInt) *100;
        //int percent1 = (int) percent*100;
        int percentToComplete = 100-(int)percent;

        if(percentToComplete<0){
            percent=100;
            percentToComplete=0;
        }

        entries.add(new PieEntry((int)percent,"Progress"));
        entries.add(new PieEntry(percentToComplete,"Goal left"));
//        entries.add(new BarEntry(4,4));

        PieDataSet dataSet = new PieDataSet(entries,"");


        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());

        chart.setData(pieData);
        chart.invalidate();


        FloatingActionButton goalFab = (FloatingActionButton) mainView.findViewById(R.id.fabGoals);
        goalFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addGoals();
            }
        });
        FloatingActionButton stepFab = (FloatingActionButton) mainView.findViewById(R.id.fabSteps);
        stepFab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addSteps();
            }
        });

        return mainView;
    }

    public void addGoalsFrag(){
        AddGoalsFragment addGoalsFragment = new AddGoalsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.progressMiddle, addGoalsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void addGoals() {
        Intent intent = new Intent(getActivity(), AddGoalsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        startActivity(intent);
    }

    public void addSteps(){
        Intent intent = new Intent(getActivity(),AddStepsActivity.class);
        startActivity(intent);
    }

}