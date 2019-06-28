package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ValuePickerFormat extends Format {
    private ArrayList<String> textChoices;

    public ArrayList<String> getTextChoices() {
        return textChoices;
    }

    public void setTextChoices(ArrayList<String> textChoices) {
        this.textChoices = textChoices;
    }
}
