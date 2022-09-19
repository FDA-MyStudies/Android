package com.harvard.studyAppModule.activityBuilder.model.serviceModel;


import com.harvard.studyAppModule.activityBuilder.model.Format;
import com.harvard.studyAppModule.activityBuilder.model.Piping;
import com.harvard.studyAppModule.activityBuilder.model.PreLoadLogic;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class Steps extends RealmObject {
    private String type;
    private String groupId;


    private String sourceQuestionKey;
    private String resultType;
    private String key;
    private String title;
    private String text;
    private boolean skippable;

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    private boolean isHidden;
    private String groupName;
    private boolean repeatable;
    private String repeatableText;
    private RealmList<Destinations> destinations;
    private String healthDataKey;
    private Format format;
    private RealmList<Steps> steps;
    private boolean defaultVisibility;



    private boolean isPiping;
    private PreLoadLogic preLoadLogic;
    private boolean isPiping;

    public boolean isPiping() {
        return isPiping;
    }

    public void setPiping(boolean piping) {
        isPiping = piping;
    }

    private Piping piping;

    public boolean isPiping() {
        return isPiping;
    }

    public void setPiping(boolean piping) {
        isPiping = piping;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isDefaultVisibility() {
        return defaultVisibility;
    }

    public void setDefaultVisibility(boolean defaultVisibility) {
        this.defaultVisibility = defaultVisibility;
    }

    public PreLoadLogic getPreLoadLogic() {
        return preLoadLogic;
    }

    public void setPreLoadLogic(PreLoadLogic preLoadLogic) {
        this.preLoadLogic = preLoadLogic;
    }

    public Piping getPiping() {
        return piping;
    }

    public void setPiping(Piping piping) {
        this.piping = piping;
    }



    public RealmList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<Steps> steps) {
        this.steps = steps;
    }

    public String getRepeatableText() {
        return repeatableText;
    }

    public void setRepeatableText(String repeatableText) {
        this.repeatableText = repeatableText;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getType() {
        return type;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public boolean isSkippable() {
        return skippable;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    public RealmList<Destinations> getDestinations() {
        return destinations;
    }

    public void setDestinations(RealmList<Destinations> destinations) {
        this.destinations = destinations;
    }
    public String getSourceQuestionKey() {
        return sourceQuestionKey;
    }

    public void setSourceQuestionKey(String sourceQuestionKey) {
        this.sourceQuestionKey = sourceQuestionKey;
    }

    public String getHealthDataKey() {
        return healthDataKey;
    }

    public void setHealthDataKey(String healthDataKey) {
        this.healthDataKey = healthDataKey;
    }
}
