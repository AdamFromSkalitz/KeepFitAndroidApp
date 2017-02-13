package com.steppy.keepfit;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.Date;

/**
 * Created by Turkleton's on 07/02/2017.
 */

public class AddGoalsFragment extends Fragment {
    private View addGoalsView;
    private DBHelper dbHelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addGoalsView = inflater.inflate(R.layout.fragment_addgoals, container, false);

//        View decorView = getActivity().getWindow().getDecorView();
//// Hide both the navigation bar and the status bar.
//// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//// a general rule, you should design your app to hide the status bar whenever you
//// hide the navigation bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

//        Button but = (Button) addGoalsView.findViewById(R.id.setButton);
//        but.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText name = (EditText) addGoalsView.findViewById(R.id.nameText);
//                EditText goal = (EditText) addGoalsView.findViewById(R.id.goalsText);
//                //CheckBox active = (CheckBox) findViewById(R.id.checkBox);
//                Date date = new Date();
//
//                dbHelper = new DBHelper(getActivity());
//                String nameString = name.getText().toString();
//                String goalString = goal.getText().toString();
//                int goalInt
//                //String activeString = String.valueOf(active.isChecked());
//                String dateString = date.toString();
//
//                if(dbHelper.insertGoal(nameString,goalString, "false", dateString,"")){
//                    Toast.makeText(getActivity(), "Name " + name.getText() + " goal " + goal.getText(), Toast.LENGTH_LONG).show();
//                }
//                dbHelper.close();
//
//                getActivity().getFragmentManager().popBackStackImmediate();
//
//            }
//        });
       return addGoalsView;
    }


}
