package com.steppy.keepfit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.Date;

public class AddGoalsActivity extends AppCompatActivity {
    MainActivity mainact;
    private BottomBar bottomBar;
    final Handler handler = new Handler();
    private ArrayList<Goal> listToBeSaved = new ArrayList<>();

    DBHelper dbHelper;
    FileOutputStream fos;
    ObjectOutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goals);
//
//        Toast.makeText(ViewGoalsActivity.this,"ViewGoals",Toast.LENGTH_LONG).show();
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(" ");


        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.nameText);
                EditText goal = (EditText) findViewById(R.id.goalsText);
                //CheckBox active = (CheckBox) findViewById(R.id.checkBox);
                Date date = new Date();

                dbHelper = new DBHelper(AddGoalsActivity.this);
                String nameString = name.getText().toString();
                String goalString = goal.getText().toString();
                //String activeString = String.valueOf(active.isChecked());
                String dateString = date.toString();

                if(dbHelper.insertGoal(nameString,goalString, "false", dateString)){
                    Toast.makeText(AddGoalsActivity.this, "Name " + name.getText() + " goal " + goal.getText(), Toast.LENGTH_LONG).show();
                }
                dbHelper.close();

                backToPrevFrag();


                //getFragmentManager().popBackStack();


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void backToPrevFrag(){
        Intent fragBack = new Intent(this, MainActivity.class);
        fragBack.putExtra("openPrevFrag",true);
        startActivity(fragBack);
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
