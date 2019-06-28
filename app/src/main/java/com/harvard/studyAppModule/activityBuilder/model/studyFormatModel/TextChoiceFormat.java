package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class TextChoiceFormat extends Format{
    private ArrayList<String> textChoices;
    private int selectionStyle;

    public ArrayList<String> getTextChoices() {
        return textChoices;
    }

    public void setTextChoices(ArrayList<String> textChoices) {
        this.textChoices = textChoices;
    }

    public int getSelectionStyle() {
        return selectionStyle;
    }

    public void setSelectionStyle(int selectionStyle) {
        this.selectionStyle = selectionStyle;
    }
}
