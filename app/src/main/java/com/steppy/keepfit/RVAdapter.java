package com.steppy.keepfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Turkleton's on 01/03/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.GoalViewHolder> {
    List<Goal> goals;
    Context context;
    DBHelper dbHelper;
    //Goal goal;

    public RVAdapter(List<Goal> goals, Context context) {
        this.goals = goals;
        this.context = context;
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView goalName;
        TextView goalUnits;
        TextView goalValue;
        ImageView active;
        ImageView edit;
        ImageView remove;

        public GoalViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            goalName = (TextView) itemView.findViewById(R.id.goal_name);
            goalUnits = (TextView) itemView.findViewById(R.id.goal_units);
            goalValue = (TextView) itemView.findViewById(R.id.goal_value);
            active = (ImageView) itemView.findViewById(R.id.imgActive);
            edit = (ImageView) itemView.findViewById(R.id.imgEdit);
            remove = (ImageView) itemView.findViewById(R.id.imgRemove);
        }
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        GoalViewHolder gvh = new GoalViewHolder(v);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder personViewHolder, final int i) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        boolean editGoals = SP.getBoolean("enableGoalEdit",true);

        ImageView editView = (ImageView) personViewHolder.cv.findViewById(R.id.imgEdit);

        if(editGoals) {
            editView.setVisibility(View.VISIBLE);
        }else{
            editView.setVisibility(View.GONE);
            }
        if (goals.get(i).isActive()) {
            personViewHolder.active.setBackgroundResource(R.drawable.pause);
        } else {
            personViewHolder.active.setBackgroundResource(R.drawable.play);
        }

        personViewHolder.goalName.setText(goals.get(i).getName());
        personViewHolder.goalValue.setText(Float.toString(goals.get(i).getSteps()));
        personViewHolder.goalUnits.setText(goals.get(i).getUnits());

        personViewHolder.active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(context);
                Cursor cur = dbHelper.getGoal(goals.get(i).getName());
                cur.moveToFirst();
                //introduce bug here, if there are two goals with same name then no matter which one is picked here
                //we will update only the first one db found.
                String active = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_ACTIVE));
                boolean activeBool = Boolean.parseBoolean(active);
                //not allowed to access these variables inside if statement ?!?!?!?!?!?!
                int id = cur.getInt(cur.getColumnIndex(DBHelper.COLUMN_ID));
                String name = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_NAME));
                String goal = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                String date = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_DATE));
                String units = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_UNITS));
                cur.close();

                if (active.equals("true")) {
                    //if active, make unactive
                    goals.get(i).makeActive(false);
                    dbHelper.updateGoal(id, name, goal, "false", date, units);
                    personViewHolder.active.setBackgroundResource(R.drawable.play);
                } else if (active.equals("false")) {
                    //make all goals inactive
                    final Cursor cursor = dbHelper.getAllGoals();
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        int idTemp = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
                        String nameTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                        String goalTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                        String dateTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
                        String unitsTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_UNITS));
                        dbHelper.updateGoal(idTemp, nameTemp, goalTemp, "false", dateTemp, unitsTemp);
                        cursor.moveToNext();
                        personViewHolder.active.setBackgroundResource(R.drawable.play);
                    }
                    for (Goal g : goals) {
                        g.makeActive(false);
                    }
                    //if unactive, make active
                    goals.get(i).makeActive(true);
                    personViewHolder.active.setBackgroundResource(R.drawable.pause);
                    dbHelper.updateGoal(id, name, goal, "true", date, units);
                    cursor.close();
                    //imgActive.setBackgroundColor(Color.parseColor("#00704a"));
                }
                dbHelper.close();
                notifyDataSetChanged();
                notifyItemChanged(i);


            }
        });

        personViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View dialogView = inflater.inflate(R.layout.fragment_edit_steps_dialog, null);

                dbHelper = new DBHelper(context);
                final Cursor result = dbHelper.getGoal(goals.get(i).getName());
                result.moveToNext();
                final int idGoal = Integer.parseInt(result.getString(result.getColumnIndex(DBHelper.COLUMN_ID)));
                String name = result.getString(result.getColumnIndex(DBHelper.COLUMN_NAME));
                String value = result.getString(result.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                final String date = result.getString(result.getColumnIndex(DBHelper.COLUMN_DATE));
                final String active = result.getString(result.getColumnIndex(DBHelper.COLUMN_ACTIVE));
                final String units = result.getString(result.getColumnIndex(DBHelper.COLUMN_UNITS));
                final TextView goalName= (TextView)dialogView.findViewById(R.id.nameText);
                final TextView goalValue=(TextView)dialogView.findViewById(R.id.goalsText);
                final Spinner unitsSpin=(Spinner)dialogView.findViewById(R.id.spinnerUnits);

                goalName.setText(name);
                goalValue.setText(value);
                switch (units){
                    case "Steps":
                        unitsSpin.setSelection(0);
                        break;
                    case "Kilometres":
                        unitsSpin.setSelection(1);
                        break;
                    case "Miles":
                        unitsSpin.setSelection(2);
                }

                result.close();
                // Add action buttons
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Goal goal = goals.get(i);
                        final String nameD = goalName.getText().toString();
                        final String valueD = goalValue.getText().toString();
                        final String unitsD = unitsSpin.getSelectedItem().toString();
                        goal.setUnits(unitsD);
                        goal.setName(nameD);
                        goal.setSteps(Float.parseFloat(valueD));
                        dbHelper.updateGoal(idGoal,nameD,valueD,active,date,unitsD);
                        dbHelper.close();
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setView(dialogView);

                Goal goal = goals.get(i);

                dbHelper = new DBHelper(context);
                Cursor res = dbHelper.getActiveGoal();
                res.moveToFirst();
                String goalActiveName = res.getString(res.getColumnIndex(DBHelper.COLUMN_NAME));
                dbHelper.close();
                res.close();
                if (!goalActiveName.equals(goal.getName())) {
                    builder.show();
                    //dl.show();
                }
            }
        });
        personViewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goal goal = goals.get(i);
                dbHelper = new DBHelper(context);
                Cursor res = dbHelper.getActiveGoal();
                String goalActiveName = "";
                res.moveToFirst();
                try {
                    goalActiveName = res.getString(res.getColumnIndex(DBHelper.COLUMN_NAME));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!goalActiveName.equals(goal.getName())) {
                    //if not active goal
                    //remove from internal storage
                    goals.remove(i);
                    dbHelper.deleteGoal(goal.getName());
                }
                notifyDataSetChanged();
                dbHelper.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}
