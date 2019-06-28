package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

/**
 * Created by Rohit on 2/23/2017.
 */

public class HeightFormat extends Format {
    private int measurementSystem;
    private String placeholder;

    public int getMeasurementSystem() {
        return measurementSystem;
    }

    public void setMeasurementSystem(int measurementSystem) {
        this.measurementSystem = measurementSystem;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
