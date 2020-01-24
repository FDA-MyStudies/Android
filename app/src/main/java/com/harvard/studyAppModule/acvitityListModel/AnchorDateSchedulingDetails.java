package com.harvard.studyAppModule.acvitityListModel;

import com.harvard.userModule.webserviceModel.Activities;

public class AnchorDateSchedulingDetails {
    private String studyId;

    private String schedulingType;

    private String sourceActivityId;

    private String targetActivityId;

    private String sourceKey;

    private String sourceFormKey;

    private String propertyId;

    private String externalPropertyId;

    private String dateOfEntryId;

    private String dateOfEntry;

    private String version;

    private String currentStatus;

    private String startDate;

    private String endDate;

    private String lastUpdatedDate;

    private Activities activities;

    public Activities getActivities() {
        return activities;
    }

    public void setActivities(Activities activities) {
        this.activities = activities;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDateOfEntry() {
        return dateOfEntry;
    }

    public void setDateOfEntry(String dateOfEntry) {
        this.dateOfEntry = dateOfEntry;
    }

    public String getExternalPropertyId() {
        return externalPropertyId;
    }

    public void setExternalPropertyId(String externalPropertyId) {
        this.externalPropertyId = externalPropertyId;
    }

    public String getDateOfEntryId() {
        return dateOfEntryId;
    }

    public void setDateOfEntryId(String dateOfEntryId) {
        this.dateOfEntryId = dateOfEntryId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getSourceFormKey() {
        return sourceFormKey;
    }

    public void setSourceFormKey(String sourceFormKey) {
        this.sourceFormKey = sourceFormKey;
    }

    private String activityState;

    private String anchorDate;

    private String participantId;

    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetActivityId() {
        return targetActivityId;
    }

    public void setTargetActivityId(String targetActivityId) {
        this.targetActivityId = targetActivityId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getSourceActivityId() {
        return sourceActivityId;
    }

    public void setSourceActivityId(String sourceActivityId) {
        this.sourceActivityId = sourceActivityId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getActivityState() {
        return activityState;
    }

    public void setActivityState(String activityState) {
        this.activityState = activityState;
    }

    public String getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(String anchorDate) {
        this.anchorDate = anchorDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(String schedulingType) {
        this.schedulingType = schedulingType;
    }
}
