package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

public class PreLoadLogic extends RealmObject implements Serializable {
    private String value;
    private String operator;
    private String activityId;
    private String activityVersion;
    private boolean isPiping;
    private RealmList<DestinationStep> destinationStep;
    private String destinationTrueStepKey;
    private String destinationFalseStepKey;
    public String getDestinationTrueStepKey() {
        return destinationTrueStepKey;
    }

    public void setDestinationTrueStepKey(String destinationTrueStepKey) {
        this.destinationTrueStepKey = destinationTrueStepKey;
    }

    public String getDestinationFalseStepKey() {
        return destinationFalseStepKey;
    }

    public void setDestinationFalseStepKey(String destinationFalseStepKey) {
        this.destinationFalseStepKey = destinationFalseStepKey;
    }



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }

    public boolean isPiping() {
        return isPiping;
    }

    public void setPiping(boolean piping) {
        isPiping = piping;
    }

    public RealmList<DestinationStep> getDestinationStep() {
        return destinationStep;
    }

    public void setDestinationStep(RealmList<DestinationStep> destinationStep) {
        this.destinationStep = destinationStep;
    }


}
