package com.harvard.studyAppModule.custom;

import com.harvard.studyAppModule.activityBuilder.model.GetPipingChoices;
import com.harvard.studyAppModule.activityBuilder.model.PipingLogic;
import com.harvard.studyAppModule.custom.question.ChoiceText;
import com.harvard.studyAppModule.custom.question.ChoiceTextExclusive;

import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by Naveen Raj on 11/14/2016.
 */
public class QuestionStepCustom extends QuestionStep {
    public AnswerFormatCustom answerFormat;
    public String placeholder;
    private String identifier;
    private boolean addable;
    private String addmoretitle;
    private String TimeIntevalDuration;

    public String key_pipe;
    public boolean isPPing;
    public String pipeValue;
    public String pipeOperator;
    public String pipingSnippet;
    public String pipeSocuceKey;

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String activityId;
    public String destinationStepKey;
    public String activityVersion;
    public String type;
    String type2;
    private String psourceQuestionKey;
    private String ppipingSnippet;
    private String pactivityId;
    private String pactivityVersion;
    String pipingTitle;

    public String getPipingTitle() {
        return pipingTitle;
    }

    public void setPipingTitle(String pipingTitle) {
        this.pipingTitle = pipingTitle;
    }

    public String getPsourceQuestionKey() {
        return psourceQuestionKey;
    }

    public void setPsourceQuestionKey(String psourceQuestionKey) {
        this.psourceQuestionKey = psourceQuestionKey;
    }

    public String getPpipingSnippet() {
        return ppipingSnippet;
    }

    public void setPpipingSnippet(String ppipingSnippet) {
        this.ppipingSnippet = ppipingSnippet;
    }

    public String getPactivityId() {
        return pactivityId;
    }

    public void setPactivityId(String pactivityId) {
        this.pactivityId = pactivityId;
    }

    public String getPactivityVersion() {
        return pactivityVersion;
    }

    public void setPactivityVersion(String pactivityVersion) {
        this.pactivityVersion = pactivityVersion;
    }

    ArrayList<GetPipingChoices> getPipingChoices;

    public ArrayList<GetPipingChoices> getGetPipingChoices() {
        return getPipingChoices;
    }

