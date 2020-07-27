package com.harvard.studyAppModule.consent.consentLARStep;

import com.harvard.studyAppModule.consent.ConsentSharingStepCustom.SingleChoiceSharingStepBody;

import org.researchstack.backbone.step.QuestionStep;

public class ConsentLARStep extends QuestionStep {

    public ConsentLARStep(String identifier) {
        super(identifier);
        setOptional(false);
    }

    @Override
    public Class getStepBodyClass() {
        return ConsentLARStepBody.class;
    }
}