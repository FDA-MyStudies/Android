package com.harvard.studyAppModule.activityBuilder;

import android.content.Context;
import android.util.Log;

import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.utils.AppController;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Rohit on 2/24/2017.
 */

public class ActivityBuilder extends OrderedTask {

    private static boolean mBranching;
    private static DBServiceSubscriber mDBServiceSubscriber;
    private static String mIdentifier;
    private static RealmList<Steps> activityQuestionStep;
    private static Context mcontext;

    private ActivityBuilder(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    public static ActivityBuilder create(Context context, String identifier, List<Step> steps, ActivityObj activityObj, boolean branching, DBServiceSubscriber dbServiceSubscriber) {
        mIdentifier = identifier;
        activityQuestionStep = activityObj.getSteps();
        mBranching = branching;
        mDBServiceSubscriber = dbServiceSubscriber;
        mcontext = context;
        return new ActivityBuilder(identifier, steps);
    }

    @Override
    public Step getStepAfterStep(Step previousStep, TaskResult taskResult) {
        Realm realmStep = AppController.getRealmobj(mcontext);
        if (mBranching) {
            Steps stepsData = null;
            for (int i = 0; i < activityQuestionStep.size(); i++) {
                if (previousStep == null) {
                    return steps.get(0);
                } else if (previousStep.getIdentifier().equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
                    stepsData = activityQuestionStep.get(i);
                }
            }

            if (stepsData.getResultType().equalsIgnoreCase("textScale") || stepsData.getResultType().equalsIgnoreCase("imageChoice") || stepsData.getResultType().equalsIgnoreCase("textChoice") || stepsData.getResultType().equalsIgnoreCase("boolean") || stepsData.getResultType().equalsIgnoreCase("valuePicker")) {

                if (stepsData != null && stepsData.getDestinations().size() == 1 && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("") && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                }
                else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else if (stepsData != null) {
                    Map<String, StepResult> map = taskResult.getResults();
                    String answer = "";
                    String destination = "";
                    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                        if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
                            try {
                                StepResult stepResult = pair.getValue();
                                Object o = stepResult.getResults().get("answer");
                                if (o instanceof Object[]) {
                                    Object[] objects = (Object[]) o;
                                    if (objects[0] instanceof String) {
                                        answer = "" + ((String) objects[0]);
                                    } else if (objects[0] instanceof Integer) {
                                        answer = "" + ((int) objects[0]);
                                    }
                                } else {
                                    answer = "" + stepResult.getResults().get("answer");
                                }
                            } catch (Exception e) {
                                answer = "";
                                e.printStackTrace();
                            }
                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                answer = "";
                            }
                            if (!answer.equalsIgnoreCase("")) {
                                if (stepsData.getResultType().equalsIgnoreCase("imageChoice") || stepsData.getResultType().equalsIgnoreCase("textChoice")) {
                                    Realm realm = AppController.getRealmobj(mcontext);
                                    StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + pair.getKey(), realm);
                                    for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
                                        if (stepRecordCustom.getTextChoices().get(j).getValue().equalsIgnoreCase(answer)) {
                                            answer = stepRecordCustom.getTextChoices().get(j).getText();
                                            break;
                                        }
                                    }
                                    mDBServiceSubscriber.closeRealmObj(realm);
                                }
                            }
                            break;
                        }
                    }
                    for (int j = 0; j < stepsData.getDestinations().size(); j++) {
                        if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
                            destination = stepsData.getDestinations().get(j).getDestination();
                        }
                    }
                    for (int k = 0; k < steps.size(); k++) {
                        if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
                            return steps.get(k);
                        }
                    }

                    // if destination doesn't satisfy
                    if (previousStep == null) {
                        return steps.get(0);
                    }

                    if (destination.equalsIgnoreCase("")) {
                        return null;
                    }

                    int nextIndex = steps.indexOf(previousStep) + 1;

                    if (nextIndex < steps.size()) {

                        return steps.get(nextIndex);
                    }
                }
            } else if (stepsData.getResultType().equalsIgnoreCase("scale") || stepsData.getResultType().equalsIgnoreCase("continuousScale") || stepsData.getResultType().equalsIgnoreCase("numeric") || stepsData.getResultType().equalsIgnoreCase("timeInterval") || stepsData.getResultType().equalsIgnoreCase("height")) {
                if (stepsData != null && stepsData.getDestinations().size() == 1 && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("") && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                } else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else if (stepsData != null) {
                    Map<String, StepResult> map = taskResult.getResults();
                    String answer = "";
                    String destination = "";
                    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                        if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
                            try {
                                StepResult stepResult = pair.getValue();
                                Object o = stepResult.getResults().get("answer");
                                if (o instanceof Object[]) {
                                    Object[] objects = (Object[]) o;
                                    if (objects[0] instanceof String) {
                                        answer = "" + ((String) objects[0]);
                                    } else if (objects[0] instanceof Integer) {
                                        answer = "" + ((int) objects[0]);
                                    }
                                } else {
                                    answer = "" + stepResult.getResults().get("answer");
                                }
                            } catch (Exception e) {
                                answer = "";
                                e.printStackTrace();
                            }
                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                answer = "";
                            }

                            break;
                        }
                    }
                    for (int j = 0; j < stepsData.getDestinations().size(); j++) {
                        if (answer.equalsIgnoreCase("")) {
                            if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
                                destination = stepsData.getDestinations().get(j).getDestination();
                            }
                        } else if (!stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase("")) {
                            double condition = Double.parseDouble(stepsData.getDestinations().get(j).getCondition());
                            if(stepsData.getResultType().equalsIgnoreCase("timeInterval"))
                            {
                                condition = Double.parseDouble(stepsData.getDestinations().get(j).getCondition())/3600d;
                            }
                            double answerDouble = Double.parseDouble(answer);
                            if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("e"))
                            {
                                 if(answerDouble == condition)
                                 {
                                     destination = stepsData.getDestinations().get(j).getDestination();
                                 }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gt"))
                            {
                                if(answerDouble > condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lt"))
                            {
                                if(answerDouble < condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gte"))
                            {
                                if(answerDouble >= condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lte"))
                            {
                                if(answerDouble <= condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("ne"))
                            {
                                if(answerDouble != condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                        }
                    }
                    for (int k = 0; k < steps.size(); k++) {
                        if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
                            return steps.get(k);
                        }
                    }

                    // if destination doesn't satisfy
                    if (previousStep == null) {
                        return steps.get(0);
                    }

                    if (destination.equalsIgnoreCase("")) {
                        return null;
                    }

                    int nextIndex = steps.indexOf(previousStep) + 1;

                    if (nextIndex < steps.size()) {
                        return steps.get(nextIndex);
                    }
                }
            }
            else {

                if (previousStep == null) {
                    return steps.get(0);
                }

                if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("")) && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                } else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else {

                    int nextIndex = steps.indexOf(previousStep) + 1;
                    if (nextIndex < steps.size()) {
                        return steps.get(nextIndex);
                    }
                }
            }

        }
        else {
            if (previousStep == null) {
                return steps.get(0);
            }

            if(!activityQuestionStep.get(steps.indexOf(previousStep)).isDefaultVisibility()){
                // check for preload logic of nextstep based on the index of first step
                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getOperator().contains(":")){

                }
                else {
                    switch (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getOperator()) {
                        case ">":
                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {
                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                int nextIndex = steps.indexOf(previousStep) + 1;
                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }
                            break;
                        case "<":
                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                int nextIndex = steps.indexOf(previousStep) + 1;

                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }
                            break;
                        case "<=":
                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                int nextIndex = steps.indexOf(previousStep) + 1;

                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }
                            break;
                        case ">=":
                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                int nextIndex = steps.indexOf(previousStep) + 1;

                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }
                            break;

                        case "=":

                            if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                int nextIndex = steps.indexOf(previousStep) + 1;

                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }

                            break;
                        case "!=":
                            if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                }
                            } else {
                                // int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                int nextIndex = steps.indexOf(previousStep) + 1;

                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {

                                                return steps.get(nextIndex);
                                            } else {
                                                return null;
                                            }

                                        }
                                    }
                                } else {

                                    if (nextIndex < steps.size()) {

                                        return steps.get(nextIndex);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
            else {

                int nextIndex = steps.indexOf(previousStep) + 1;
                if (nextIndex < steps.size()) {
                    if (activityQuestionStep.get(nextIndex).isHidden()) {
                        while (activityQuestionStep.get(nextIndex).isHidden()) {
                            nextIndex += 1;
                            if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                if (nextIndex < steps.size()) {

                                    return steps.get(nextIndex);
                                } else {
                                    return null;
                                }

                            }
                        }
                    } else {

                        if (nextIndex < steps.size()) {

                            return steps.get(nextIndex);
                        }
                    }
                }
            }
        }


        return null;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult taskResult) {

       taskResult.getResults().remove(step.getIdentifier());
       mDBServiceSubscriber.deleteStepRecord(mcontext,step.getIdentifier());

       if (mBranching) {
            String identifier = "";
            for (int i = 0; i < activityQuestionStep.size(); i++) {
                for (int k = 0; k < activityQuestionStep.get(i).getDestinations().size(); k++) {
                    if (activityQuestionStep.get(i).getDestinations().get(k).getDestination().equalsIgnoreCase(step.getIdentifier())) {
                        Map<String, StepResult> map = taskResult.getResults();
                        for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                            if (pair.getKey().equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
                                if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("boolean")|| activityQuestionStep.get(i).getResultType().equalsIgnoreCase("valuePicker")) {
                                    try {
                                        if (pair.getValue() != null) {
                                            String answer = null;
                                            try {
                                                StepResult stepResult = pair.getValue();

                                                Object o = stepResult.getResults().get("answer");
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects[0] instanceof String) {
                                                        answer = "" + ((String) objects[0]);
                                                    } else if (objects[0] instanceof Integer) {
                                                        answer = "" + ((int) objects[0]);
                                                    }
                                                } else {
                                                    answer = "" + stepResult.getResults().get("answer");
                                                }


                                            } catch (Exception e) {
                                                answer = "";
                                                e.printStackTrace();
                                            }
                                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                                answer = "";
                                            }
                                            if (!answer.equalsIgnoreCase("")) {
                                                if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice")) {
                                                    Realm realm = AppController.getRealmobj(mcontext);
                                                    StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(i).getKey(), realm);
                                                    for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
                                                        if (stepRecordCustom.getTextChoices().get(j).getValue().equalsIgnoreCase(answer)) {
                                                            answer = stepRecordCustom.getTextChoices().get(j).getText();
                                                            break;
                                                        }
                                                    }
                                                    mDBServiceSubscriber.closeRealmObj(realm);
                                                }
                                            }
                                            if (activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase(answer)) {
                                                identifier = activityQuestionStep.get(i).getKey();
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int nextIndex = steps.indexOf(step) - 1;

                                        if (nextIndex >= 0) {
                                            return steps.get(nextIndex);
                                        }
                                    }
                                }
                                else if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("scale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("continuousScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("numeric") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("timeInterval") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("height")) {
                                    try {
                                        if (pair.getValue() != null) {
                                            String answer = null;
                                            try {
                                                StepResult stepResult = pair.getValue();

                                                Object o = stepResult.getResults().get("answer");
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects[0] instanceof String) {
                                                        answer = "" + ((String) objects[0]);
                                                    } else if (objects[0] instanceof Integer) {
                                                        answer = "" + ((int) objects[0]);
                                                    }
                                                } else {
                                                    answer = "" + stepResult.getResults().get("answer");
                                                }


                                            } catch (Exception e) {
                                                answer = "";
                                                e.printStackTrace();
                                            }
                                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                                answer = "";
                                            }
                                            if(answer.equalsIgnoreCase("")) {
                                                if (activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase(answer)) {
                                                    identifier = activityQuestionStep.get(i).getKey();
                                                }
                                            }
                                            else if (!activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase("")) {
                                                double condition = Double.parseDouble(activityQuestionStep.get(i).getDestinations().get(k).getCondition());
                                                double answerDouble = Double.parseDouble(answer);
                                                if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("e"))
                                                {
                                                    if(answerDouble == condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("gt"))
                                                {
                                                    if(answerDouble > condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("lt"))
                                                {
                                                    if(answerDouble < condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("gte"))
                                                {
                                                    if(answerDouble >= condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("lte"))
                                                {
                                                    if(answerDouble <= condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("ne"))
                                                {
                                                    if(answerDouble != condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int nextIndex = steps.indexOf(step) - 1;

                                        if (nextIndex >= 0) {
                                            return steps.get(nextIndex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if((!identifier.equalsIgnoreCase("")) && (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("boolean")|| activityQuestionStep.get(i).getResultType().equalsIgnoreCase("valuePicker"))) {
                        break;
                    }
                }
                if((!identifier.equalsIgnoreCase("")) && (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("boolean")|| activityQuestionStep.get(i).getResultType().equalsIgnoreCase("valuePicker"))) {
                    break;
                }
            }
            for (int j = 0; j < steps.size(); j++) {
                if (steps.get(j).getIdentifier().equalsIgnoreCase(identifier)) {
                    return steps.get(j);
                }
            }
        }
        else {
           if(steps.indexOf(step) == 0){
               return null;
           }
            if(activityQuestionStep.get(steps.indexOf(step)).isHidden()){
                int prevIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(step)).getSourceQuestionKey()));
                if(taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex).getKey())
                        && taskResult.getStepResult(activityQuestionStep.get(prevIndex).getKey()) != null){
                    Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true");

                    if (prevIndex >= 0) {


                    return steps.get(prevIndex);
                         }
                }
            }
            else{

                int prevIndex = steps.indexOf(step) - 1;
                if(activityQuestionStep.get(prevIndex).isHidden()){

                    if(taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex).getSourceQuestionKey())
                            && taskResult.getStepResult(activityQuestionStep.get(prevIndex).getSourceQuestionKey()) != null){
                        if (prevIndex >= 0) {
                           //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                            return steps.get(prevIndex);
                        }
                    } else {
                        int prevIndex1 = prevIndex - 1;
                        for(int p = prevIndex1; p >= 0; p--){
                          if(activityQuestionStep.get(p).isHidden()){
                              // check the taskresult for the source question and decide
                              if(taskResult.getResults().containsKey(activityQuestionStep.get(p).getSourceQuestionKey())
                                      && taskResult.getStepResult(activityQuestionStep.get(p).getSourceQuestionKey()) != null){
                                  // if task result is there then check for the flow if it is true flow or false flow

                                  switch (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getOperator()){

                                      case ">":
                                          if(Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())){

                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }

                                          break;
                                      case "<":
                                          if(Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())){
                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }
                                          break;
                                      case  "<=":
                                          if(Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue()))
                                          {

                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }
                                          break;
                                      case ">=":
                                          if(Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue()))
                                          {

                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }
                                          break;

                                      case "=":

                                          if(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue()))
                                          {

                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }

                                          break;
                                      case "!=":
                                          if(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue()))
                                          {

                                              if (p >= 0) {
                                                  //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                  return steps.get(p);
                                              }
                                          }

                                          break;
                                  }
                              }
                          }
                          else{
                              return steps.get(p);
                          }
                        }
                    }
                }
                else{
                    if (prevIndex >= 0) {
                        Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                        return steps.get(prevIndex);
                    }
                }

            }
