package com.steppy.keepfit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class EditGoalsActivity extends AppCompatActivity {
    private BottomBar bottomBar;
    final Handler handler = new Handler();
    private ArrayList<Goal> listToBeSaved = new ArrayList<>();


    Intent intent;
    DBHelper dbHelper;
    FileOutputStream fos;
    ObjectOutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals);
//
//        Toast.makeText(ViewGoalsActivity.this,"ViewGoals",Toast.LENGTH_LONG).show();
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");

        intent = getIntent();
        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.nameText);
                EditText goal = (EditText) findViewById(R.id.goalsText);
                //CheckBox active = (CheckBox) findViewById(R.id.checkBox);


                dbHelper = new DBHelper(EditGoalsActivity.this);
                String nameString = name.getText().toString();
                String goalString = goal.getText().toString();


                String existGoalName = intent.getStringExtra("name");
                Cursor goalToEditRes = dbHelper.getGoal(existGoalName);

                goalToEditRes.moveToFirst();
                int id = goalToEditRes.getInt(goalToEditRes.getColumnIndex(DBHelper.COLUMN_ID));
                //String existGoalName = goalToEditRes.getString(goalToEditRes.getColumnIndex(DBHelper.COLUMN_NAME));
                String existGoalValue = goalToEditRes.getString(goalToEditRes.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                String dateString = goalToEditRes.getString(goalToEditRes.getColumnIndex(DBHelper.COLUMN_DATE));
                String active = goalToEditRes.getString(goalToEditRes.getColumnIndex(DBHelper.COLUMN_ACTIVE));
                goalToEditRes.close();

                String units = goalToEditRes.getString(goalToEditRes.getColumnIndex(DBHelper.COLUMN_UNITS));

                if(goalString.equals("")){
                    goalString = existGoalValue;
                }
                if(nameString.equals("")){
                    nameString = existGoalName;
                }
                if (dbHelper.updateGoal(id, nameString, goalString, active, dateString,units)) {
                    Toast.makeText(EditGoalsActivity.this, "Name " +nameString + " goal " + goalString, Toast.LENGTH_LONG).show();
                }
                dbHelper.close();
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