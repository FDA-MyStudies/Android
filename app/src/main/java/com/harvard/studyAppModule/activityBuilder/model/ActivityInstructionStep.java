package com.harvard.studyAppModule.activityBuilder.model;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ActivityInstructionStep extends ActivityStep {
    private String type;
    private ActivityResult result;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public ActivityResult getResult() {
        return result;
    }

    public void setResult(ActivityResult result) {
        this.result = result;
    }
}
