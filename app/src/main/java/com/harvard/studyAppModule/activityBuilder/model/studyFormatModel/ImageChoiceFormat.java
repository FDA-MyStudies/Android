package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ImageChoiceFormat extends Format {
    private ArrayList<ScaleTextChoice> imageChoices;

    public ArrayList<ScaleTextChoice> getImageChoices() {
        return imageChoices;
    }

    public void setImageChoices(ArrayList<ScaleTextChoice> imageChoices) {
        this.imageChoices = imageChoices;
    }
}