    public void setGetPipingChoices(ArrayList<GetPipingChoices> getPipingChoices) {
        this.getPipingChoices = getPipingChoices;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getDestinationStepKey() {
        return destinationStepKey;
    }

    public void setDestinationStepKey(String destinationStepKey) {
        this.destinationStepKey = destinationStepKey;
    }


    public String getPipeSocuceKey() {
        return pipeSocuceKey;
    }

    public void setPipeSocuceKey(String pipeSocuceKey) {
        this.pipeSocuceKey = pipeSocuceKey;
    }

    /**
     * Returns a new question step that includes the specified identifier.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     */
    public QuestionStepCustom(String identifier, boolean isPPing, String pipeOperator, String key_pipe,

                              String pipeValue, String pipingSnippet, String pipeSocuceKey, String activityId,
                              String destinationStepKey, String activityVersion , String type,
                              ArrayList<GetPipingChoices> getPipingChoices,
                              String psourceQuestionKey,
                                        String ppipingSnippet,
                                        String pactivityId,
                                        String pactivityVersion,String pipingTitle) {
        super(identifier);
        this.identifier = identifier;
        this.isPPing = isPPing;
        this.pipeOperator = pipeOperator;
        this.key_pipe = key_pipe;
        this.pipeValue = pipeValue;
        this.pipingSnippet = pipingSnippet;
        this.pipeSocuceKey=pipeSocuceKey;
        this.activityId=activityId;
        this. destinationStepKey=destinationStepKey;
        this. activityVersion=activityVersion;
        this.type=type;
        this.getPipingChoices=getPipingChoices;
        this.psourceQuestionKey=psourceQuestionKey;
        this.ppipingSnippet=ppipingSnippet;
        this.pactivityId=pactivityId;
        this.pactivityVersion=pactivityVersion;
        this.pipingTitle = pipingTitle;
     }

    /**
     * Returns a new question step that includes the specified identifier, and title.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     * @param title      A localized string that represents the primary text of the question.
     */
    public QuestionStepCustom(String identifier, String title,boolean isPPing,String pipeOperator,String key_pipe,

                              String pipeValue,String pipingSnippet, String pipeSocuceKey,String activityId,
                              String destinationStepKey,String activityVersion, String type ,String psourceQuestionKey,
                              String ppipingSnippet,
                              String pactivityId,
                              String pactivityVersion,String pipingTitle) {
        super(identifier, title);
        this.identifier = identifier;
        this.isPPing = isPPing;
        this.pipeOperator = pipeOperator;
        this.key_pipe = key_pipe;
        this.pipeValue = pipeValue;
        this.pipingSnippet = pipingSnippet;
        this.pipeSocuceKey=pipeSocuceKey;
        this.activityId=activityId;
        this. destinationStepKey=destinationStepKey;
        this. activityVersion=activityVersion;
        this.type=type;
        this.psourceQuestionKey=psourceQuestionKey;
        this.ppipingSnippet=ppipingSnippet;
        this.pactivityId=pactivityId;
        this.pactivityVersion=pactivityVersion;
        this.pipingTitle = pipingTitle;

    }

    /**
     * Returns a new question step that includes the specified identifier, title, and answer
     * format.
     *
     * @param identifier The identifier of the step (a step identifier should be unique within the
     *                   task).
     * @param title      A localized string that represents the primary text of the question.
     * @param format     The format in which the answer is expected.
     */
    public QuestionStepCustom(String identifier, String title, AnswerFormatCustom format, boolean isPPing, String pipeOperator, String key_pipe,
                              String pipeValue, String pipingSnippet, String pipeSocuceKey, String activityId,
                              String destinationStepKey, String activityVersion, String type,
                              ArrayList<GetPipingChoices>getPipingChoices,String type2,String psourceQuestionKey,
                              String ppipingSnippet,
                              String pactivityId,
                              String pactivityVersion,String pipingTitle

    ) {
        super(identifier, title);
        this.answerFormat = format;
        this.identifier = identifier;
        this.isPPing = isPPing;
        this.pipeOperator = pipeOperator;
        this.key_pipe = key_pipe;
        this.pipeValue = pipeValue;
        this.pipingSnippet = pipingSnippet;
        this.pipeSocuceKey=pipeSocuceKey;
        this.activityId=activityId;
        this. destinationStepKey=destinationStepKey;
        this. activityVersion=activityVersion;
        this.type=type;
        this.getPipingChoices=getPipingChoices;
        this.type2 =type2;
        this.psourceQuestionKey=psourceQuestionKey;
        this.ppipingSnippet=ppipingSnippet;
        this.pactivityId=pactivityId;
        this.pactivityVersion=pactivityVersion;
        this.pipingTitle = pipingTitle;

    }

    public void setidentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns a special {@link org.researchstack.backbone.ui.step.layout.StepLayout} that is used
     * for all question steps.
     * <p>
     * This step layout uses the {@link #getStepBodyClass()} to fill in the user interaction portion
     * of the layout's UI.
     *
     * @return the StepLayout to be used for general QuestionSteps
     */
    @Override
    public Class getStepLayoutClass() {
        return SurveyStepLayoutCustom.class;
    }

    /**
     * Returns a subclass of {@link org.researchstack.backbone.ui.step.body.StepBody} responsible
     * for creating the ui for answering the question, base on the AnswerFormat.
     * <p>
     * This class is used by {@link SurveyStepLayout} to create the part of the layout where the
     * user answers the question. For example, a StepBody for a simple text question would be
     * responsible for creating an EditText for the SurveyStepLayout to place inside of its layout.
     * <p>
     * Override this method with your own StepBody implementation if you create a custom
     * QuestionStep.
     *
     * @return the StepBody implementation for this question step.
     */
    public Class<?> getStepBodyClass() {
        return answerFormat.getQuestionType().getStepBodyClass();
    }

    /**
     * Returns the format of the answer.
     * <p>
     * For example, the answer format might include the type of data to collect, the constraints to
     * place on the answer, or a list of available choices (in the case of single or multiple select
     * questions). It also provides the default {@link org.researchstack.backbone.ui.step.body.StepBody}
     * for questions of its type.
     *
     * @return the answer format for this question step
     */
    public AnswerFormatCustom getAnswerFormat1() {
        return answerFormat;
    }

    /**
     * Sets the answer format for this question step.
     *
     * @param answerFormat the answer format for this question step
     * @see #getAnswerFormat()
     */
    public void setAnswerFormat1(AnswerFormatCustom answerFormat) {
        this.answerFormat = answerFormat;
    }

    /**
     * Returns a localized string that represents the placeholder text displayed before an answer
     * has been entered.
     * <p>
     * For numeric and text-based answers, the placeholder content is displayed in the text field or
     * text area when an answer has not yet been entered.
     *
     * @return the placeholder string
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Sets a localized string that represents the placeholder text displayed before an answer has
     * been entered.
     * <p>
     * For numeric and text-based answers, the placeholder content is displayed in the text field or
     * text area when an answer has not yet been entered.
     *
     * @param placeholder the placeholder string
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getAddmoretitle() {
        return addmoretitle;
    }

    public void setAddMoreTitle(String addMoreTitle) {
        this.addmoretitle = addMoreTitle;
    }

    public String getKey_pipe() {
        return key_pipe;
    }

    public void setKey_pipe(String key_pipe) {
        this.key_pipe = key_pipe;
    }

    public boolean isPPing() {
        return isPPing;
    }

    public void setPPing(boolean PPing) {
        isPPing = PPing;
    }

    public String getPipeValue() {
        return pipeValue;
    }

    public void setPipeValue(String pipeValue) {
        this.pipeValue = pipeValue;
    }

    public String getPipeOperator() {
        return pipeOperator;
    }

    public void setPipeOperator(String pipeOperator) {
        this.pipeOperator = pipeOperator;
    }

    public String getPipingSnippet() {
        return pipingSnippet;
    }

    public void setPipingSnippet(String pipingSnippet) {
        this.pipingSnippet = pipingSnippet;
    }
}
