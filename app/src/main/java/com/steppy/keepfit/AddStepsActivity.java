package com.steppy.keepfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class AddStepsActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_steps);


       // if getSharedPreferences()

        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText goalProgress = (EditText) findViewById(R.id.stepsText);
                String progressString = goalProgress.getText().toString();
                Date date = new Date();

                dbHelper = new DBHelper(AddStepsActivity.this);

                if(dbHelper.updateDayProgress(progressString)){
                    Toast.makeText(AddStepsActivity.this, "Goal "+progressString, Toast.LENGTH_LONG).show();
                }
                dbHelper.close();
                backToPrevFrag();

            }
        });

    }

    public void backToPrevFrag(){
        Intent fragBack = new Intent(this, MainActivity.class);
        fragBack.putExtra("openPrevFrag",true);
        startActivity(fragBack);
    }
}
