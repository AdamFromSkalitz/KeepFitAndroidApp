package com.steppy.keepfit;

import java.io.Serializable;

/**
 * Created by Turkleton's on 03/02/2017.
 */


public class Goal implements Serializable {
    private String name;
    private float steps;
    private boolean active;

    public Goal(String name, float steps, boolean active) {
        this.name = name;
        this.steps = steps;
        this.active = active;
    }

    public String getName(){
        return name;
    }
    public float getSteps(){
        return steps;
    }
    public boolean isActive(){
        return active;
    }

    public void setName(String Name){
        name = Name;
    }
    public void setSteps(float Steps){
        steps=Steps;
    }
    public void makeActive(boolean Active){
        active = active;
    }

}
