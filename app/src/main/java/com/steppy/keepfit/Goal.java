package com.steppy.keepfit;

import java.io.Serializable;

/**
 * Created by Turkleton's on 03/02/2017.
 */


public class Goal implements Serializable {
    private String name;
    private int steps;
    private boolean active;

    public Goal(String name, int steps, boolean active) {
        this.name = name;
        this.steps = steps;
        this.active = active;
    }

    public String getName(){
        return name;
    }
    public int getSteps(){
        return steps;
    }
    public boolean isActive(){
        return active;
    }

}
