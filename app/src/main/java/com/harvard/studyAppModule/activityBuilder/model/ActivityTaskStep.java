package com.harvard.studyAppModule.activityBuilder.model;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ActivityTaskStep extends ActivityStep {
    private String type;
    private String resultType;
    private String key;
    private String text;
    private ArrayList<Integer> options;
    private Format format;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Integer> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Integer> options) {
        this.options = options;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
}
