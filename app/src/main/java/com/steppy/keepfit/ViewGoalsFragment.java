package com.steppy.keepfit;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by Turkleton's on 03/02/2017.
 */

public class ViewGoalsFragment extends Fragment{
    ListView listView;
    ArrayList<Goal> ItemGoalList;
    CustomAdapter customAdapter;
    DBHelper dbHelper;

    FileInputStream fis;
    ObjectInputStream is;

    static final int READ_BLOCK_SIZE = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View goalView = inflater.inflate(R.layout.fragment_goals, container, false);


        listView = (ListView) goalView.findViewById(R.id.goalList);
        ItemGoalList = new ArrayList<Goal>();
        customAdapter = new CustomAdapter(getActivity(), ItemGoalList);
        listView.setEmptyView(goalView.findViewById(R.id.empty));
        listView.setAdapter(customAdapter);


        populateList();


        FloatingActionButton myFab = (FloatingActionButton) goalView.findViewById(R.id.fabGoals);
        myFab.setOnClickListener(new View.OnClickListener() {
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
        return goalView;
    }




    private void populateList() {

        dbHelper = new DBHelper(getActivity());
        final Cursor cursor = dbHelper.getAllGoals();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String active = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ACTIVE));
            boolean activeBool = Boolean.parseBoolean(active);
            String goalValue = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
            int goalValueInt = Integer.parseInt(goalValue);
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));


            Goal goal = new Goal(name,goalValueInt,activeBool);
            ItemGoalList.add(goal);

            if(activeBool){
                //make all item views images pauses
                View view = listView.getRootView();
                //View image = view.findViewById(R.id.imgActive);

            }
            cursor.moveToNext();
        }
        customAdapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }


    public void addGoals() {
        Intent intent = new Intent(getActivity(), AddGoalsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    public void addSteps(){
        Intent intent = new Intent(getActivity(),AddStepsActivity.class);
        startActivity(intent);
    }

}
