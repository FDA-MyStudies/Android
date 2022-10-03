package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/23/2017.
 */

public class SurveyToSurveyModel extends RealmObject {
    @PrimaryKey
    private String survetTosurveyActivityId;
    private String survetTosurveySourceKey;
    private String survetTosurveyactivityVersion;

    public String getSurvetTosurveyactivityVersion() {
        return survetTosurveyactivityVersion;
    }

    public void setSurvetTosurveyactivityVersion(String survetTosurveyactivityVersion) {
        this.survetTosurveyactivityVersion = survetTosurveyactivityVersion;
    }

    public String getSurvetTosurveyActivityId() {
        return survetTosurveyActivityId;
    }

    public void setSurvetTosurveyActivityId(String survetTosurveyActivityId) {
        this.survetTosurveyActivityId = survetTosurveyActivityId;
    }

    public String getSurvetTosurveySourceKey() {
        return survetTosurveySourceKey;
    }

    public void setSurvetTosurveySourceKey(String survetTosurveySourceKey) {
        this.survetTosurveySourceKey = survetTosurveySourceKey;
    }
}
