package com.steppy.keepfit;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Turkleton's on 03/02/2017.
 */

public class ViewGoalsFragment extends Fragment{
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    static final int READ_BLOCK_SIZE = 100;
    TextView v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        adapter=new CustomAdapter<String>(this,
//                R.layout.row,
//                listItems);
//        setListAdapter(adapter);

        // Inflate the layout for this fragment
        final View goalView = inflater.inflate(R.layout.fragment_goals, container, false);

        v = (TextView) goalView.findViewById(R.id.tv);
        populateList();


        FloatingActionButton myFab = (FloatingActionButton) goalView.findViewById(R.id.fabGoals);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addGoals();
            }
        });

        return goalView;
    }

    private void populateList() {
        String FILENAME = "goals.txt";
        try{
            FileInputStream fileIn = getActivity().openFileInput(FILENAME);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="bling";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            //Toast.makeText(AddGoalsActivity.this, "thing " + s, Toast.LENGTH_LONG).show();
            v.setText(s);


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
