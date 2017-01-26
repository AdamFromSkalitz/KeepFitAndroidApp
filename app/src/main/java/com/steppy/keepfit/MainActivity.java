package com.steppy.keepfit;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;


import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {
    private BottomBar bottomBar;
    private Button but;

    Calendar c = Calendar.getInstance();
    int yearr;
    int monthh;
    int day;
    static final int dialog_id = 0;


    public void showDialogOnButtonClick(){
        but = (Button) findViewById(R.id.date);

        but.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(dialog_id);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id ){
        if(id==dialog_id){
            return new DatePickerDialog(this,dpickerListener,yearr,monthh,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yearr = year;
            monthh=month;
            day = dayOfMonth;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showDialogOnButtonClick();

        View logo = getLayoutInflater().inflate(R.layout.datepicker, null);
        toolbar.addView(logo);
        getSupportActionBar().setTitle(" ");


        final Calendar cal = Calendar.getInstance();
        yearr = cal.get(Calendar.YEAR);
        monthh = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(MainActivity.this,"month"+monthh,Toast.LENGTH_LONG).show();

        but = (Button) findViewById(R.id.date);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
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
    public void showDatePickerDialog(View v){



    }


//    class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
//
//        public StartDatePicker(){}
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // TODO Auto-generated method stub
//            // Use the current date as the default date in the picker
//            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, this, startYear, startMonth, startDay);
//            return dialog;
//
//        }
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            // TODO Auto-generated method stub
//            // Do something with the date chosen by the user
//            startYear = year;
//            startMonth = monthOfYear;
//            startDay = dayOfMonth;
//            but.setText(startMonth);
//        }
//    }

//    public void thing(){
//        Calendar now = Calendar.getInstance();
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                MainActivity.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//        dpd.show(getFragmentManager(), "Datepickerdialog");
//    }

}
