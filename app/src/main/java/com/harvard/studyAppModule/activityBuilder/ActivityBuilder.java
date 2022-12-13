package com.harvard.studyAppModule.activityBuilder;

import android.content.Context;
import android.util.Log;

import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.activityBuilder.model.PreLoadLogic;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            }
            else if (stepsData.getResultType().equalsIgnoreCase("scale") || stepsData.getResultType().equalsIgnoreCase("continuousScale") || stepsData.getResultType().equalsIgnoreCase("numeric") || stepsData.getResultType().equalsIgnoreCase("timeInterval") || stepsData.getResultType().equalsIgnoreCase("height")) {
                if (stepsData != null && stepsData.getDestinations().size() == 1 && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("") && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                }
                else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                }
                else if (stepsData != null) {
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
            if (activityQuestionStep.get(steps.indexOf(previousStep)).isDefaultVisibility() && !activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("grouped")) {
                // check for preload logic of nextstep based on the index of first step
                int nextIndex = steps.indexOf(previousStep) + 1;
                if (nextIndex < steps.size()) {
                    if (activityQuestionStep.get(nextIndex).isHidden()) {
                        while (activityQuestionStep.get(nextIndex).isHidden()) {
                             nextIndex += 1;
                            if(nextIndex < steps.size()) {
                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                    if (nextIndex < steps.size()) {
                                        return steps.get(nextIndex);
                                    } else {
                                        return null;
                                    }

                                }
                            }
                            else{
                                return null;
                            }
                        }
                    }
                    else {

                        if (nextIndex < steps.size()) {

                            return steps.get(nextIndex);
                        }
                    }
                }
                }
            else if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("grouped")){
                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                        // cs.saveAndFinish();
                        return null;
                    }
                    return null;
                }
                else {
                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).isDefaultVisibility()) {
                        int nextIndex = steps.indexOf(previousStep) + 1;
                        if (nextIndex < steps.size()) {
                            if (activityQuestionStep.get(nextIndex).isHidden()) {
                                while (activityQuestionStep.get(nextIndex).isHidden()) {
                                    nextIndex += 1;
                                    if (nextIndex < steps.size()) {
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return null;
                                            }

                                        }
                                    }
                                    else {
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                            return null;
                                        }
                                        return null;
                                    }
                                }
                            }
                            else {
                                if (nextIndex < steps.size()) {
                                    return steps.get(nextIndex);
                                }
                                else{
                                    return null;
                                }
                            }
                        }
                    }

                    else {
                        if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getOperator().contains(":")) {
                            String[] strOper = activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getOperator().split(":");
                            List<String> listOp = new ArrayList<String>(Arrays.asList(strOper));
                            String[] strValue = activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue().split(":");
                            List<String> listVal = new ArrayList<String>(Arrays.asList(strValue));
                            boolean result = false;
                            StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                            for (int val = 0; val < listVal.size(); val++) {
                                switch (listOp.get(0).trim()) {
                                    case ">":
                                    if (stepRecordCustom != null) {
                                            try {
                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) > Double.valueOf(listVal.get(val))) {
                                                    result = true;
                                                    listOp.remove(0);
                                                } else {
                                                    result = false;
                                                    listOp.remove(0);
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case "<":
                                  if (stepRecordCustom != null) {
                                            try {
                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) < Double.valueOf(listVal.get(val))) {
                                                    result = true;
                                                    listOp.remove(0);
                                                } else {
                                                    result = false;
                                                    listOp.remove(0);
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case "<=":
                                     if (stepRecordCustom != null) {
                                            try {
                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                if(Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) <= Double.valueOf(listVal.get(val))) {
                                                    result = true;
                                                    listOp.remove(0);
                                                }
                                                else {
                                                    result = false;
                                                    listOp.remove(0);
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case ">=":
                                    if (stepRecordCustom != null) {
                                            try {
                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) >= Double.valueOf(listVal.get(val))) {
                                                    result = true;
                                                    listOp.remove(0);
                                                }
                                                else {
                                                    result = false;
                                                    listOp.remove(0);
                                                }
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case "=":
                                        if (stepRecordCustom != null) {
                                            try {

                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                        if (object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString().equalsIgnoreCase(listVal.get(val))) {
                                            result = true;
                                            listOp.remove(0);
                                        }
                                        else {
                                            result = false;
                                            listOp.remove(0);
                                        }
                                         }
                                        catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    case "!=":
                                        if (stepRecordCustom != null) {
                                            try {
                                                JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                        if (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val))) {
                                            result = true;
                                            listOp.remove(0);
                                        }
                                        else {
                                            result = false;
                                            listOp.remove(0);
                                        }
                                           }
                                            catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                                        break;
                                    case "AND":
                                        switch (listOp.get(1)) {
                                            case ">":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result && Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) > Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);

                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "<":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result && Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) < Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);

                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "<=":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result && Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) >= Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        } else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);

                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case ">=":

                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result && Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) >= Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "=":
                                                if (stepRecordCustom != null) {
                                                 //   try {
                                                        //JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result && (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
//                                                    }
//                                                    catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
                                                }
                                                break;
                                            case "!=":
                                                if (stepRecordCustom != null) {
                                                    if (result && (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                        result = true;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
                                                    else {
                                                        result = false;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                    case "OR":
                                        switch (listOp.get(1)) {
                                            case ">":

                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result || Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) > Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "<":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result || Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) < Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "<=":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result || Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) <= Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case ">=":
                                                if (stepRecordCustom != null) {
                                                    try {
                                                        JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                        if (result || Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) >= Double.valueOf(listVal.get(val))) {
                                                            result = true;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                        else {
                                                            result = false;
                                                            if (listOp.size() >= 1) {
                                                                listOp.remove(1);
                                                                listOp.remove(0);
                                                            } else {
                                                                listOp.remove(0);
                                                            }
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "=":
                                                if (stepRecordCustom != null) {
                                                    //   try {
                                                    //JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                    if (result || (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                        result = true;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
                                                    else {
                                                        result = false;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
//                                                    }
//                                                    catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
                                                }
                                                break;
                                            case "!=":
                                             if (stepRecordCustom != null) {
                                                    //   try {
                                                    //JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                    if (result || (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                        result = true;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
                                                    else {
                                                        result = false;
                                                        if (listOp.size() >= 1) {
                                                            listOp.remove(1);
                                                            listOp.remove(0);
                                                        } else {
                                                            listOp.remove(0);
                                                        }
                                                    }
//                                                    }
//                                                    catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
                                                }
                                    break;
                                        }
                                        break;
                                }
                            }

                            if (result) {
                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                    return null;
                                }
                                else {
                                    int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                    if (nextIndex < steps.size()) {
                                        if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                            return null;
                                        }

                                        return steps.get(nextIndex);
                                    } else {
                                        if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                            return null;
                                        }
                                        return null;
                                    }
                                }
                            }
                            else {
                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                    return null;
                                }
                                else{
                                int nextIndex = steps.indexOf(previousStep) + 1;
                                if(nextIndex < steps.size() - 1) {
                                    if (activityQuestionStep.get(nextIndex).isHidden()) {
                                        while (activityQuestionStep.get(nextIndex).isHidden()) {
                                            if(nextIndex < steps.size() - 1) {
                                                nextIndex += 1;
                                            }
                                            if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                if (nextIndex < steps.size()) {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyActivityId",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey",
                                                                        "");

                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey2",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyactivityVersion",
                                                                        "");
                                                        // cs.saveAndFinish();
                                                        return null;
                                                    }
                                                    return steps.get(nextIndex);
                                                } else {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyActivityId",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey",
                                                                        "");

                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey2",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyactivityVersion",
                                                                        "");
                                                        // cs.saveAndFinish();
                                                        return null;
                                                    }
                                                    return null;
                                                }

                                            }
                                        }
                                    } else {
                                        if (nextIndex < steps.size()) {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyActivityId",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey",
                                                                "");

                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey2",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyactivityVersion",
                                                                "");
                                                // cs.saveAndFinish();
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        } else {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyActivityId",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey",
                                                                "");

                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey2",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyactivityVersion",
                                                                "");
                                                // cs.saveAndFinish();
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                }
                            }
                            }
                        }
                        else {
                             //if(activityQuestionStep.get(steps.indexOf(previousStep)).getSourceQuestionKey() == null){
                                 if(activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId().equalsIgnoreCase("")
                                         && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("")) {
                                    if ((steps.indexOf(previousStep) + 1) <= activityQuestionStep.size() - 1) {
                                         if (activityQuestionStep.get(steps.indexOf(previousStep) + 1).getGroupId() != null && !activityQuestionStep.get(steps.indexOf(previousStep) + 1).getGroupId().equalsIgnoreCase("")) {
                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep) + 1).getGroupId())) {
                                                 int index = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep) + 1).getKey()));
                                                 return steps.get(index);
                                             }
                                             else{
                                                 //if() need the flow for non group activity
                                             }
                                         }
                                     }
                                     else {
                                         return null;
                                     }
                                 }
                            else {
                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue().equalsIgnoreCase("") && activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId().equalsIgnoreCase("")) {
                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getGroupId().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep) + 1).getGroupId())) {
                                             int index = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep) + 1).getKey()));
                                             return steps.get(index);
                                         }
                                     }
                                     else {
                                         switch (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getOperator()) {
                                             case ">":
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                     Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getResults().get("answer");

                                                     if (Double.valueOf(obj[0].toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                         if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                                             return null;
                                                         }
                                                         else {
                                                             int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                             else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                         if (nextIndex < steps.size() - 1) {
                                                             if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size() - 1) {
                                                                         nextIndex += 1;
                                                                     }
                                                                     if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         if (nextIndex < steps.size()) {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 clearSurveyToSurveyPreference();
                                                                                 return null;
                                                                             }
                                                                             return steps.get(nextIndex);
                                                                         } else {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 clearSurveyToSurveyPreference();
                                                                                 return null;
                                                                             }
                                                                             return null;
                                                                         }

                                                                     }
                                                                 }
                                                             } else {
                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());

                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         clearSurveyToSurveyPreference();
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                         }
                                                     }
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     return steps.get(nextIndex);
                                                                 }
                                                             } else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;
                                                                 if (nextIndex < steps.size() - 1) {
                                                                     if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size() - 1) {
                                                                                 nextIndex += 1;
                                                                             }
                                                                             if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                                 if (nextIndex < steps.size()) {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveyActivityId",
                                                                                                         "");
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveySourceKey",
                                                                                                         "");

                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveySourceKey2",
                                                                                                         "");
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveyactivityVersion",
                                                                                                         "");
                                                                                         // cs.saveAndFinish();
                                                                                         return null;
                                                                                     }
                                                                                     return steps.get(nextIndex);
                                                                                 } else {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveyActivityId",
                                                                                                         "");
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveySourceKey",
                                                                                                         "");

                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveySourceKey2",
                                                                                                         "");
                                                                                         AppController.getHelperSharedPreference()
                                                                                                 .writePreference(
                                                                                                         mcontext,
                                                                                                         "survetTosurveyactivityVersion",
                                                                                                         "");
                                                                                         // cs.saveAndFinish();
                                                                                         return null;
                                                                                     }
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
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         } else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyActivityId",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey",
                                                                                     "");

                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey2",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyactivityVersion",
                                                                                     "");
                                                                     // cs.saveAndFinish();
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                         }
                                                     }
                                                 }
                                                 else {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                                                     return null;
                                                                 }
                                                                 else {
                                                                     int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     }
                                                                 }
                                                             }
                                                             else {

                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                     return null;
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                     return null;
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 }
                                                                 else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     }
                                                                 }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         }
                                                         else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             } else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 break;

                                             case "<":
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {

                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 }
                                                             } else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
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
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         } else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyActivityId",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey",
                                                                                     "");

                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey2",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyactivityVersion",
                                                                                     "");
                                                                     // cs.saveAndFinish();
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                         }
                                                     }
                                                 }
                                                 else {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             } else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 } else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {

                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     } else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         } else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyActivityId",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey",
                                                                                     "");

                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey2",
                                                                                     "");
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyactivityVersion",
                                                                                     "");
                                                                     // cs.saveAndFinish();
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                         }
                                                     }
                                                 }
                                                 break;

                                             case "<=":
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         "");

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         "");
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             } else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 } else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
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
                                                 }
                                                 else {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             } else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyActivityId",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey",
                                                                                                     "");

                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveySourceKey2",
                                                                                                     "");
                                                                                     AppController.getHelperSharedPreference()
                                                                                             .writePreference(
                                                                                                     mcontext,
                                                                                                     "survetTosurveyactivityVersion",
                                                                                                     "");
                                                                                     // cs.saveAndFinish();
                                                                                     return null;
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 } else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     } else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
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
                                                 break;

                                             case ">=":
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {

                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyActivityId",
                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey",
                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveySourceKey2",
                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                                     AppController.getHelperSharedPreference()
                                                                             .writePreference(
                                                                                     mcontext,
                                                                                     "survetTosurveyactivityVersion",
                                                                                     activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                                     // cs.saveAndFinish();
                                                                     return null;
                                                                 }
                                                                 else if (nextIndex < steps.size()) {
                                                                     return steps.get(nextIndex);
                                                                 }
                                                                 else {
                                                                     return null;
                                                                 }
                                                             }
                                                             else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 else if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 return steps.get(nextIndex);
                                                                             }
                                                                             else {
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 }
                                                                 else {

                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         clearSurveyToSurveyPreference();
                                                                     }
                                                                     else if (nextIndex < steps.size()) {

                                                                         return steps.get(nextIndex);
                                                                     }
                                                                     else {
                                                                         clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }

                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1) != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                             clearSurveyToSurveyPreference();
                                                         }
                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         } else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             } else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                    clearSurveyToSurveyPreference();
                                                                     // cs.saveAndFinish();
                                                                     return null;
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 else {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                    return null;
                                                                 }
                                                                 else if (nextIndex < steps.size()) {

                                                                     return steps.get(nextIndex);
                                                                 }
                                                                 else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                             else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                     return null;
                                                                 }
                                                                 else if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;

                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {

                                                                             if (nextIndex < steps.size()) {

                                                                                 return steps.get(nextIndex);
                                                                             }
                                                                             else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                     return null;
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 }
                                                                 else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyActivityId",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey",
                                                                                             "");

                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveySourceKey2",
                                                                                             "");
                                                                             AppController.getHelperSharedPreference()
                                                                                     .writePreference(
                                                                                             mcontext,
                                                                                             "survetTosurveyactivityVersion",
                                                                                             "");
                                                                             // cs.saveAndFinish();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                             clearSurveyToSurveyPreference();
                                                             return null;
                                                         }
                                                         else if (activityQuestionStep.get(nextIndex).isHidden()) {
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
                                                         }
                                                         else {
                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                 clearSurveyToSurveyPreference();
                                                                 return null;
                                                             }
                                                             if (nextIndex < steps.size()) {
                                                                 return steps.get(nextIndex);
                                                             }
                                                             else {
                                                                 return null;
                                                             }
                                                         }
                                                     }

                                                 }
                                                 break;

                                             case "=":
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                     //Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getResults().get("answer");
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             //Object[] obj = (Object[]) object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1 ).getKey()).getJSONObject("results").get("answer");
                                                             if (object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                             else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;
                                                                 if (nextIndex < steps.size() - 1) {
                                                                     if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size() - 1) {
                                                                                 nextIndex += 1;
                                                                             }
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
                                                                         else {
                                                                             return null;

                                                                         }
                                                                     }
                                                                 }
                                                             }

                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                         if (nextIndex < steps.size() - 1) {
                                                             if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size() - 1) {
                                                                         nextIndex += 1;
                                                                     }
                                                                     if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         if (nextIndex < steps.size()) {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveyActivityId",
                                                                                                 "");
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveySourceKey",
                                                                                                 "");

                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveySourceKey2",
                                                                                                 "");
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveyactivityVersion",
                                                                                                 "");
                                                                                 // cs.saveAndFinish();
                                                                                 return null;
                                                                             }
                                                                             return steps.get(nextIndex);
                                                                         } else {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveyActivityId",
                                                                                                 "");
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveySourceKey",
                                                                                                 "");

                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveySourceKey2",
                                                                                                 "");
                                                                                 AppController.getHelperSharedPreference()
                                                                                         .writePreference(
                                                                                                 mcontext,
                                                                                                 "survetTosurveyactivityVersion",
                                                                                                 "");
                                                                                 // cs.saveAndFinish();
                                                                                 return null;
                                                                             }
                                                                             return null;
                                                                         }

                                                                     }
                                                                 }
                                                             } else {

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         "");

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         "");
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyActivityId",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey",
                                                                                         "");

                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveySourceKey2",
                                                                                         "");
                                                                         AppController.getHelperSharedPreference()
                                                                                 .writePreference(
                                                                                         mcontext,
                                                                                         "survetTosurveyactivityVersion",
                                                                                         "");
                                                                         // cs.saveAndFinish();
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                         }
                                                     }
                                                 }
