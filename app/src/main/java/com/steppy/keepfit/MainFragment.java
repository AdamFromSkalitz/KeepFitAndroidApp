package com.steppy.keepfit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import static android.icu.lang.UCharacter.SentenceBreak.SP;

/**
 * Created by Turkleton's on 31/01/2017.
 */

public class MainFragment extends Fragment {
    TextView progress;
    TextView goalValue;
    DBHelper dbHelper;
    private String stepsProgress;
    public static final String PREFS_NAME = "MyPrefsFile";


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
        Cursor cursor = dbHelper.getActiveGoal();
        cursor.moveToFirst();
        try {
            String goal = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
            goalValue.setText("Goal   "+goal);
        }catch(Exception e){
            e.printStackTrace();
        }
        cursor.close();

        progress = (TextView) mainView.findViewById(R.id.textViewProgressNumber);

        cursor = dbHelper.getDayProgress();
        cursor.moveToFirst();
        try {
            stepsProgress = cursor.getString(cursor.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
        }catch (Exception e){
            stepsProgress = "0";
            e.printStackTrace();
        }
        progress.setText("Progress:   " + stepsProgress);
        cursor.close();
        dbHelper.closeDB();


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