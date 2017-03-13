package com.steppy.keepfit;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by Turkleton's on 13/03/2017.
 */

public class undoGoalDeleteListener implements View.OnClickListener {

    private Goal goal;
    private Context context;

    private int progress;
    private String percent;
    private String date;
    private RVAdapter rvAdapter;
    private List<Goal> goals;

    public undoGoalDeleteListener(Goal goal, Context context,int progress, String percent, String date, RVAdapter rvAdapter, List<Goal> goals){
        this.goal = goal;
        this.context = context;
        this.progress = progress;
        this.percent = percent;
        this.date = date;
        this.rvAdapter=rvAdapter;
        this.goals=goals;
    }

    @Override
    public void onClick(View v) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.insertGoal(goal.getName(),(int)goal.getSteps(),Boolean.toString(goal.isActive()),date,goal.getUnits());
        goals.add(goal);
        rvAdapter.notifyDataSetChanged();
    }
}
