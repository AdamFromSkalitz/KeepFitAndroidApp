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


import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Turkleton's on 07/02/2017.
 */
public class GraphFragment extends Fragment {
    private DBHelper dbHelper;
    List<String> dates;

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
    private String cutOffPercentage="0";
    private Button buttonConfirm;
    //private Cursor customResult;
    View graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        graphView = inflater.inflate(R.layout.fragment_graph, container, false);

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
        //startDate = mDay+"/"+mMonth+"/"+mYear;
        //endDate = (mDay+7)+"/"+mMonth+"/"+mYear;

        butStartDate = (Button) graphView.findViewById(R.id.buttonStartDate);
        butStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //startDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                                Date date = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                                String dateString1 = sdf.format(date);
                                startDate = dateString1;
                                Toast.makeText(getActivity(),dateString1,Toast.LENGTH_SHORT).show();
                                butStartDate.setText(startDate);
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
                                //endDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                                Date date = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dateString1 = sdf.format(date);
                                endDate = dateString1;
                                Toast.makeText(getActivity(),dateString1,Toast.LENGTH_SHORT).show();
                                butEndDate.setText(dateString1);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        final Spinner unitsSpin = (Spinner) graphView.findViewById(R.id.spinnerUnits);

        final Spinner cutOffSpin = (Spinner) graphView.findViewById(R.id.spinnerCutOff);

        final EditText cutOffEdit = (EditText) graphView.findViewById(R.id.editTextCutOff);


        dbHelper = new DBHelper(getActivity());
        buttonConfirm = (Button) graphView.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create query with user given stuff
                cutOffDirection = cutOffSpin.getSelectedItem().toString();
                cutOffPercentage = cutOffEdit.getText().toString();
                unitsString = unitsSpin.getSelectedItem().toString();

                Cursor customResult  = dbHelper.getCustomUserOldGoals(statistics,startDate,endDate,unitsString,cutOffDirection,cutOffPercentage);
                popGraph(customResult);

            }
        });
        //drawCombiChart();
        //generateBarTwoData();
        return graphView;
    }

    public void popGraph(Cursor customResult){
        //Cursor result = dbHelper.getAllOldGoals();
        //Cursor result = customResult;
        List<BarEntry> entries = new ArrayList<>();
        dates = new ArrayList<>();
        List<String> goalNames = new ArrayList<>();

        customResult.moveToFirst();

        int i=0;
        while(!customResult.isAfterLast()){
            String name = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_NAME));
            goalNames.add(name);
            String dateString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_DATE));
            String progressString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS));
            String goalString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_GOALVALUE));
            String percent = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PERCENTAGE));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            String blah="";
            try{
                date = df.parse(dateString);
                blah = df.format(date);

                dates.add(date.getDate()+"/"+date.getMonth()+"-"+name);
                //Toast.makeText(getActivity(),dateString,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(),blah,Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
            int progressInt = Integer.parseInt(progressString);
            int goalInt = Integer.parseInt(goalString);
            int remainder = goalInt-progressInt;
            if(remainder<0){
                remainder=0;
            }
            //new BarEntry()
            entries.add(new BarEntry(i,new float[]{progressInt,remainder},name));

            final String finalBlah=blah;

            i++;
            customResult.moveToNext();
        }
        customResult.close();
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

        chart = clearBarChart(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);


        BarDataSet dataSet = new BarDataSet(entries,"");


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

        int[] colorsArray =  new int[colors.size()];

        //colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(getColors());

        dataSet.setStackLabels(new String[]{"progress", "goal total", });

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);

        chart.setData(barData);
        chart.invalidate();


    }
    public BarChart clearBarChart(BarChart chart){
        List<BarEntry> emptyEntries = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(emptyEntries,"");
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();
        return chart;
    }


    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }

        return colors;
    }

}


