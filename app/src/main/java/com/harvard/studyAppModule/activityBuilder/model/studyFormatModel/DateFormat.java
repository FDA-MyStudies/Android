package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import java.util.Date;

/**
 * Created by Rohit on 2/23/2017.
 */

public class DateFormat extends Format {
    private int style;
    private Date minDate;
    private Date maxDate;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

}
