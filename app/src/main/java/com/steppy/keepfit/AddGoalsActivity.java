package com.steppy.keepfit;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class AddGoalsActivity extends AppCompatActivity {
    private BottomBar bottomBar;
    final Handler handler = new Handler();
    private ArrayList<Goal> listToBeSaved = new ArrayList<>();


    FileOutputStream fos;
    ObjectOutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goals);
//
//        Toast.makeText(ViewGoalsActivity.this,"ViewGoals",Toast.LENGTH_LONG).show();
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");


        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.nameText);
                EditText goal = (EditText) findViewById(R.id.goalsText);
                CheckBox active = (CheckBox) findViewById(R.id.checkBox);

                Toast.makeText(AddGoalsActivity.this, "Name " + name.getText() + " goal " + goal.getText() + " active " + active.isChecked(), Toast.LENGTH_LONG).show();

                String FILENAME = "goals.txt";
                String r = "read";
                String nameString = name.getText().toString();
                String goalString = goal.getText().toString();
                String activeString = String.valueOf(active.isChecked());
                Goal thisGoal = new Goal(nameString,Integer.parseInt(goal.getText().toString()),active.isChecked());
                listToBeSaved.add(thisGoal);
                try {
                     fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                    //OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
                     os = new AppendingObjectOutputStream (fos);
                    //outputWriter.write(r);
                    os.writeObject(thisGoal);
                    os.close();
                    fos.close();

//                    outputWriter.write(nameString);
//                    outputWriter.write(goalString);
//                    outputWriter.write(activeString);
//                    outputWriter.close();
                    Toast.makeText(getBaseContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
// fos.write(nameString);
//                fos.write(string.getBytes());
//                fos.write(string.getBytes());
//
//                fos.close();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
