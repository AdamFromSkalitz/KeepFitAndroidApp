package com.steppy.keepfit;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView listView;
    EditText editTextView;
    ArrayList<Goal> ItemGoalList;
    CustomAdapter customAdapter;

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

        return goalView;
    }

//    @SuppressLint("NewApi")
//    public void addValue(View v) {
//        String name = editTextView.getText().toString();
//        if (name.isEmpty()) {
//            Toast.makeText(getActivity(), "Plz enter Values",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            Goal md = new Goal(name);
//            ItemGoalList.add(md);
//            customAdapter.notifyDataSetChanged();
//            editTextView.setText("");
//        }
//    }

    private void populateList() {
        String FILENAME = "goals.txt";
        try{
//            FileInputStream fileIn = getActivity().openFileInput(FILENAME);
//            InputStreamReader InputRead = new InputStreamReader(fileIn);
//
//            char[] inputBuffer= new char[READ_BLOCK_SIZE];
//            String s="h";
//            int charRead;
//
//            while ((charRead=InputRead.read(inputBuffer))>0) {
//                // char to string conversion
//                String readstring=String.copyValueOf(inputBuffer,0,charRead);
//                s +=readstring;
//            }
//            InputRead.close();
             fis = getActivity().openFileInput(FILENAME);
             is = new ObjectInputStream(fis);

            while (true) {
                try {
                    Goal goal = (Goal) is.readObject();
                    ItemGoalList.add(goal);
                } catch (EOFException e) {
                    customAdapter.notifyDataSetChanged();
                    is.close();
                    fis.close();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    public void addGoals() {
        Intent intent = new Intent(getActivity(), AddGoalsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
