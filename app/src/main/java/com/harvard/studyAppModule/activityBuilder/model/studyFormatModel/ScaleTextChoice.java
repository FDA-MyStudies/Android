package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ScaleTextChoice {
    private String text;
    private String value;
    @SerializedName("detail text")
    private String detailtext;
    private String exclusive;

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

    public String getDetailtext() {
        return detailtext;
    }

    public void setDetailtext(String detailtext) {
        this.detailtext = detailtext;
    }

    public String getExclusive() {
        return exclusive;
    }

    public void setExclusive(String exclusive) {
        this.exclusive = exclusive;
    }
}
