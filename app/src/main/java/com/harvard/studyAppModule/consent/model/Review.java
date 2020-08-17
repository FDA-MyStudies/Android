package com.harvard.studyAppModule.consent.model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Review extends RealmObject{
    private String reasonForConsent;

    private String reviewHTML;

    private String signatureTitle;

    private String consentByLAR;

    private String additionalSignature;

    private RealmList<String> signatures;

    public String getConsentByLAR() {
        return consentByLAR;
    }

    public void setConsentByLAR(String consentByLAR) {
        this.consentByLAR = consentByLAR;
    }

    public String getReasonForConsent() {
        return reasonForConsent;
    }

    public void setReasonForConsent(String reasonForConsent) {
        this.reasonForConsent = reasonForConsent;
    }

    public String getAdditionalSignature() {
        return additionalSignature;
    }

    public void setAdditionalSignature(String additionalSignature) {
        this.additionalSignature = additionalSignature;
    }

    public RealmList<String> getSignatures() {
        return signatures;
    }

    public void setSignatures(RealmList<String> signatures) {
        this.signatures = signatures;
    }

    public String getSignatureContent() {
        return reviewHTML;
    }

    public void setSignatureContent(String signatureContent) {
        this.reviewHTML = signatureContent;
    }

    public String getSignatureTitle() {
        return signatureTitle;
    }

    public void setSignatureTitle(String signatureTitle) {
        this.signatureTitle = signatureTitle;
    }
}
