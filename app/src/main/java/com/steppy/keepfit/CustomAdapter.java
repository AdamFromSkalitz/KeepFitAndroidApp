package com.steppy.keepfit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Turkleton's on 03/02/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Goal> itemGoalList;

    public CustomAdapter(Context context, ArrayList<Goal> modelList) {
        this.context = context;
        this.itemGoalList = modelList;
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
            TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
            ImageView imgRemove = (ImageView) convertView.findViewById(R.id.imgRemove);
            Goal m = itemGoalList.get(position);
            tvName.setText(m.getName());
            // click listiner for remove button
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemGoalList.remove(position);
                    //remove from internal storage
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}