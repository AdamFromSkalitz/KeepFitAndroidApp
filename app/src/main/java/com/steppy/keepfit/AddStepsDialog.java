package com.steppy.keepfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Turkleton's on 01/03/2017.
 */

public class AddStepsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //final View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean testMode = SP.getBoolean("enableTest", false);
        if(testMode) {
            EditText steps = (EditText) getActivity().findViewById(R.id.stepsText); //new EditText(this);
            steps.setVisibility(View.VISIBLE);
            steps.setHint("Steps Taken");


            final Button dateBut = (Button) getActivity().findViewById(R.id.addDateBut);//new Button(this);
            dateBut.setVisibility(View.VISIBLE);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_add_steps_dialog, null))
                // Add action buttons
                .setPositiveButton("Save Goal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddStepsDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
