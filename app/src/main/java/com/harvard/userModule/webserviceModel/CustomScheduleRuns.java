package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

public class CustomScheduleRuns extends RealmObject {
//    private String activityStartDate;
    private String runStartDate;

//    private String activityEndDate;
    private String runEndDate;

    public String getActivityStartDate() {
        return runStartDate;
    }

    public void setActivityStartDate(String activityStartDate) {
        this.runStartDate = activityStartDate;
    }

    public String getActivityEndDate() {
        return runEndDate;
    }

    public void setActivityEndDate(String activityEndDate) {
        this.runEndDate = activityEndDate;
    }
}
