package com.steppy.keepfit;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Turkleton's on 07/02/2017.
 */
public class GraphFragment extends Fragment {
    private DBHelper dbHelper;
    List<BarEntry> entries = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    private String statistics = "Total";
    private String startDate="";
    private String endDate="";
    private Button butStartDate;
    private Button butEndDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String unitsString="";
    private String cutOffDirection="No Selection";
    private int cutOffPercentage=0;
    private Button buttonConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View graphView = inflater.inflate(R.layout.fragment_graph, container, false);

        TabLayout tabLayout = (TabLayout) graphView.findViewById(R.id.layoutTab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(getActivity(),tab.getText(),Toast.LENGTH_SHORT).show();
                statistics = tab.getText().toString();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //Set up initial start and end dates
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        startDate = mDay+"/"+mMonth+"/"+mYear;
        endDate = (mDay+7)+"/"+mMonth+"/"+mYear;

        butStartDate = (Button) graphView.findViewById(R.id.buttonStartDate);
        butStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startDate = dayOfMonth+"/"+monthOfYear+"/"+year;

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        butEndDate = (Button) graphView.findViewById(R.id.buttonEndDate);
        butEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endDate = dayOfMonth+"/"+monthOfYear+"/"+year;

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        Spinner unitsSpin = (Spinner) graphView.findViewById(R.id.spinnerUnits);
        unitsString = unitsSpin.getSelectedItem().toString();

        Spinner cutOffSpin = (Spinner) graphView.findViewById(R.id.spinnerCutOff);
        cutOffDirection = cutOffSpin.getSelectedItem().toString();

        EditText cutOffEdit = (EditText) graphView.findViewById(R.id.editTextCutOff);
        cutOffPercentage = Integer.parseInt(cutOffEdit.getText().toString());

        dbHelper = new DBHelper(getActivity());
        buttonConfirm = (Button) graphView.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create query with user given stuff
                Cursor customResult  = dbHelper.getCustomUserOldGoals(statistics,startDate,endDate,unitsString,cutOffDirection,cutOffPercentage);
            }
        });



        Cursor result = dbHelper.getAllOldGoals();
        result.moveToFirst();

        int i=0;
        while(!result.isAfterLast()){
            String dateString = result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_DATE));
            String progressString = result.getString(result.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS));
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            Date date;
            String blah="";
            try{
                date = df.parse(dateString);
                blah = df.format(date);
                dates.add(blah);
                //Toast.makeText(getActivity(),dateString,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(),blah,Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
            int progressInt = Integer.parseInt(progressString);
            entries.add(new BarEntry(i,progressInt));

            final String finalBlah=blah;

            i++;
            result.moveToNext();
        }
        result.close();
        dbHelper.close();


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            //final String[] quarters = new String[dates.size()]; //{ dates.get(0), "1/2/67", "Q3", "Q4" };
            final String[] quarters = dates.toArray(new String[dates.size()]);
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };

        BarChart chart = (BarChart) graphView.findViewById(R.id.chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

//        entries.add(new BarEntry(2,7));
//        entries.add(new BarEntry(3,6));
//        entries.add(new BarEntry(4,4));

        BarDataSet dataSet = new BarDataSet(entries,"Steps Taken");
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.BLUE);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();

        return graphView;
    }
}
