package com.steppy.keepfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by Turkleton's on 03/02/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Goal> itemGoalList;
    DBHelper dbHelper;

//    @Override
//    public void add(){
//
//    }
    public CustomAdapter(Context context, ArrayList<Goal> goalList) {
        this.context = context;
        this.itemGoalList = goalList;

    }
    @Override
    public int getCount() {
        return itemGoalList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemGoalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row, null);

            final Goal g = itemGoalList.get(position);

            TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
            String txt = g.getName()+g.isActive();
            tvName.setText(txt);



            final ImageView imgActive = (ImageView) convertView.findViewById(R.id.imgActive);
            //Do the next four lines work?
            if(g.isActive()) {
                imgActive.setBackgroundResource(R.drawable.play);
            }else{
                imgActive.setBackgroundResource(R.drawable.pause);
            }

            imgActive.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    dbHelper = new DBHelper(context);
                    Cursor cur = dbHelper.getGoal(g.getName());
                    cur.moveToFirst();
                    //introduce bug here, if there are two goals with same name then no matter which one is picked here
                    //we will update only the first one db found.
                    String active = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_ACTIVE));
                    boolean activeBool = Boolean.parseBoolean(active);
                    //not allowed to access these variables inside if statement ?!?!?!?!?!?!
                    int id = cur.getInt(cur.getColumnIndex(DBHelper.COLUMN_ID));
                    String name = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_NAME));
                    String goal = cur.getString(cur.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                    String date =  cur.getString(cur.getColumnIndex(DBHelper.COLUMN_DATE));
                    cur.close();

                    if(active.equals("true")) {
                        //if active, make unactive
                        dbHelper.updateGoal(id, name, goal, "false", date);
                        g.makeActive(false);
                        imgActive.setBackgroundResource(R.drawable.pause);
                        //imgActive.setBackgroundColor(Color.parseColor("#e99c9c"));
                    }

                    else if(active.equals("false")){

                        //make all goals inactive
                        final Cursor cursor = dbHelper.getAllGoals();
                        cursor.moveToFirst();
                        while(!cursor.isAfterLast()) {
                            int idTemp = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
                            String nameTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                            String goalTemp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GOALVALUE));
                            String dateTemp =  cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));

                            dbHelper.updateGoal(idTemp, nameTemp, goalTemp, "false", dateTemp);
                            cursor.moveToNext();
                            imgActive.setBackgroundResource(R.drawable.pause);
                        }
                        for (Goal g :itemGoalList){
                            g.makeActive(false);
                        }
                        //if unactive, make active
                        dbHelper.updateGoal(id,name,goal,"true",date);
                        g.makeActive(true);
                        imgActive.setBackgroundResource(R.drawable.play);
                        cursor.close();

                        //imgActive.setBackgroundColor(Color.parseColor("#00704a"));
                    }
                    dbHelper.close();

                }
            });

            ImageView imgEdit = (ImageView) convertView.findViewById(R.id.imgEdit);
            imgEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    dbHelper = new DBHelper(context);
                    Cursor res = dbHelper.getActiveGoal();
                    res.moveToFirst();
                    String goalActiveName = res.getString(res.getColumnIndex(DBHelper.COLUMN_NAME));
                    if (!goalActiveName.equals(g.getName())) {
                        Intent intent = new Intent(context, EditGoalsActivity.class);
                        String goalName = g.getName();
                        intent.putExtra("name", goalName);
                        notifyDataSetChanged();
                        dbHelper.close();

                        context.startActivity(intent);
                    }
                }
            });

            ImageView imgRemove = (ImageView) convertView.findViewById(R.id.imgRemove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper = new DBHelper(context);
                    Cursor res = dbHelper.getActiveGoal();
                    res.moveToFirst();
                    String goalActiveName = res.getString(res.getColumnIndex(DBHelper.COLUMN_NAME));
                    if (!goalActiveName.equals(g.getName())){
                        //if not active goal
                        //remove from internal storage
                        itemGoalList.remove(position);
                        dbHelper.deleteGoal(g.getName());
                    }
                    notifyDataSetChanged();
                    dbHelper.close();
                }
            });
        }
        return convertView;
    }


}