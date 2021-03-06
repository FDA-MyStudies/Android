package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 04/06/2017.
 */

public class ActivitiesWS extends RealmObject {
    private String startTime;

    private String activityId;

    private String title;

    private String lastModified;

    private Frequency frequency;

    private String endTime;

    private String type;

    private String state;

    private String status;

    private boolean branching;

    private String activityVersion;

    private String schedulingType;

    private String lastModifiedDate;

    private SchedulingAnchorDate anchorDate;

    private String anchorDateVersion;

    private String anchorDatecreatedDate;

    public String getAnchorDateVersion() {
        return anchorDateVersion;
    }

    public void setAnchorDateVersion(String anchorDateVersion) {
        this.anchorDateVersion = anchorDateVersion;
    }

    public String getAnchorDatecreatedDate() {
        return anchorDatecreatedDate;
    }

    public void setAnchorDatecreatedDate(String anchorDatecreatedDate) {
        this.anchorDatecreatedDate = anchorDatecreatedDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public SchedulingAnchorDate getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(SchedulingAnchorDate anchorDate) {
        this.anchorDate = anchorDate;
    }

    public String getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(String schedulingType) {
        this.schedulingType = schedulingType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isBranching() {
        return branching;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }

    public boolean getBranching() {
        return branching;
    }

    public void setBranching(boolean branching) {
        this.branching = branching;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
