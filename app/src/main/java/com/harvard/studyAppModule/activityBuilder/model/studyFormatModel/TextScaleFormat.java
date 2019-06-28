package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class TextScaleFormat extends Format {
    private boolean vertical;
    private ArrayList<ScaleTextChoice> textChoices;


    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public ArrayList<ScaleTextChoice> getTextChoices() {
        return textChoices;
    }

    public void setTextChoices(ArrayList<ScaleTextChoice> textChoices) {
        this.textChoices = textChoices;
    }
}
