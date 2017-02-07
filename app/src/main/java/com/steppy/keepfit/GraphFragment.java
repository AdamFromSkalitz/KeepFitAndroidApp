package com.steppy.keepfit;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Turkleton's on 07/02/2017.
 */
public class GraphFragment extends Fragment {
    private DBHelper dbHelper;
    List<BarEntry> entries = new ArrayList<>();
    List<String> dates = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View graphView = inflater.inflate(R.layout.fragment_graph, container, false);
        dbHelper = new DBHelper(getActivity());
        BarChart chart = (BarChart) graphView.findViewById(R.id.chart);

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
                Toast.makeText(getActivity(),dateString,Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),blah,Toast.LENGTH_SHORT).show();
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
            final String[] quarters = new String[] { dates.get(0), "1/2/67", "Q3", "Q4" };

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

//                // we don't draw numbers, so no decimal digits needed
//                @Override
//                public int getDecimalDigits() {  return 0; }
        };
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
