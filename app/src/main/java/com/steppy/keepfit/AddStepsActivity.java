package com.steppy.keepfit;

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

        Button but = (Button) findViewById(R.id.setButton);
        but.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText goal = (EditText) findViewById(R.id.stepsText);
                String goalString = goal.getText().toString();
                Date date = new Date();

                dbHelper = new DBHelper(AddStepsActivity.this);

                if(dbHelper.updateDayProgress(Integer.parseInt(goalString))){
                    Toast.makeText(AddStepsActivity.this, "Goal "+goalString, Toast.LENGTH_LONG).show();
                }
                dbHelper.close();


            }
        });

    }
}
