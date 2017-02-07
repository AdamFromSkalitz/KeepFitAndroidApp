package com.steppy.keepfit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Turkleton's on 07/02/2017.
 */
public class GraphFragment extends Fragment {

    List<BarEntry> entries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View graphView = inflater.inflate(R.layout.fragment_graph, container, false);

        BarChart chart = (BarChart) graphView.findViewById(R.id.chart);

        entries.add(new BarEntry(1,3));
        entries.add(new BarEntry(2,7));
        entries.add(new BarEntry(3,6));
        entries.add(new BarEntry(4,4));

        BarDataSet dataSet = new BarDataSet(entries,"Label");
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.BLUE);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();

        return graphView;
    }
}
