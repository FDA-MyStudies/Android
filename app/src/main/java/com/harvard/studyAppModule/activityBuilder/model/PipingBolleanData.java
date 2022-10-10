package com.harvard.studyAppModule.activityBuilder.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/23/2017.
 */

public class PipingBolleanData extends RealmObject {


    @PrimaryKey
    private String activityId;
    private boolean isPiping;
    private String Operator;
    private String key;
    private String value;
    private String pipingSnippit;
    private String sourceQuestionKey;
    private String destinationKey;
    private String activityVersion;
    private String resultType;

    public boolean getIsPiping() {
        return isPiping;
    }

    public void setIsPiping(boolean isPiping) {
        this.isPiping = isPiping;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPipingSnippit() {
        return pipingSnippit;
    }

    public void setPipingSnippit(String pipingSnippit) {
        this.pipingSnippit = pipingSnippit;
    }

    public String getSourceQuestionKey() {
        return sourceQuestionKey;
    }

    public void setSourceQuestionKey(String sourceQuestionKey) {
        this.sourceQuestionKey = sourceQuestionKey;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getDestinationKey() {
        return destinationKey;
    }

    public void setDestinationKey(String destinationKey) {
        this.destinationKey = destinationKey;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
