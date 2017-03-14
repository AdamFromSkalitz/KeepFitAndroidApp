package com.steppy.keepfit;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.fingerprintAuthDrawable;

/**
 * Created by Turkleton's on 03/02/2017.
 */

public class ViewGoalsFragment extends Fragment{
    ListView listView;
    ArrayList<Goal> ItemGoalList;
    RVAdapter adapter;
    //CustomAdapter customAdapter;
    DBHelper dbHelper;
    View goalView;
    FileInputStream fis;
    ObjectInputStream is;
    RecyclerView rv;

    static final int READ_BLOCK_SIZE = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        goalView = inflater.inflate(R.layout.fragment_goals, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Goals");

        putWarning();
        ItemGoalList = new ArrayList<Goal>();
        rv = (RecyclerView) goalView.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(goalView.getContext());
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(ItemGoalList,goalView.getContext(),getActivity());
        rv.setAdapter(adapter);
        populateList();


        final FloatingActionMenu goalMenu =(FloatingActionMenu) goalView.findViewById(R.id.floatingMenu);
        final FloatingActionButton goalFab = (FloatingActionButton) goalView.findViewById(R.id.fabGoals);
        goalFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addGoals();
            }
        });
        FloatingActionButton stepFab = (FloatingActionButton) goalView.findViewById(R.id.fabSteps);
        stepFab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addSteps();
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int scrollDistance=0;
                if(dy > scrollDistance){
                    goalMenu.hideMenu(true);
                }else if(dy<scrollDistance){
                    goalMenu.showMenu(true);
                }
            }

//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
//            {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
//                {
//                    goalMenu.showMenu(true);
//                }
//
//                super.onScrollStateChanged(recyclerView, newState);
//            }
        });

        return goalView;
    }




    private void populateList() {

        dbHelper = new DBHelper(getActivity());
        ItemGoalList.clear();
        //final Cursor curs = dbHelper.getAllOldGoals();


        final Cursor cursor = dbHelper.getAllGoals();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String active = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ACTIVE));
            boolean activeBool = Boolean.parseBoolean(active);
            Float goalValue = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));

            String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
            String units = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_UNITS));
            int goalValueInt = Math.round(convertToUnits(units, goalValue));

            Goal goal = new Goal(name,goalValueInt,activeBool,units);
            ItemGoalList.add(goal);
            cursor.moveToNext();
        }
        //customAdapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();

        TextView emptyView = (TextView) goalView.findViewById(R.id.empty_view);
        if (ItemGoalList.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    public void addGoals() {
        Intent intent = new Intent(getActivity(), AddGoalsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("list", ItemGoalList);
        //intent.putExtra("adapter",adapter);
        startActivity(intent);
    }
    public void addSteps(){
        Intent intent = new Intent(getActivity(),AddStepsActivity.class);
        startActivity(intent);
    }
    public float convertToUnits(String unitsSpinString,float progress){

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

    public void putWarning(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean testMode = SP.getBoolean("enableTest", false);
        ImageView iv = (ImageView) getActivity().findViewById(R.id.alertTestMode);
        if(testMode){
            iv.setVisibility(View.VISIBLE);
        }else{
            iv.setVisibility(View.INVISIBLE);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        putWarning();
        populateList();
    }


}
