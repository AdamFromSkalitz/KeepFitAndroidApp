package com.steppy.keepfit;

import java.io.Serializable;

/**
 * Created by Turkleton's on 03/02/2017.
 */


public class Goal implements Serializable {
    private String name;
    private float steps;
    private boolean active;
    private String units;

    public Goal(String name, float steps, boolean active,String units) {
        this.name = name;
        this.steps = steps;
        this.active = active;
        this.units = units;
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
        this.active = Active;
    }

    public void setUnits(String units){
        this.units=units;
    }
    public String getUnits(){
        return units;
    }


}
