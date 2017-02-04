package com.steppy.keepfit;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by Turkleton's on 31/01/2017.
 */

public class MainFragment extends Fragment {
    TextView progress;
    TextView goalValue;
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        final View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        dbHelper = new DBHelper(getActivity());

        goalValue = (TextView) mainView.findViewById(R.id.textViewGoalNumber);
        Cursor cursor = dbHelper.getActiveGoal();
        cursor.moveToFirst();
        try {
            String goal = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
            goalValue.setText(goal);
        }catch(Exception e){
            e.printStackTrace();
        }
        progress = (TextView) mainView.findViewById(R.id.textViewProgressNumber);
        //cursor = dbHelper.getDayProgress();
//        cursor.moveToFirst();
//        String active = cursor.getString(cursor.getColumnIndex(DBHelper.PROGRESS_COLUMN_STEPS));
//        progress.setText(active);

        cursor.close();
        dbHelper.closeDB();


        FloatingActionButton myFab = (FloatingActionButton) mainView.findViewById(R.id.fabGoals);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addGoals();
            }
        });

        return mainView;
    }


    public void addGoals() {
        Intent intent = new Intent(getActivity(), AddGoalsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}