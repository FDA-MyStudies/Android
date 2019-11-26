package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

public class CustomScheduleRuns extends RealmObject {
    private String activityStartDate;

    private String activityEndDate;

    public String getActivityStartDate() {
        return activityStartDate;
    }

    public void setActivityStartDate(String activityStartDate) {
        this.activityStartDate = activityStartDate;
    }

    public String getActivityEndDate() {
        return activityEndDate;
    }

    public void setActivityEndDate(String activityEndDate) {
        this.activityEndDate = activityEndDate;
    }
}
