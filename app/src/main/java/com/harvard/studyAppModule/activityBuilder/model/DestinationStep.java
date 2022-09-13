package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class DestinationStep extends RealmObject implements Serializable {
    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public String getDestinationStepKey() {
        return destinationStepKey;
    }

    public void setDestinationStepKey(String destinationStepKey) {
        this.destinationStepKey = destinationStepKey;
    }

    private boolean condition;
    private String destinationStepKey;
}
