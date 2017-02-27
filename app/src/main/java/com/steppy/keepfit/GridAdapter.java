package com.steppy.keepfit;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Turkleton's on 23/02/2017.
 */

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Goal> itemGoalList;
    //public String[] message = {"Goal1","Goal2", "Goal3","Goal4","Goal5","Goal6","Goal7"};

    public GridAdapter(Context c,ArrayList<Goal> goalList){
        mContext=c;
        itemGoalList=goalList;
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
        TextView textView=null;

        if(convertView==null){
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(350,350));
            //textView.setPadding(8,8,8,8);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.color.colorPrimary);
            //textView.setTextColor(ContextCompat.getColor(mContext,R.color.black));

        }else{
            textView = (TextView) convertView;
        }
        textView.setText(itemGoalList.get(position).getName());
        return textView;
    }
}
