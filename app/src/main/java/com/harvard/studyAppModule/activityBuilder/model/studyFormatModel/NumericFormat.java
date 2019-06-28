package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

/**
 * Created by Rohit on 2/23/2017.
 */

public class NumericFormat extends Format {
    private int style;
    private int minValue;
    private int maxValue;
    private String placeholder;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
