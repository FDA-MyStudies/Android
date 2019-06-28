package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

/**
 * Created by Rohit on 2/23/2017.
 */

public class TextFormat extends Format {
    private int maxLength;
    private String validationRegex;
    private String invalidMessage;
    private boolean multipleLines;
    private String placeholder;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    public boolean isMultipleLines() {
        return multipleLines;
    }

    public void setMultipleLines(boolean multipleLines) {
        this.multipleLines = multipleLines;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
