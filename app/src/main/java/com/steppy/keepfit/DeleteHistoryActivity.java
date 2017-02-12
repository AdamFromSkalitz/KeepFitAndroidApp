package com.steppy.keepfit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DeleteHistoryActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_history);

        dbHelper = new DBHelper(this);
        Button del = (Button) findViewById(R.id.buttonDel);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Toast.makeText(DeleteHistoryActivity.this,"yes",Toast.LENGTH_LONG).show();
                                dbHelper.deleteAllOldGoals();
                                dbHelper.close();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(DeleteHistoryActivity.this,"no",Toast.LENGTH_LONG).show();
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteHistoryActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
}
