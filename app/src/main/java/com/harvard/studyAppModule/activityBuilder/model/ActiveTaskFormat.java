package com.harvard.studyAppModule.activityBuilder.model;

/**
 * Created by Naveen Raj on 03/20/2017.
 */

public class ActiveTaskFormat {
    private int initialSpan;
    private int minimumSpan;
    private int maximumSpan;
    private int playSpeed;
    private int maximumTests;
    private int maximumConsecutiveFailures;
    private String customTargetImage;
    private String customTargetPluralName;
    private boolean requireReversal;
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getInitialSpan() {
        return initialSpan;
    }

    public void setInitialSpan(int initialSpan) {
        this.initialSpan = initialSpan;
    }

    public int getMinimumSpan() {
        return minimumSpan;
    }

    public void setMinimumSpan(int minimumSpan) {
        this.minimumSpan = minimumSpan;
    }

    public int getMaximumSpan() {
        return maximumSpan;
    }

    public void setMaximumSpan(int maximumSpan) {
        this.maximumSpan = maximumSpan;
    }

    public int getPlaySpeed() {
        return playSpeed;
    }

    public void setPlaySpeed(int playSpeed) {
        this.playSpeed = playSpeed;
    }

    public int getMaximumTests() {
        return maximumTests;
    }

    public void setMaximumTests(int maximumTests) {
        this.maximumTests = maximumTests;
    }

    public int getMaximumConsecutiveFailures() {
        return maximumConsecutiveFailures;
    }

    public void setMaximumConsecutiveFailures(int maximumConsecutiveFailures) {
        this.maximumConsecutiveFailures = maximumConsecutiveFailures;
    }

    public String getCustomTargetImage() {
        return customTargetImage;
    }

    public void setCustomTargetImage(String customTargetImage) {
        this.customTargetImage = customTargetImage;
    }

    public String getCustomTargetPluralName() {
        return customTargetPluralName;
    }

    public void setCustomTargetPluralName(String customTargetPluralName) {
        this.customTargetPluralName = customTargetPluralName;
    }

    public boolean isRequireReversal() {
        return requireReversal;
    }

    public void setRequireReversal(boolean requireReversal) {
        this.requireReversal = requireReversal;
    }
}
