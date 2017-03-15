package com.steppy.keepfit;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Turkleton's on 23/02/2017.
 */

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Goal> itemGoalList;
    private DBHelper dbhelper;
    private ArrayList<Integer> colors;
    //public String[] message = {"Goal1","Goal2", "Goal3","Goal4","Goal5","Goal6","Goal7"};

    public GridAdapter(Context context,ArrayList<Goal> goalList){
        mContext=context;
        itemGoalList=goalList;
        dbhelper = new DBHelper(mContext);
        colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView goalNameTV=null;

        if(convertView==null){
            goalNameTV = new TextView(mContext);
            goalNameTV.setLayoutParams(new GridView.LayoutParams(350,350));
            //textView.setPadding(8,8,8,8);
            goalNameTV.setGravity(Gravity.CENTER);

            //Random r = new Random(); colors.get(r.nextInt(colors.size());
            goalNameTV.setBackgroundResource(R.color.colorPrimary);
            //textView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        }else{
            goalNameTV = (TextView) convertView;
        }
        String goalName=itemGoalList.get(position).getName();
        Cursor res = dbhelper.getOldGoal(goalName);
        res.moveToFirst();
        String goalValue = res.getString(res.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_GOALVALUE));
        String goalProgress = res.getString(res.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS));
        String goalPercent = res.getString(res.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PERCENTAGE));
        //convert to units
        goalNameTV.setText(goalName+"\n Goal: "+goalValue+"\n Progress: "+goalProgress+ "\n Percentage: "+goalPercent+"%");
        res.close();

        return goalNameTV;
    }
}