//                                else if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("grouped")){
//                                    StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
//                                    if(stepRecordCustom != null){
//                                        try {
//                                            JSONObject object = new JSONObject(stepRecordCustom.getResult());
//                                            if(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1 ).getKey()).getJSONObject("results").get("answer").toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())){
//
//                                            }
//                                            else
//                                            {
//
//
//                                            }
//                                            object.get("results");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//
                                                 else {
                                                     StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("continuousScale")
                                                             || activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("scale")
                                                             || activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("numeric")
                                                             || activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("height"))
                                                             {

                                                                     if (Double.valueOf(object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString()).equals(Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue()))) {
                                                                         int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                                                         if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                                                             return null;
                                                                         }
                                                                         else if (nextIndex < steps.size()) {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                                 // cs.saveAndFinish();
                                                                                 return null;
                                                                             }
                                                                             return steps.get(nextIndex);
                                                                         }
                                                                         else {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                             }
                                                                             return null;
                                                                         }
                                                                     }
                                                                     else {
                                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                                         if (nextIndex < steps.size() - 1) {
                                                                             if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                                 while (activityQuestionStep.get(nextIndex).isHidden()) {

                                                                                     if (nextIndex < steps.size() - 1) {
                                                                                         nextIndex += 1;
                                                                                     }
                                                                                     if (nextIndex < steps.size()) {
                                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                                             if (nextIndex < steps.size()) {
                                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                                     clearSurveyToSurveyPreference();
                                                                                                 }
                                                                                                 return steps.get(nextIndex);
                                                                                             } else {
                                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                                    clearSurveyToSurveyPreference();
                                                                                                 }
                                                                                                 return null;
                                                                                             }

                                                                                         }
                                                                                     }
                                                                                 }
                                                                             } else {
                                                                                 if (nextIndex < steps.size()) {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                         clearSurveyToSurveyPreference();
                                                                                     }
                                                                                     return steps.get(nextIndex);
                                                                                 } else {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                       clearSurveyToSurveyPreference();
                                                                                     }
                                                                                     return null;
                                                                                 }
                                                                             }
                                                                         }
                                                                     }

                                                             }
                                                             else {

                                                                 //else {
                                                                     if (object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                         int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                                                         if(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")){
                                                                             return null;
                                                                         }
                                                                         else if (nextIndex < steps.size()) {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                                 return null;
                                                                             }
                                                                             return steps.get(nextIndex);
                                                                         }
                                                                         else {
                                                                             if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                 updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                             }
                                                                             return null;
                                                                         }
                                                                     }
                                                                     else {
                                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                                         if (nextIndex < steps.size() - 1) {
                                                                             if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                                 while (activityQuestionStep.get(nextIndex).isHidden()) {

                                                                                     if (nextIndex < steps.size() - 1) {
                                                                                         nextIndex += 1;
                                                                                     }
                                                                                     if (nextIndex < steps.size()) {
                                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                                             if (nextIndex < steps.size()) {
                                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                                    clearSurveyToSurveyPreference();
                                                                                                 }
                                                                                                 return steps.get(nextIndex);
                                                                                             } else {
                                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                                     clearSurveyToSurveyPreference();
                                                                                                 }
                                                                                                 return null;
                                                                                             }

                                                                                         }
                                                                                     }
                                                                                 }
                                                                             } else {
                                                                                 if (nextIndex < steps.size()) {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                        clearSurveyToSurveyPreference();
                                                                                     }
                                                                                     return steps.get(nextIndex);
                                                                                 } else {
                                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                        clearSurveyToSurveyPreference();
                                                                                     }
                                                                                     return null;
                                                                                 }
                                                                             }
                                                                         }
                                                                     }
                                                                // }
                                                             }
                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));

                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                            clearSurveyToSurveyPreference();
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }
                                                             }
                                                         }
                                                         else {
                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                   //  return null;
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                             else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                    // return null;
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 break;
                                             case "!=":


                                                 StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(steps.indexOf(previousStep)).getKey(), realmStep);
                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             //Object[] obj = (Object[]) object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1 ).getKey()).getJSONObject("results").get("answer");
                                                             if (!object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").getJSONArray("answer").get(0).toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));

                                                                 if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());

                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 } else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());

                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                             else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 } else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();

                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }

                                                             }

                                                         }
                                                         catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                                         int nextIndex = steps.indexOf(previousStep) + 1;

                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         }
                                                         else {

                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             }
                                                             else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                   clearSurveyToSurveyPreference();
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 else {
                                                     if (stepRecordCustom != null) {
                                                         try {
                                                             JSONObject object = new JSONObject(stepRecordCustom.getResult());
                                                             if (!object.getJSONObject(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getKey()).getJSONObject("results").get("answer").toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getValue())) {
                                                                 int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey()));
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());

                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                                 else if (nextIndex < steps.size()) {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                         updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                         return null;
                                                                     }
                                                                     return steps.get(nextIndex);
                                                                 }
                                                                 else {
                                                                     if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic());
                                                                         return null;
                                                                     }
                                                                     return null;
                                                                 }
                                                             }
                                                             else {
                                                                 int nextIndex = steps.indexOf(previousStep) + 1;

                                                                 if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                         nextIndex += 1;
                                                                         if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                             if (nextIndex < steps.size()) {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().get(activityQuestionStep.get(steps.indexOf(previousStep)).getSteps().size() - 1).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                    clearSurveyToSurveyPreference();
                                                                                 }
                                                                                 return steps.get(nextIndex);
                                                                             } else {
                                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                                     clearSurveyToSurveyPreference();
                                                                                 }
                                                                                 return null;
                                                                             }

                                                                         }
                                                                     }
                                                                 }
                                                                 else {

                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                            clearSurveyToSurveyPreference();
                                                                             //return null;
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                             clearSurveyToSurveyPreference();
                                                                             return null;
                                                                         }
                                                                         return null;
                                                                     }
                                                                 }

                                                             }

                                                         } catch (JSONException e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                     else {
                                                         //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                                         int nextIndex = steps.indexOf(previousStep) + 1;
                                                         if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                             while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                                 nextIndex += 1;
                                                                 if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                                     if (nextIndex < steps.size()) {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                           clearSurveyToSurveyPreference();
                                                                         }
                                                                         return steps.get(nextIndex);
                                                                     } else {
                                                                         if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                            clearSurveyToSurveyPreference();
                                                                         }
                                                                         return null;
                                                                     }

                                                                 }
                                                             }
                                                         } else {
                                                             if (nextIndex < steps.size()) {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                    clearSurveyToSurveyPreference();
                                                                 }
                                                                 return steps.get(nextIndex);
                                                             } else {
                                                                 if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                     clearSurveyToSurveyPreference();
                                                                 }
                                                                 return null;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 break;
                                         }
                                     }
                                 }

                        }
                    }
                }
            }

            else {

                //if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getOperator().contains(":")) {
                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue().contains(":")) {
                        String[] strOper = activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getOperator().split(":");
                        List<String> listOp = new ArrayList<String>(Arrays.asList(strOper));

                        String[] strValue = activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue().split(":");
                        List<String> listVal = new ArrayList<String>(Arrays.asList(strValue));
                        boolean result = false;
                        for (int val = 0; val < listVal.size(); val++) {
                            switch (listOp.get(0).trim()) {
                                case ">":
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(listVal.get(val))) {
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }

                                    break;
                                case "<":
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) < Double.valueOf(listVal.get(val))) {
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }
                                    break;
                                case "<=":
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) <= Double.valueOf(listVal.get(val))) {
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }
                                    break;
                                case ">=":
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) >= Double.valueOf(listVal.get(val))) {
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }
                                    break;
                                case "=":

                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                    {
                                        if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(listVal.get(val)))){
                                            result = true;
                                            listOp.remove(0);
                                        }
                                        else {
                                            result = false;
                                            listOp.remove(0);
                                        }
                                    }
                                    else if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val))){
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }
                                    break;
                                case "!=":
                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                            || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                    {
                                        if (!Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(listVal.get(val)))){
                                            result = true;
                                            listOp.remove(0);
                                        }
                                        else {
                                            result = false;
                                            listOp.remove(0);
                                        }
                                    }
                                    else
                                    if(!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val))){
                                        result = true;
                                        listOp.remove(0);
                                    }
                                    else {
                                        result = false;
                                        listOp.remove(0);
                                    }
                                    break;
                                case "&&" :
                                    switch (listOp.get(1)) {
                                        case ">":
                                            if (result && (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);

                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);

                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                        case "<":
                                            if (result && (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) < Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);

                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);

                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                        case "<=":
                                            if (result && (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) <= Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);

                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                        case ">=":
                                            if (result && (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) >= Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                        case "=":

                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                            {
                                                if (result && (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(listVal.get(val))))) {
                                                    result = true;
                                                    if(listOp.size() >= 1) {
                                                        listOp.remove(1);
                                                        listOp.remove(0);
                                                    }else {
                                                        listOp.remove(0);
                                                    }
                                                } else {
                                                    result = false;
                                                    if(listOp.size() >= 1) {
                                                        listOp.remove(1);
                                                        listOp.remove(0);
                                                    }else {
                                                        listOp.remove(0);
                                                    }
                                                }
                                            }
                                            else if (result && (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                        case "!=":
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                            {

                                            }
                                            else if (result && (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString() != listVal.get(val))) {
                                                result = true;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            } else {
                                                result = false;
                                                if(listOp.size() >= 1) {
                                                    listOp.remove(1);
                                                    listOp.remove(0);
                                                }else {
                                                    listOp.remove(0);
                                                }
                                            }
                                            break;
                                    }
                                    break;
                                case "||":
                                    switch (listOp.get(1)) {
                                        case ">":
                                            if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(listVal.get(val)))) {
                                                result = true;

                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                        case "<":
                                            if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) < Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                        case "<=":
                                            if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) <= Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                        case ">=":
                                            if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) >= Double.valueOf(listVal.get(val)))) {
                                                result = true;
                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                        case "=":
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                            {
                                                if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(listVal.get(val))))) {
                                                    result = true;
                                                } else {
                                                    result = false;
                                                }
                                            }

                                           else if (result || (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(listVal.get(val)))) {
                                                result = true;
                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                        case "!=":
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric")
                                                    || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                            {
                                                if (result || (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) != Double.valueOf(listVal.get(val)))) {
                                                    result = true;
                                                } else {
                                                    result = false;
                                                }
                                            }
                                            else if (result || !(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equalsIgnoreCase(listVal.get(val))) {
                                                result = true;
                                            } else {
                                                result = false;
                                            }
                                            if(listOp.size() >= 1) {
                                                listOp.remove(1);
                                                listOp.remove(0);
                                            }else {
                                                listOp.remove(0);
                                            }
                                            break;
                                    }
                                    break;
                            }
                        }
                        if(result){
                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {

                                return null;
                            }
                            else{
                                int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                if (nextIndex < steps.size()) {
                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                        return null;
                                    }
                                    return steps.get(nextIndex);
                                }
                                else {
                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                        return null;
                                    }
                                    return null;
                                }
                            }
                        }
                        else{
//                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
//                                return null;
//                            }
                           // else {
                                int nextIndex = steps.indexOf(previousStep) + 1;
                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                        nextIndex += 1;
                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                   clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }

                                        }
                                    }
                                }
                                else {
                                    if (nextIndex < steps.size()) {
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                          clearSurveyToSurveyPreference();
                                        }
                                        return steps.get(nextIndex);
                                    }
                                    else{
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                           clearSurveyToSurveyPreference();
                                            return null;
                                        }
                                        return null;
                                    }
                                }
                           // }
                        }
                    }
               // }
                else if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue().equalsIgnoreCase("")){
                        if(!activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("")){
                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                            if (nextIndex < steps.size()) {

                                return steps.get(nextIndex);
                            }else{
                                return null;
                            }
                        }
                        else{
                            if(steps.indexOf(previousStep) < activityQuestionStep.size()) {
                                int nextIndex = steps.indexOf(previousStep) + 1;
                                if(nextIndex < activityQuestionStep.size()) {
                                    if (activityQuestionStep.get(nextIndex).isHidden()) {
                                        while (activityQuestionStep.get(nextIndex).isHidden()) {
                                            nextIndex += 1;
                                            if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                if (nextIndex < steps.size()) {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyActivityId",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey",
                                                                        "");

                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey2",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyactivityVersion",
                                                                        "");
                                                        // cs.saveAndFinish();
                                                        return null;
                                                    }
                                                    return steps.get(nextIndex);
                                                } else {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyActivityId",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey",
                                                                        "");

                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveySourceKey2",
                                                                        "");
                                                        AppController.getHelperSharedPreference()
                                                                .writePreference(
                                                                        mcontext,
                                                                        "survetTosurveyactivityVersion",
                                                                        "");
                                                        // cs.saveAndFinish();
                                                        return null;
                                                    }
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                    else {

                                        if (nextIndex < steps.size()) {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyActivityId",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey",
                                                                "");

                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey2",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyactivityVersion",
                                                                "");
                                                // cs.saveAndFinish();
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        } else {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyActivityId",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey",
                                                                "");

                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey2",
                                                                "");
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyactivityVersion",
                                                                "");
                                                // cs.saveAndFinish();
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                }
                                else {
                                    return null;
                                }
                            }
                        }
                    }
                else {
                    switch (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getOperator()) {
                        case ">":
                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single")){
                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");


                                    if (Double.valueOf(obj[0].toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                            return null;
                                        }
                                        else if (nextIndex < steps.size()) {

                                            return steps.get(nextIndex);
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                            AppController.getHelperSharedPreference()
                                                    .writePreference(
                                                            mcontext,
                                                            "survetTosurveyActivityId",
                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                            AppController.getHelperSharedPreference()
                                                    .writePreference(
                                                            mcontext,
                                                            "survetTosurveySourceKey",
                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                            AppController.getHelperSharedPreference()
                                                    .writePreference(
                                                            mcontext,
                                                            "survetTosurveySourceKey2",
                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                            AppController.getHelperSharedPreference()
                                                    .writePreference(
                                                            mcontext,
                                                            "survetTosurveyactivityVersion",
                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                            // cs.saveAndFinish();
                                            return null;
                                        }
                                        else if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            AppController.getHelperSharedPreference()
                                                                    .writePreference(
                                                                            mcontext,
                                                                            "survetTosurveyActivityId",
                                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                            AppController.getHelperSharedPreference()
                                                                    .writePreference(
                                                                            mcontext,
                                                                            "survetTosurveySourceKey",
                                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                            AppController.getHelperSharedPreference()
                                                                    .writePreference(
                                                                            mcontext,
                                                                            "survetTosurveySourceKey2",
                                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                            AppController.getHelperSharedPreference()
                                                                    .writePreference(
                                                                            mcontext,
                                                                            "survetTosurveyactivityVersion",
                                                                            activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                            // cs.saveAndFinish();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyActivityId",
                                                                activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId());
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey",
                                                                activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());

                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveySourceKey2",
                                                                activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey());
                                                AppController.getHelperSharedPreference()
                                                        .writePreference(
                                                                mcontext,
                                                                "survetTosurveyactivityVersion",
                                                                activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityVersion());
                                                // cs.saveAndFinish();
                                                return null;
                                            }
                                            else if (nextIndex < steps.size()) {
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                return null;
                                            }
                                        }
                                    }

                            }
                            else {
                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                        updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                        return null;
                                    }
                                    return null;
                                }
                               else {
                                        if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {
                                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                        else {
                                            int nextIndex = steps.indexOf(previousStep) + 1;
                                            if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                    nextIndex += 1;
                                                    if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                        if (nextIndex < steps.size()) {
                                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return steps.get(nextIndex);
                                                        }
                                                        else {
                                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return null;
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                if (nextIndex < steps.size()) {
                                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return steps.get(nextIndex);
                                                }
                                                else {
                                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                 }
                            break;

                        case "<":
                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                    return null;
                                }
                                return null;
                            }
                            else {
                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                    Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");

                                    if (Double.valueOf(obj[0].toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                        else{
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }
                                                }
                                            }
                                        }
                                        else {

                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                           clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {

                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                            }
                            break;

                        case "<=":
                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                return null;
                            }
                            else {
                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                    Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");

                                    if (Double.valueOf(obj[0].toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                        else{
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                clearSurveyToSurveyPreference();
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;
                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                          clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                           clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {

                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                            }
                            break;

                        case ">=":
                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                return null;
                            }
                            else {
                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                    Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");

                                    if (Double.valueOf(obj[0].toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                        else{
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();

                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }
                                                }
                                            }
                                        }
                                        else {

                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));

                                        if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                    }
                                    else {
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    } else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {

                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                            }
                            break;

                        case "=":


                                if ((activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single"))
                                        || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("boolean")) {
                                    Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");
                                    if (obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {
                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                             return null;
                                        }
                                        else if (nextIndex < steps.size()) {
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                        else{
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                    else {
                                        //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {
                                            if (nextIndex < steps.size()) {
//                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
//                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
//                                                    return null;
//                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
//                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
//                                                   clearSurveyToSurveyPreference();
//                                                    return null;
//                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale") || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale") ||
                                            activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric") || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("timeInterval") ||
                                            activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height"))
                                    {
                                        if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue()))) {

                                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                                return null;
                                            }
                                            else if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            else{
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                        else {
                                            //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                            int nextIndex = steps.indexOf(previousStep) + 1;

                                            if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                    nextIndex += 1;
                                                    if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                        if (nextIndex < steps.size()) {
                                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                               clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return steps.get(nextIndex);
                                                        }
                                                        else {
                                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                               clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return null;
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                if (nextIndex < steps.size()) {
                                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                       clearSurveyToSurveyPreference();
                                                    }
                                                    return steps.get(nextIndex);
                                                }
                                                else{
                                                    if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                                return null;
                                            }
                                            else if(nextIndex >= activityQuestionStep.size() - 1 ){
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("") ) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            }
                                            if (nextIndex < steps.size()) {
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                }
                                                return steps.get(nextIndex);
                                            }
                                        }
                                        else {
                                            //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                            int nextIndex = steps.indexOf(previousStep) + 1;

                                            if(nextIndex >= activityQuestionStep.size() - 1 ){
                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    clearSurveyToSurveyPreference();
                                                    return null;
                                                }

                                                return null;
                                            }
                                            else {
                                                if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                    while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                        nextIndex += 1;
                                                        if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                            if (nextIndex < steps.size()) {
                                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                   clearSurveyToSurveyPreference();
                                                                }
                                                                return steps.get(nextIndex);
                                                            }
                                                            else {
                                                                if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                    clearSurveyToSurveyPreference();
                                                                }
                                                                return null;
                                                            }
                                                        }
                                                    }
                                                }
                                                else {

                                                    if (nextIndex < steps.size()) {
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                           clearSurveyToSurveyPreference();
                                                        }
                                                        return steps.get(nextIndex);
                                                    }
                                                    else{
                                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                            break;

                        case "!=":


                                if ((activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(previousStep)).getFormat().getSelectionStyle().equalsIgnoreCase("Single"))
                                        || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("boolean")) {
                                    Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResults().get("answer");
                                    if (!obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {
                                        int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                        if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                            return null;
                                        }
                                       else if (nextIndex < steps.size()) {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                return null;
                                            }
                                            return steps.get(nextIndex);
                                        }
                                        else {
                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                clearSurveyToSurveyPreference();
                                                return null;
                                            }
                                            return null;
                                        }
                                    }
                                    else {
                                        //   int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                        int nextIndex = steps.indexOf(previousStep) + 1;

                                        if (activityQuestionStep.get(nextIndex).isHidden()) {
                                            while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                nextIndex += 1;
                                                if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                    if (nextIndex < steps.size()) {
                                                        if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                            return null;
                                                        }
                                                        return steps.get(nextIndex);
                                                    } else {
                                                        if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                            clearSurveyToSurveyPreference();
                                                            return null;
                                                        }
                                                        return null;
                                                    }

                                                }
                                            }
                                        }
                                        else {
                                            if (nextIndex < steps.size()) {
//                                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
//                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
//                                                    return null;
//                                                }
                                                return steps.get(nextIndex);
                                            } else {
//                                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
//                                                   clearSurveyToSurveyPreference();
//                                                    return null;
//                                                }
                                                return null;
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("scale") || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("continuousScale") ||
                                            activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("numeric") || activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("timeInterval") ||
                                            activityQuestionStep.get(steps.indexOf(previousStep)).getResultType().equalsIgnoreCase("height")) {
                                        if (!Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString()).equals(Double.valueOf(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue()))) {

                                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                                return null;
                                            }
                                            else if (nextIndex < steps.size()) {
                                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
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
                                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                               clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return steps.get(nextIndex);
                                                        } else {
                                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return null;
                                                        }
                                                    }
                                                }
                                            }
                                            else {

                                                if (nextIndex < steps.size()) {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        // cs.saveAndFinish();
                                                        return null;
                                                    }
                                                    return steps.get(nextIndex);
                                                } else {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        if (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(previousStep)).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getValue())) {

                                            int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey()));
                                            if(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationStepKey().equalsIgnoreCase("0")) {
                                                return null;
                                            }
                                            else if (nextIndex < steps.size()) {
                                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return steps.get(nextIndex);
                                            } else {
                                                if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                    updateSurveyToSurveyPreference(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic());
                                                    return null;
                                                }
                                                return null;
                                            }
                                        }
                                        else {
                                            // int nextIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getDestinationFalseStepKey()));
                                            int nextIndex = steps.indexOf(previousStep) + 1;

                                            if (activityQuestionStep.get(nextIndex).isHidden()) {
                                                while (activityQuestionStep.get(nextIndex).isHidden()) {
                                                    nextIndex += 1;
                                                    if (!activityQuestionStep.get(nextIndex).isHidden()) {
                                                        if (nextIndex < steps.size()) {
                                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                clearSurveyToSurveyPreference();
                                                                return null;
                                                            }
                                                            return steps.get(nextIndex);
                                                        }
                                                        else {
                                                            if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                                clearSurveyToSurveyPreference();
                                                                // cs.saveAndFinish();
                                                                return null;
                                                            }
                                                            return null;
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                if (nextIndex < steps.size()) {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                       clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return steps.get(nextIndex);
                                                }
                                                else {
                                                    if (activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId() != null && !activityQuestionStep.get(steps.indexOf(previousStep)).getPreLoadLogic().getActivityId().equalsIgnoreCase("")) {
                                                        clearSurveyToSurveyPreference();
                                                        return null;
                                                    }
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                }

                            break;
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
                                                }
                                                else {
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
                                    }
                                    catch (Exception e) {
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
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        int nextIndex = steps.indexOf(step) - 1;

                                        if (nextIndex >= 0) {
                                            return steps.get(nextIndex);
                                        }
                                    }
                                }
                                else if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("grouped") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("")){
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
                                            }
                                            catch (Exception e) {
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
                                    }
                                    catch (Exception e) {
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
                if(activityQuestionStep.get(steps.indexOf(step)).getSourceQuestionKey() == null){ //activityQuestionStep.get(steps.indexOf(step)).getSourceQuestionKey().equalsIgnoreCase("")
                    if(activityQuestionStep.get(steps.indexOf(step)).getGroupId() != null && !activityQuestionStep.get(steps.indexOf(step)).getGroupId().equalsIgnoreCase("")) {
                        if ((steps.indexOf(step) - 1) < activityQuestionStep.size() - 1) {
                            if (activityQuestionStep.get(steps.indexOf(step) - 1).getGroupId() != null && !activityQuestionStep.get(steps.indexOf(step) - 1).getGroupId().equalsIgnoreCase("")) {
                                if (activityQuestionStep.get(steps.indexOf(step)).getGroupId().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(step) - 1).getGroupId())) {
                                    int index = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(step) - 1).getKey()));
                                    return steps.get(index);
                                }
                            }
                        }
                        else {
                            return null;
                        }
                    }
                }
                else {
                    int prevIndex = steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(steps.indexOf(step)).getSourceQuestionKey()));
                    if ((activityQuestionStep.get(prevIndex).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(prevIndex).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) || activityQuestionStep.get(prevIndex).getResultType().equalsIgnoreCase("boolean")) {
                        Object[] obj1 = (Object[]) taskResult.getStepResult(activityQuestionStep.get(prevIndex).getKey()).getResults().get("answer");
                        if (obj1[0].toString() != null) {
                            if (prevIndex >= 0) {
                                return steps.get(prevIndex);
                            }
                        } else {
                            int prevIndex2 = steps.indexOf(step) - 1;
                            if (activityQuestionStep.get(prevIndex2).isHidden()) {

                                if (taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex2).getSourceQuestionKey())
                                        && taskResult.getStepResult(activityQuestionStep.get(prevIndex2).getSourceQuestionKey()) != null) {

                                    if (prevIndex2 >= 0) {
                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                        return steps.get(prevIndex2);
                                    }
                                } else {
                                    int prevIndex1 = prevIndex - 1;
                                    for (int p = prevIndex1; p >= 0; p--) {
                                        if (activityQuestionStep.get(p).isHidden()) {
                                            // check the taskresult for the source question and decide
                                            if (taskResult.getResults().containsKey(activityQuestionStep.get(p).getKey())
                                                    && taskResult.getStepResult(activityQuestionStep.get(p).getKey()) != null) {
                                                // if task result is there then check for the flow if it is true flow or false flow

                                                switch (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getOperator()) {

                                                    case ">":
                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (Double.valueOf(obj[0].toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        } else {
                                                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }

                                                        break;
                                                    case "<":
                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (Double.valueOf(obj[0].toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        } else {
                                                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "<=":
                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (Double.valueOf(obj[0].toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        } else {
                                                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case ">=":
                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (Double.valueOf(obj[0].toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        } else {
                                                            if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }
                                                        break;

                                                    case "=":

                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }
                                                        else {
                                                            if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "!=":
                                                        if (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                            Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                            if (!obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        } else {
                                                            if (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                                if (p >= 0) {
                                                                    //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                                    return steps.get(p);
                                                                }
                                                            }
                                                        }

                                                        break;
                                                }
                                            }
                                        } else {
                                            return steps.get(p);
                                        }
                                    }
                                }
                            } else {
                                if (prevIndex >= 0) {
                                    Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                    return steps.get(prevIndex);
                                }

                            }
                        }
                    } else if (taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex).getKey())
                            && taskResult.getStepResult(activityQuestionStep.get(prevIndex).getKey()) != null) {

                        if (prevIndex >= 0) {
                            return steps.get(prevIndex);
                        }
                    }
                }
            }
            else{
                int prevIndex = steps.indexOf(step) - 1;
                if(activityQuestionStep.get(prevIndex).isHidden()){
                if((activityQuestionStep.get(prevIndex).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(prevIndex).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) || activityQuestionStep.get(prevIndex).getResultType().equalsIgnoreCase("boolean")) {
                    if (taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex).getKey())) {
                        Object[] obj1 = (Object[]) taskResult.getStepResult(activityQuestionStep.get(prevIndex).getKey()).getResults().get("answer");
                        if (obj1[0].toString() != null) {
                            if (prevIndex >= 0) {
                                //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                return steps.get(prevIndex);
                            } else {
                                return null;
                            }
                        }
                        else {
                            if (prevIndex > 0) {
                                prevIndex = prevIndex - 1;
                            }
                        }
                    }
                    else {
                        int prevIndex1 = prevIndex - 1;
                        for(int p = prevIndex1; p >= 0; p--){
                            if(activityQuestionStep.get(p).isHidden()){
                                // check the taskresult for the source question and decide
                                if(taskResult.getResults().containsKey(activityQuestionStep.get(p).getSourceQuestionKey())
                                        && taskResult.getStepResult(activityQuestionStep.get(p).getSourceQuestionKey()) != null){
                                    // if task result is there then check for the flow if it is true flow or false flow

                                    switch (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getOperator()){

                                        case ">":
                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (Double.valueOf(obj[0].toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }

                                            break;
                                        case "<":
                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (Double.valueOf(obj[0].toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }
                                            break;
                                        case  "<=":
                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (Double.valueOf(obj[0].toString())<= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }
                                            break;
                                        case ">=":
                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (Double.valueOf(obj[0].toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }
                                            break;

                                        case "=":

                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }
                                            break;
                                        case "!=":
                                            if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                                Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                                if (!obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
                                                }
                                            }else{
                                                if (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                    if (p >= 0) {
                                                        //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                        return steps.get(p);
                                                    }
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
                else if(taskResult.getResults().containsKey(activityQuestionStep.get(prevIndex).getKey())
                            && taskResult.getStepResult(activityQuestionStep.get(prevIndex).getKey()) != null){

                        if (prevIndex >= 0) {
                           //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                            return steps.get(prevIndex);
                        }
                    }
                    else {
                        int prevIndex1 = prevIndex - 1;
                        for(int p = prevIndex1; p >= 0; p--){
                          if(activityQuestionStep.get(p).isHidden()){
                              // check the taskresult for the source question and decide
                              if(taskResult.getResults().containsKey(activityQuestionStep.get(p).getSourceQuestionKey())
                                      && taskResult.getStepResult(activityQuestionStep.get(p).getSourceQuestionKey()) != null){
                                  // if task result is there then check for the flow if it is true flow or false flow

                                  switch (activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getOperator()){

                                      case ">":
                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (Double.valueOf(obj[0].toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) > Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }

                                          break;
                                      case "<":
                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (Double.valueOf(obj[0].toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) < Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }
                                          break;
                                      case  "<=":
                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (Double.valueOf(obj[0].toString())<= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) <= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }
                                          break;
                                      case ">=":
                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (Double.valueOf(obj[0].toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (Double.valueOf(taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString()) >= Double.valueOf(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }
                                          break;

                                      case "=":

                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }
                                          break;
                                      case "!=":
                                          if(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getResultType().equalsIgnoreCase("textChoice") && activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                              Object[] obj = (Object[]) taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResults().get("answer");
                                              if (!obj[0].toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
                                              }
                                          }else{
                                              if (!taskResult.getStepResult(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getKey()).getResult().toString().equalsIgnoreCase(activityQuestionStep.get(steps.indexOf(getStepWithIdentifier(activityQuestionStep.get(p).getSourceQuestionKey()))).getPreLoadLogic().getValue())) {

                                                  if (p >= 0) {
                                                      //Log.e("Krishna", "getStepBeforeStep: taskResult Hidden true return");
                                                      return steps.get(p);
                                                  }
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
            return null;
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

    public void updateSurveyToSurveyPreference(PreLoadLogic preLoadLogic){
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveyActivityId",
                        preLoadLogic.getActivityId());
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveySourceKey",
                        preLoadLogic.getDestinationStepKey());

        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveySourceKey2",
                        preLoadLogic.getDestinationStepKey());
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveyactivityVersion",
                        preLoadLogic.getActivityVersion());
    }

    public void clearSurveyToSurveyPreference(){
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveyActivityId",
                        "");
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveySourceKey",
                        "");

        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveySourceKey2",
                        "");
        AppController.getHelperSharedPreference()
                .writePreference(
                        mcontext,
                        "survetTosurveyactivityVersion",
                        "");
    }

}
