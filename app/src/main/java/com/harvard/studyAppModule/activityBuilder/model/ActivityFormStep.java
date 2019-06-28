package com.harvard.studyAppModule.activityBuilder.model;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ActivityFormStep extends ActivityResult {
    private String type;
    private String resultType;
    private boolean repeatable;
    private String repeatableText;
    private ArrayList<ActivityQuestionStep> steps;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getRepeatableText() {
        return repeatableText;
    }

    public void setRepeatableText(String repeatableText) {
        this.repeatableText = repeatableText;
    }

    public ArrayList<ActivityQuestionStep> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<ActivityQuestionStep> steps) {
        this.steps = steps;
    }
}
