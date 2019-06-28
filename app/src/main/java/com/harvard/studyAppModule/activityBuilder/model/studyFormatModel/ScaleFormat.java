package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ScaleFormat extends Format {
    private int step;
    private boolean vertical;
    private String maxDesc;
    private String minDesc;
    private String maxImage;
    private String minImage;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public String getMaxDesc() {
        return maxDesc;
    }

    public void setMaxDesc(String maxDesc) {
        this.maxDesc = maxDesc;
    }

    public String getMinDesc() {
        return minDesc;
    }

    public void setMinDesc(String minDesc) {
        this.minDesc = minDesc;
    }

    public String getMaxImage() {
        return maxImage;
    }

    public void setMaxImage(String maxImage) {
        this.maxImage = maxImage;
    }

    public String getMinImage() {
        return minImage;
    }

    public void setMinImage(String minImage) {
        this.minImage = minImage;
    }
}
