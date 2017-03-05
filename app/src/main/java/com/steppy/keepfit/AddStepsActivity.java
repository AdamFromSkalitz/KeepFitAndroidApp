package com.steppy.keepfit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

public class AddStepsActivity extends Activity {

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

                float progressInt=0f;
                try {
                    progressInt = Float.parseFloat(progressString);
//                }catch (NumberFormatException e){
//                    Toast.makeText(AddStepsActivity.this,"Please add a number", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                }catch (Exception e){
                    //Toast.makeText(AddStepsActivity.this,"Please do it properly", Toast.LENGTH_SHORT).show();
                    goalProgress.setError("Field cannot be blank nor contain special characters");
                    e.printStackTrace();
                    return;
                }
                Date date = new Date();

                Spinner unitsSpin = (Spinner) findViewById(R.id.spinnerUnits);
                String units = unitsSpin.getSelectedItem().toString();

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(AddStepsActivity.this);

                int stepsInt = (int)progressInt;
                float steps=0;
                float inches=0;
                float cms=0;
                float inch=0;
                float cm=0;
                switch (units){
                    case "Metres":
//                      // 1 meters = 100cm
                        cms = progressInt*100;
                        cm = SP.getFloat("mappingMet",75);
                        steps = cms/cm;
                        stepsInt = (int) steps;
                        break;
                    case "Kilometres":
                        // 1 kilometer = 1000 metres
                        float m = progressInt*1000;
                        // 1 meters = 100cm
                        cms = m*100;
                        cm = SP.getInt("mappingMet",75);
                        steps = cms/cm;
                        stepsInt = (int) steps;
                        break;
                    case "Yards":
                        //1 yard = 36 inches
                        inches = progressInt*36;
                        inch = SP.getInt("mappingImp",30);
                        steps = inches/inch;
                        stepsInt = (int) steps;
                        break;
                    case "Miles":
                        //1 mile = 1760 yards
                        float yards = progressInt*1760;
                        //1 yard = 36 inches
                        inches = yards*36;
                        inch = SP.getInt("mappingImp",30);
                        steps = inches/inch;
                        // steps in inches atm
                        stepsInt = (int) steps;
                        break;
                }


                Toast.makeText(AddStepsActivity.this,""+stepsInt, Toast.LENGTH_SHORT).show();
                dbHelper = new DBHelper(AddStepsActivity.this);

                if(dbHelper.updateDayProgress(stepsInt)){
                    Toast.makeText(AddStepsActivity.this, "Goal "+stepsInt, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddStepsActivity.this, "Please set an active goal", Toast.LENGTH_LONG).show();
                    return;
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
