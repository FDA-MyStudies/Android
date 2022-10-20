package com.harvard.studyAppModule.activityBuilder.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/23/2017.
 */

public class GetPipingChoices implements Serializable {
    @PrimaryKey
    private String value;
    private String text;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