//            int nextIndex = steps.indexOf(step) - 1;
//            if(!activityQuestionStep.get(nextIndex).isHidden()){
//                if (nextIndex >= 0) {
//                    return steps.get(nextIndex);
//                }
//            }
//            else {
//
//                for(int i = nextIndex; i>=0; i--){
//                    if(taskResult.getResults().containsKey(activityQuestionStep.get(i).getKey())
//                            && taskResult.getStepResult(activityQuestionStep.get(i).getKey()) != null){
//                        Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true");
//                        //if (nextIndex >= 0) {
//                            Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
//                            return steps.get(i);
//                   //     }
//                    }
//                    else{
//                        if (i >= 0) {
//                            int previousIndex = i - 1;
//                            if (activityQuestionStep.get(previousIndex).isHidden()) {
//                                Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true");
//                                if (taskResult.getResults().containsKey(activityQuestionStep.get(previousIndex).getKey())
//                                        && taskResult.getStepResult(activityQuestionStep.get(previousIndex).getKey()) != null) {
//                                    if (previousIndex >= 0) {
//                                        Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return iteration");
//                                        return steps.get(previousIndex);
//                                    }
//                                }
//                            }
//                            else {
//                                if (previousIndex >= 0) {
//                                    Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return iteration");
//                                    return steps.get(previousIndex);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                    int nextIndex1 = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(nextIndex).getSourceQuestionKey()));
//
////                    if(nextIndex1 >= 0) {
////                        if (taskResult.getResults().containsKey(activityQuestionStep.get(nextIndex1).getKey())) {
////                            if (taskResult.getStepResult(activityQuestionStep.get(nextIndex1).getKey()).getResult().toString() != null) {
////                                if (nextIndex1 >= 0) {
////                                    return steps.get(nextIndex1);
////                                }
////                            } else {
////                                nextIndex1 -= 1;
////                            }
////                        }else{
////
////                            nextIndex1 -= 1;
////                            if (nextIndex >= 0) {
////                                return steps.get(nextIndex1);
////                            }
////                        }
////                    }
//
////                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(step)).getSourceQuestionKey()));
////                taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(nextIndex)).getKey()).getResult().toString();
////
////                //int nextIndex = steps.indexOf(step) - 1;
////                if (nextIndex >= 0) {
////                    return steps.get(nextIndex);
////                }
//
//            }

            return null;
        }


        int nextIndex = steps.indexOf(step) - 1;

        if (nextIndex >= 0) {
            return steps.get(nextIndex);
        }

        return null;
    }

    @Override
    public Step getStepWithIdentifier(String identifier) {
        Iterator var2 = this.steps.iterator();

        Step step;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            step = (Step)var2.next();
        } while(!identifier.equals(step.getIdentifier()));

        return step;
    }

}
