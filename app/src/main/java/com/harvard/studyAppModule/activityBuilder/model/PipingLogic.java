package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class PipingLogic extends RealmObject implements Serializable {
    public String getSourceQuestionKey() {
        return sourceQuestionKey;
    }

    public void setSourceQuestionKey(String sourceQuestionKey) {
        this.sourceQuestionKey = sourceQuestionKey;
    }

    public String getPipingSnippet() {
        return pipingSnippet;
    }

    public void setPipingSnippet(String pipingSnippet) {
        this.pipingSnippet = pipingSnippet;
    }

    private String sourceQuestionKey;
    private String pipingSnippet;
    private String activityId;
    private String activityVersion;

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
}
