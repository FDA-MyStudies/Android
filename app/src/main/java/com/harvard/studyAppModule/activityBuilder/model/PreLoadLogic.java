package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

public class PreLoadLogic extends RealmObject implements Serializable {
    private String value;
    private String operator;
    private String activityid;
    private String activityVersion;
    private String destinationStepKey;
    private RealmList<DestinationStep> destinationStep;

    public String getDestinationStepKey() {
        return destinationStepKey;
    }

    public void setDestinationStepKey(String destinationStepKey) {
        this.destinationStepKey = destinationStepKey;
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
        return activityid;
    }

    public void setActivityId(String activityId) {
        this.activityid = activityId;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }


    public RealmList<DestinationStep> getDestinationStep() {
        return destinationStep;
    }

    public void setDestinationStep(RealmList<DestinationStep> destinationStep) {
        this.destinationStep = destinationStep;
    }


}
