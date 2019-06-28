package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ScaleImageChoice {
    private String image;
    private String text;
    private String value;
    @SerializedName("selected image")
    private String selectedImage;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(String selectedImage) {
        this.selectedImage = selectedImage;
    }
}
