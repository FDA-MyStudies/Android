package com.harvard.studyAppModule.activityBuilder.model.serviceModel;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class ActivityObj extends RealmObject {
    private String type;
    private Info metadata;
    private String surveyId;
    private String studyId;
//    private QuestionnaireConfiguration questionnaireConfiguration;
    private RealmList<Steps> steps;
//    private RealmList<RandomizationSets> randomizationSets;
//    private RealmList<ResourceContext> resourceContext;


    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

//    public void setRandomizationSets(RealmList<RandomizationSets> randomizationSets) {
//        this.randomizationSets = randomizationSets;
//    }
//
//    public RealmList<ResourceContext> getResourceContext() {
//        return resourceContext;
//    }
//
//    public RealmList<RandomizationSets> getRandomizationSets() {
//        return randomizationSets;
//    }
//
//    public void setResourceContext(RealmList<ResourceContext> resourceContext) {
//        this.resourceContext = resourceContext;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Info getMetadata() {
        return metadata;
    }

    public void setMetadata(Info metadata) {
        this.metadata = metadata;
    }

    //    public QuestionnaireConfiguration getQuestionnaireConfiguration() {
//        return questionnaireConfiguration;
//    }
//
//    public void setQuestionnaireConfiguration(QuestionnaireConfiguration questionnaireConfiguration) {
//        this.questionnaireConfiguration = questionnaireConfiguration;
//    }

    public RealmList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<Steps> steps) {
        this.steps = steps;
    }

}
