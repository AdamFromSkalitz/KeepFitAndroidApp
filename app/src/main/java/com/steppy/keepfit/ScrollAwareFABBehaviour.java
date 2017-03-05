package com.steppy.keepfit;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

public class ScrollAwareFABBehaviour extends CoordinatorLayout.Behavior<FloatingActionMenu> {
    private static final String TAG = "ScrollAwareFABBehavior";
    public ScrollAwareFABBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    public boolean onStartNestedScroll(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu child, View dependency) {
        if(dependency instanceof RecyclerView)
            return true;

        return false;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               com.github.clans.fab.FloatingActionMenu child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // TODO Auto-generated method stub
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
           // child.hide();
            child.hideMenu(true);
            //child.setAnimation();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.showMenu(true);
        }
    }
}