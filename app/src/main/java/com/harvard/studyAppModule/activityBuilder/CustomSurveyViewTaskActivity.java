package com.harvard.studyAppModule.activityBuilder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.harvard.R;
import com.harvard.notificationModule.NotificationModuleSubscriber;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.StudyModulePresenter;
import com.harvard.studyAppModule.SurveyActivity;
import com.harvard.studyAppModule.SurveyCompleteActivity;
import com.harvard.studyAppModule.SurveyDashboardFragment;
import com.harvard.studyAppModule.activityBuilder.model.Choices;
import com.harvard.studyAppModule.activityBuilder.model.SurveyToSurveyModel;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityInfoData;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.studyAppModule.acvitityListModel.ActivityListData;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.studyAppModule.custom.Result.TaskRecordCustom;
import com.harvard.studyAppModule.custom.StepSwitcherCustom;
import com.harvard.studyAppModule.events.GetActivityInfoEvent;
import com.harvard.studyAppModule.studyModel.NotificationDbResources;
import com.harvard.studyAppModule.studyModel.Resource;
import com.harvard.studyAppModule.studyModel.ResponseInfoActiveTaskModel;
import com.harvard.studyAppModule.studyModel.StudyHome;
import com.harvard.userModule.webserviceModel.Studies;
import com.harvard.utils.ActiveTaskService;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ConnectionDetector;
import com.harvard.webserviceModule.apiHelper.HttpRequest;
import com.harvard.webserviceModule.apiHelper.Responsemodel;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.storage.database.TaskRecord;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.utils.FormatHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naveen Raj on 03/03/2017.
 */

public class CustomSurveyViewTaskActivity<T> extends AppCompatActivity implements StepCallbacks, ApiCall.OnAsyncRequestComplete {
    public static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
    public static final String ACTIVITYID = "ViewTaskActivity.ActivityId";
    public static final String STUDYID = "ViewTaskActivity.StudyId";
    public static final String RUNID = "ViewTaskActivity.RunId";
    public static final String ACTIVITY_STATUS = "ViewTaskActivity.Status";
    public static final String MISSED_RUN = "ViewTaskActivity.MissedRun";
    public static final String COMPLETED_RUN = "ViewTaskActivity.CompletedRun";
    public static final String TOTAL_RUN = "ViewTaskActivity.TotalRun";
    public static final String EXTRA_SURVEYOBJ = "ViewTaskActivity.ExtraSurveyobj";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String ACTIVITY_VERSION = "ViewTaskActivity.Activity_Version";
    private static final String RUN_START_DATE = "ViewTaskActivity.RunStartDate";
    private static final String RUN_END_DATE = "ViewTaskActivity.RunEndDate";
    private static final String BRANCHING = "ViewTaskActivity.branching";
    public static final String FREQUENCY_TYPE = "ViewTaskActivity.frequencyType";
    public final int ACTIVTTYINFO_RESPONSECODE = 101;

    private StepSwitcherCustom root;

    private Step currentStep;
    private Task task;
    private String StudyId;
    private String mActivityStatus;
    private TaskResult taskResult;
    ActivityObj mActivityObject;
    private Date currentRunStartDate;
    private Date currentRunEndDate;
    int currentStepPosition;
    Intent serviceintent;
    BroadcastReceiver receiver;
    boolean ActiveTaskReceiver = false;
    DBServiceSubscriber dbServiceSubscriber;
    String mActivityId;
    Realm realm;
    Studies mStudies;
    ActivityInfoData activityInfoData2;
    public static String RESOURCES = "resources";
    String resultValue;
    ArrayList<String> names = new ArrayList<>();
    boolean surveyTosurveyFlag = false;
    boolean flag = false;
    int count;
    ChoiceAnswerFormatCustom duplicateFormat;

    public static Intent newIntent(Context context, String surveyId, String studyId, int mCurrentRunId, String mActivityStatus, int missedRun, int completedRun, int totalRun, String mActivityVersion, Date currentRunStartDate, Date currentRunEndDate, String activityId, boolean branching, String frequencyType) {
        Intent intent = new Intent(context, CustomSurveyViewTaskActivity.class);
        intent.putExtra(EXTRA_STUDYID, surveyId);
        intent.putExtra(STUDYID, studyId);
        intent.putExtra(RUNID, mCurrentRunId);
        intent.putExtra(ACTIVITY_STATUS, mActivityStatus);
        intent.putExtra(MISSED_RUN, missedRun);
        intent.putExtra(COMPLETED_RUN, completedRun);
        intent.putExtra(TOTAL_RUN, totalRun);
        intent.putExtra(TOTAL_RUN, totalRun);
        intent.putExtra(ACTIVITY_VERSION, mActivityVersion);
        intent.putExtra(RUN_START_DATE, currentRunStartDate);
        intent.putExtra(RUN_END_DATE, currentRunEndDate);
        intent.putExtra(ACTIVITY_VERSION, mActivityVersion);
        intent.putExtra(ACTIVITYID, activityId);
        intent.putExtra(BRANCHING, branching);
        intent.putExtra(FREQUENCY_TYPE, frequencyType);
        return intent;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setResult(RESULT_CANCELED);
        super.setContentView(R.layout.stepswitchercustom);
        Toolbar toolbar = (Toolbar) findViewById(org.researchstack.backbone.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbServiceSubscriber = new DBServiceSubscriber();
        realm = AppController.getRealmobj(this);
        mActivityStatus = getIntent().getStringExtra(ACTIVITY_STATUS);
        mActivityId = getIntent().getStringExtra(ACTIVITYID);
        currentRunStartDate = (Date) getIntent().getSerializableExtra(RUN_START_DATE);
        currentRunEndDate = (Date) getIntent().getSerializableExtra(RUN_END_DATE);
        root = (StepSwitcherCustom) findViewById(R.id.container);


        mActivityObject = dbServiceSubscriber.getActivityBySurveyId((String) getIntent().getSerializableExtra(STUDYID), mActivityId, realm);
       /* for(int i=0;i<mActivityObject.getSteps().size();i++){
            if(mActivityObject.getSteps().get(i).getResultType().equalsIgnoreCase("boolean")){
                final RealmList<Choices> textChoices = new RealmList<>();
                Choices choices1 = new Choices();
                choices1.setText("Yes");
                choices1.setValue("true");
                choices1.setDetail("");
                choices1.setExclusive(false);
                textChoices.add(choices1);
                Choices choices2 = new Choices();
                choices2.setText("No");
                choices2.setValue("false");
                choices2.setDetail("");
                choices2.setExclusive(false);
                textChoices.add(choices2);
                final int finalI = i;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mActivityObject.getSteps().get(finalI).getFormat().setTextChoices(textChoices);
                        mActivityObject.getSteps().get(finalI).getFormat().setSelectionStyle("Single");
                    }
                });
            }
        }*/
        StepsBuilder stepsBuilder = new StepsBuilder(CustomSurveyViewTaskActivity.this, mActivityObject, getIntent().getBooleanExtra(BRANCHING, false), realm);

        task = ActivityBuilder.create(this, ((String) getIntent().getSerializableExtra(EXTRA_STUDYID)), stepsBuilder.getsteps(), mActivityObject, getIntent().getBooleanExtra(BRANCHING, false), dbServiceSubscriber);

        StudyId = (String) getIntent().getSerializableExtra(EXTRA_STUDYID);
        StepRecordCustom savedsteps = dbServiceSubscriber.getSavedSteps(task, realm);


        if (savedsteps != null) {
            /*SurveyToSurveyModel surveyToSurveyModel = dbServiceSubscriber.getSurveyToSurveyModelData(realm);
            if (surveyToSurveyModel != null) {
                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveyActivityId",
                                surveyToSurveyModel.getSurvetTosurveyActivityId());
                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveySourceKey",
                                surveyToSurveyModel.getSurvetTosurveySourceKey());
            }*/
            String survetTosurveyActivityId = AppController.getHelperSharedPreference()
                    .readPreference(CustomSurveyViewTaskActivity.this,
                            "survetTosurveyActivityId", "");
            String survetTosurveySourceKey = AppController.getHelperSharedPreference()
                    .readPreference(
                            CustomSurveyViewTaskActivity.this, "survetTosurveySourceKey", "");

            if (survetTosurveyActivityId != null && survetTosurveySourceKey != null && !survetTosurveyActivityId.equalsIgnoreCase("") && !survetTosurveySourceKey.equalsIgnoreCase("") && !survetTosurveyActivityId.equalsIgnoreCase("null") && !survetTosurveySourceKey.equalsIgnoreCase("null")) {
                currentStep = task.getStepWithIdentifier(survetTosurveySourceKey);
                taskResult = new TaskResult(task.getIdentifier());
                taskResult.setStartDate(new Date());
                flag = true;
                if (currentStep == null) {
                    currentStep = task.getStepWithIdentifier(savedsteps.getStepId());
                }
            } else {
                currentStep = task.getStepWithIdentifier(savedsteps.getStepId());

            }
            //currentStep = task.getStepWithIdentifier(savedsteps.getStepId());


            //currentStep = task.getStepWithIdentifier(savedsteps.getStepId());
            if (currentStep != null && flag == false) {
                RealmResults<StepRecordCustom> stepRecordCustoms = dbServiceSubscriber.getStepRecordCustom(task, realm);
                for (int i = 0; i < stepRecordCustoms.size(); i++) {
                    Log.e("hivsv", stepRecordCustoms.get(i).getStepId());
                    if (currentStep.getIdentifier().equalsIgnoreCase(stepRecordCustoms.get(i).getStepId())) {
                        TaskRecord taskRecord = new TaskRecord();
                        taskRecord.taskId = stepRecordCustoms.get(i).getTaskId();
                        taskRecord.started = stepRecordCustoms.get(i).getStarted();
                        taskRecord.completed = stepRecordCustoms.get(i).getCompleted();
                        taskResult = TaskRecordCustom.toTaskResult(taskRecord, stepRecordCustoms);
                    }
                }
            } else {
                taskResult = new TaskResult(task.getIdentifier());
                taskResult.setStartDate(new Date());
            }
        } else {

            String survetTosurveyActivityId = AppController.getHelperSharedPreference()
                    .readPreference(CustomSurveyViewTaskActivity.this,
                            "survetTosurveyActivityId", "");
            String survetTosurveySourceKey = AppController.getHelperSharedPreference()
                    .readPreference(
                            CustomSurveyViewTaskActivity.this, "survetTosurveySourceKey", "");

            if (survetTosurveyActivityId != null && survetTosurveySourceKey != null && !survetTosurveyActivityId.equalsIgnoreCase("") && !survetTosurveySourceKey.equalsIgnoreCase("") && !survetTosurveyActivityId.equalsIgnoreCase("null") && !survetTosurveySourceKey.equalsIgnoreCase("null")) {
                currentStep = task.getStepWithIdentifier(survetTosurveySourceKey);
                taskResult = new TaskResult(task.getIdentifier());
                taskResult.setStartDate(new Date());
                flag = true;
            } else {
                taskResult = new TaskResult(task.getIdentifier());
                taskResult.setStartDate(new Date());
            }


        }

        task.validateParameters();

        if (currentStep == null) {
            currentStep = task.getStepAfterStep(null, taskResult);
        }

        showStep(currentStep);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.survey_menu, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, s.length(), 0);
        item.setTitle(s);
        return true;
    }

    protected Step getCurrentStep() {
        return currentStep;
    }

    protected void showNextStep() {

        savestepresult(currentStep, true);
        if (currentStep.getClass() != InstructionStep.class && currentStep.getClass() != QuestionStep.class) {

            QuestionStepCustom currentStepPipe = (QuestionStepCustom) currentStep;
            String activityid = "" + currentStepPipe.getActivityId();
            String sourceKey = "" + currentStepPipe.getDestinationStepKey();
            String activityVersion = "" + currentStepPipe.getActivityVersion();
            AppController.getHelperSharedPreference()
                    .writePreference(
                            CustomSurveyViewTaskActivity.this,
                            "survetTosurveyActivityId",
                            "");
            AppController.getHelperSharedPreference()
                    .writePreference(
                            CustomSurveyViewTaskActivity.this,
                            "survetTosurveySourceKey",
                            "");
            if (activityid != null && !activityid.equalsIgnoreCase("") && !activityid.equalsIgnoreCase("null")) {
                surveyTosurveyFlag = true;

                saveAndFinish();

                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveyActivityId",
                                activityid);
                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveySourceKey",
                                sourceKey);

                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveySourceKey2",
                                sourceKey);
                AppController.getHelperSharedPreference()
                        .writePreference(
                                CustomSurveyViewTaskActivity.this,
                                "survetTosurveyactivityVersion",
                                activityVersion);
            }
        }
        if(surveyTosurveyFlag == false) {
            Step nextStep = task.getStepAfterStep(currentStep, taskResult);
            if (nextStep == null && surveyTosurveyFlag == false) {
                saveAndFinish();
            } else {
                showStep(nextStep);
                count = count + 1;
                Log.e("countt", String.valueOf(count));
            }
        }
    }

    private void savestepresult(Step currentStep, boolean savecurrent) {

        TaskRecordCustom taskRecord = new TaskRecordCustom();
        taskRecord.taskId = taskResult.getIdentifier();
        taskRecord.started = taskResult.getStartDate();
        taskRecord.completed = taskResult.getEndDate();

        for (StepResult stepResult : taskResult.getResults().values()) {
            if (stepResult != null && stepResult.getIdentifier().equalsIgnoreCase(currentStep.getIdentifier())) {
                StepRecordCustom stepRecord = new StepRecordCustom();

                int nextId;
                StepRecordCustom stepRecordCustom = dbServiceSubscriber.getStepRecordCustomById(taskResult.getIdentifier(), stepResult.getIdentifier(), realm);
                if (stepRecordCustom == null) {
                    Number currentIdNum = dbServiceSubscriber.getStepRecordCustomId(realm);
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                } else {
                    nextId = stepRecordCustom.id;
                }

                stepRecord.setId(nextId);
                stepRecord.taskRecordId = taskRecord.id;
                stepRecord.taskId = taskResult.getIdentifier();
                stepRecord.stepId = stepResult.getIdentifier();
                // includes studyId
                stepRecord.activityID = taskResult.getIdentifier().substring(0, taskResult.getIdentifier().lastIndexOf("_"));
                stepRecord.started = stepResult.getStartDate();
                stepRecord.completed = stepResult.getEndDate();
                stepRecord.runStartDate = currentRunStartDate;
                stepRecord.runEndDate = currentRunEndDate;
                stepRecord.studyId = "" + getIntent().getStringExtra(STUDYID);

                try {
                    if (stepResult.getAnswerFormat() == null) {
                        QuestionStepCustom step = (QuestionStepCustom) currentStep;
                        ChoiceAnswerFormatCustom format = (ChoiceAnswerFormatCustom) step.getAnswerFormat1();

                        duplicateFormat = format;
                        RealmList<Choices> textChoices = new RealmList<>();
                        for (int i = 0; i < format.getChoices().length; i++) {
                            Choices choices = new Choices();
                            choices.setValue(format.getChoices()[i].getText());
                            textChoices.add(choices);
                        }
                        stepRecord.setTextChoices(textChoices);
                    } else {
                        QuestionStep step = (QuestionStep) currentStep;
                        ChoiceAnswerFormat format = (ChoiceAnswerFormat) step.getAnswerFormat();
                        RealmList<Choices> textChoices = new RealmList<>();
                        for (int i = 0; i < format.getChoices().length; i++) {
                            Choices choices = new Choices();
                            choices.setValue(format.getChoices()[i].getText());
                            textChoices.add(choices);
                        }
                        stepRecord.setTextChoices(textChoices);
                    }
                } catch (Exception e) {
                    stepRecord.setTextChoices(null);
                }

                stepRecord.taskStepID = taskResult.getIdentifier() + "_" + stepResult.getIdentifier();
                if (!stepResult.getResults().isEmpty()) {
                    Gson gson = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601).create();
                    stepRecord.result = gson.toJson(stepResult.getResults());
                    resultValue = "";
                    resultValue = "" + stepResult.getResults().get("answer");
                }
                Log.e("Krishna", "savestepresult: identifier currentStep before updateStepRecord " + currentStep.getIdentifier());
                dbServiceSubscriber.updateStepRecord(this, stepRecord);


                if (dbServiceSubscriber.getStudyResource(getIntent().getStringExtra(STUDYID), realm) == null) {
                } else if (dbServiceSubscriber.getStudyResource(getIntent().getStringExtra(STUDYID), realm).getResources() == null) {
                } else {
                    StudyHome mStudyHome = null;
                    try {
                        mStudyHome = dbServiceSubscriber.getWithdrawalType(getIntent().getStringExtra(STUDYID), realm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RealmList<Resource> mResourceArrayList = dbServiceSubscriber.getStudyResource(getIntent().getStringExtra(STUDYID), realm).getResources();

                    if (mStudyHome != null && mResourceArrayList != null)
                        getResourceNotification(mResourceArrayList, mStudyHome);
                }


            }
        }
    }

    private void getResourceNotification(RealmList<Resource> mResourceArrayList, StudyHome mStudyHome) {
        for (int i = 0; i < mResourceArrayList.size(); i++) {
            if (mResourceArrayList.get(i).getAudience() != null && mResourceArrayList.get(i).getAudience().equalsIgnoreCase("Limited")) {
                if (mResourceArrayList.get(i).getAvailability().getAvailableDate().equalsIgnoreCase("")) {
                    StepRecordCustom stepRecordCustom = dbServiceSubscriber.getSurveyResponseFromDB(getIntent().getStringExtra(STUDYID) + "_STUDYID_" + mActivityId, mStudyHome.getAnchorDate().getQuestionInfo().getKey(), realm);
                    if (stepRecordCustom != null) {
                        Calendar startCalender = Calendar.getInstance();

                        Calendar endCalender = Calendar.getInstance();


                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(stepRecordCustom.getResult());
                            startCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get("answer")));
                            startCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getStartDays());
                            startCalender.set(Calendar.HOUR, 0);
                            startCalender.set(Calendar.MINUTE, 0);
                            startCalender.set(Calendar.SECOND, 0);
                            startCalender.set(Calendar.AM_PM, Calendar.AM);
                            NotificationDbResources notificationsDb = null;
                            RealmResults<NotificationDbResources> notificationsDbs = dbServiceSubscriber.getNotificationDbResources(mActivityId, getIntent().getStringExtra(STUDYID), RESOURCES, realm);
                            if (notificationsDbs != null && notificationsDbs.size() > 0) {
                                for (int j = 0; j < notificationsDbs.size(); j++) {
                                    if (notificationsDbs.get(j).getResourceId().equalsIgnoreCase(mResourceArrayList.get(i).getResourcesId())) {
                                        notificationsDb = notificationsDbs.get(j);
                                        break;
                                    }
                                }
                            }
                            if (notificationsDb == null) {
                                setRemainder(startCalender, mActivityId, getIntent().getStringExtra(STUDYID), mResourceArrayList.get(i).getNotificationText(), mResourceArrayList.get(i).getResourcesId());
                            }

                            endCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get("answer")));
                            endCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getEndDays());
                            endCalender.set(Calendar.HOUR, 11);
                            endCalender.set(Calendar.MINUTE, 59);
                            endCalender.set(Calendar.SECOND, 59);
                            endCalender.set(Calendar.AM_PM, Calendar.PM);


                            Calendar currentday = Calendar.getInstance();
                            currentday.set(Calendar.HOUR, 0);
                            currentday.set(Calendar.MINUTE, 0);
                            currentday.set(Calendar.SECOND, 0);
                            currentday.set(Calendar.AM_PM, Calendar.AM);


                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    private void setRemainder(Calendar startCalender, String activityId, String studyId, String notificationText, String resourceId) {
        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.generateAnchorDateLocalNotification(startCalender.getTime(), activityId, studyId, CustomSurveyViewTaskActivity.this, notificationText, resourceId);
    }

    protected void showPreviousStep() {
        boolean flag = false;

        String survetTosurveySourceKey2 = AppController.getHelperSharedPreference()
                .readPreference(
                        CustomSurveyViewTaskActivity.this, "survetTosurveySourceKey2", "");


        if (currentStep.getIdentifier().equalsIgnoreCase(survetTosurveySourceKey2)) {
            flag = true;
        } else {
            flag = false;

        }

        if (flag == false) {
            Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
            if (previousStep == null) {
                finish();
            } else {
                savestepresult(previousStep, false);
                showStep(previousStep);
            }
        }
    }

    private void showStep(Step step) {
        //branching logic here
        currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
                .getCurrent();
        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();
        QuestionStepCustom stepCustom = (QuestionStepCustom) currentStep;

        //updateActivityInfo(stepCustom.getPipingLogic().getActivityId(),stepCustom.getPipingLogic().getActivityVersion());
        mStudies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), realm);
/*
        new ResponseData(stepCustom.getDestinationStepKey(),stepCustom.getActivityId(),mStudies.getParticipantId(),step).execute();
        updateActivityInfo(stepCustom.getPipingLogic().getActivityId(),stepCustom.getPipingLogic().getActivityVersion());*/


        if(stepCustom!=null&&stepCustom.getPactivityId()!=null&&
                stepCustom.getPactivityVersion()!=null&&!stepCustom.getPactivityId().equalsIgnoreCase("")&&
                !stepCustom.getPactivityVersion().equalsIgnoreCase("")){
            //call apis
            updateActivityInfo(stepCustom.getPactivityId(),stepCustom.getPactivityVersion());

        }else {
            initiatePiping("", step, taskResult, step, currentStepPosition, newStepPosition, currentStep);
        }
        StepLayout stepLayout = getLayoutForStep(step);
        stepLayout.getLayout().setTag(org.researchstack.backbone.R.id.rsb_step_layout_id, step.getIdentifier());
        root.show(stepLayout,
                newStepPosition >= currentStepPosition
                        ? StepSwitcherCustom.SHIFT_LEFT
                        : StepSwitcherCustom.SHIFT_RIGHT);
        currentStep = step;
        AppController.getHelperHideKeyboard(this);
        resultValue = "";
    }

    protected StepLayout getLayoutForStep(Step step) {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);


        String survetTosurveyActivityId = AppController.getHelperSharedPreference()
                .readPreference(CustomSurveyViewTaskActivity.this,
                        "survetTosurveyActivityId", "");
        String survetTosurveySourceKey = AppController.getHelperSharedPreference()
                .readPreference(
                        CustomSurveyViewTaskActivity.this, "survetTosurveySourceKey", "");
        StepResult result;
        if (survetTosurveyActivityId != null && survetTosurveySourceKey != null && !survetTosurveyActivityId.equalsIgnoreCase("") && !survetTosurveySourceKey.equalsIgnoreCase("") && !survetTosurveyActivityId.equalsIgnoreCase("null") && !survetTosurveySourceKey.equalsIgnoreCase("null")) {
            // Get result from the TaskResult, can be null
            result = taskResult.getStepResult(survetTosurveySourceKey);
        } else {
            // Get result from the TaskResult, can be null
            result = taskResult.getStepResult(step.getIdentifier());
        }


        // Return the Class & constructor
        StepLayout stepLayout = createLayoutFromStep(step);
        stepLayout.initialize(step, result);
        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    @NonNull
    private StepLayout createLayoutFromStep(Step step) {
        try {
            Class cls = step.getStepLayoutClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveAndFinish() {
        taskResult.setEndDate(new Date());

        Intent intent = new Intent(CustomSurveyViewTaskActivity.this, SurveyCompleteActivity.class);
        intent.putExtra(EXTRA_TASK_RESULT, taskResult);
        intent.putExtra(STUDYID, getIntent().getStringExtra(STUDYID));
        intent.putExtra(EXTRA_STUDYID, getIntent().getStringExtra(EXTRA_STUDYID));
        intent.putExtra(RUNID, getIntent().getIntExtra(RUNID, 0));
        intent.putExtra(MISSED_RUN, getIntent().getStringExtra(MISSED_RUN));
        intent.putExtra(COMPLETED_RUN, getIntent().getIntExtra(COMPLETED_RUN, 0));
        intent.putExtra(TOTAL_RUN, getIntent().getIntExtra(TOTAL_RUN, 0));
        intent.putExtra(ACTIVITY_VERSION, getIntent().getStringExtra(ACTIVITY_VERSION));
        intent.putExtra(FREQUENCY_TYPE, getIntent().getStringExtra(FREQUENCY_TYPE));
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            finish();
            //if that id comes then finish and launch other activity
        } else if (requestCode == 123 && resultCode == RESULT_CANCELED) {
            this.recreate();
        }
    }

    @Override
    protected void onPause() {
        AppController.getHelperHideKeyboard(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            notifyStepOfBackPress();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            showConfirmExitDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        notifyStepOfBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
    }

    private void notifyStepOfBackPress() {
        StepLayout currentStepLayout = (StepLayout) findViewById(org.researchstack.backbone.R.id.rsb_current_step);
        currentStepLayout.isBackEventConsumed();
        count = count - 1;
        Log.e("countt", String.valueOf(count));

        if (isMyServiceRunning(ActiveTaskService.class)) {
            try {
                if (serviceintent != null && receiver != null) {
                    stopService(serviceintent);
                    unregisterReceiver(receiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.clearSurveyToSurveyModelData(realm);

        String survetTosurveyActivityId = AppController.getHelperSharedPreference()
                .readPreference(CustomSurveyViewTaskActivity.this,
                        "survetTosurveyActivityId", "");
        String activityVersion = AppController.getHelperSharedPreference()
                .readPreference(CustomSurveyViewTaskActivity.this,
                        "survetTosurveyactivityVersion", "");

        String survetTosurveySourceKey = AppController.getHelperSharedPreference()
                .readPreference(
                        CustomSurveyViewTaskActivity.this, "survetTosurveySourceKey", "");
        SurveyToSurveyModel surveyToSurveyModel = new SurveyToSurveyModel();
        surveyToSurveyModel.setSurvetTosurveyActivityId(survetTosurveyActivityId);
        surveyToSurveyModel.setSurvetTosurveyactivityVersion(activityVersion);
        surveyToSurveyModel.setSurvetTosurveySourceKey(survetTosurveySourceKey);

        dbServiceSubscriber.saveSurveyTosurveyData(this, surveyToSurveyModel);
        AppController.getHelperSharedPreference()
                .writePreference(
                        CustomSurveyViewTaskActivity.this,
                        "survetTosurveyActivityId",
                        "");
        AppController.getHelperSharedPreference()
                .writePreference(
                        CustomSurveyViewTaskActivity.this,
                        "survetTosurveySourceKey",
                        "");

        dbServiceSubscriber.closeRealmObj(realm);


       /* AppController.getHelperSharedPreference()
                .writePreference(
                        CustomSurveyViewTaskActivity.this,
                        "survetTosurveySourceKey2",
                        "");*/
        if (isMyServiceRunning(ActiveTaskService.class)) {
            try {
                if (serviceintent != null && receiver != null) {
                    stopService(serviceintent);
                    unregisterReceiver(receiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public void serviceStarted(BroadcastReceiver receiver, Intent serviceintent) {
        this.receiver = receiver;
        this.serviceintent = serviceintent;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        onSaveStepResult(step.getIdentifier(), result);

        onExecuteStepAction(action);
    }

    protected void onSaveStepResult(String id, StepResult result) {
        taskResult.setStepResultForStepIdentifier(id, result);
    }

    protected void onExecuteStepAction(int action) {
        if (action == StepCallbacks.ACTION_NEXT) {
            showNextStep();
        } else if (action == StepCallbacks.ACTION_PREV) {

            showPreviousStep();

        } else if (action == StepCallbacks.ACTION_END) {
            showConfirmExitDialog();
        } else if (action == StepCallbacks.ACTION_NONE) {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        } else {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments");
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void showConfirmExitDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).setTitle(R.string.app_name)
                .setMessage(R.string.csv_task_exit_activity)
                .setPositiveButton(R.string.csv_task_endtask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        AppController.getHelperSharedPreference()
                                .writePreference(
                                        CustomSurveyViewTaskActivity.this,
                                        "survetTosurveyActivityId",
                                        "");
                        AppController.getHelperSharedPreference()
                                .writePreference(
                                        CustomSurveyViewTaskActivity.this,
                                        "survetTosurveySourceKey",
                                        "");
                        AppController.getHelperSharedPreference()
                                .writePreference(
                                        CustomSurveyViewTaskActivity.this,
                                        "survetTosurveySourceKey2",
                                        "");
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onCancelStep() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void addformquestion(QuestionStep questionStep, String identifier, String stepIdentifier) {
        String survayId[] = StudyId.split("_STUDYID_");
        ActivityObj surveyObject = dbServiceSubscriber.getActivityBySurveyId(getIntent().getStringExtra(STUDYID), survayId[1].substring(0, survayId[1].lastIndexOf("_")), realm);
        Steps step = dbServiceSubscriber.getSteps(identifier, realm);

        if (surveyObject != null && step != null) {
            Steps steps = dbServiceSubscriber.updateSteps(step, questionStep.getIdentifier(), realm);
            RealmList<Steps> formstep = surveyObject.getSteps();
            realm.beginTransaction();
            for (int i = 0; i < formstep.size(); i++) {
                if (formstep.get(i).getType().equalsIgnoreCase("form") && formstep.get(i).getKey().equalsIgnoreCase(stepIdentifier)) {
                    formstep.get(i).getSteps().add(steps);
                }
            }
            realm.commitTransaction();
        } else {
            Toast.makeText(this, getResources().getString(R.string.step_couldnt_add), Toast.LENGTH_SHORT).show();
        }
    }


    public void initiatePiping(String identifier, Step currentStep,
                               TaskResult taskResult, Step step, int currentStepPosition, int newStepPosition, Step currentStep1) {

        if (step.getClass() != QuestionStep.class) {
            QuestionStepCustom nextStepPipe = (QuestionStepCustom) step;
            ((QuestionStepCustom) currentStep).isPPing();
            if (((QuestionStepCustom) currentStep).isPPing) {

                if (taskResult!=null
                        && taskResult.getResults()!=null&&taskResult.getResults().size()!=0
                        && taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey())!=null
                        && taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResult() != null
                        && !taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResult().toString().isEmpty()) {
                    Object val = taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResults().get("answer");

                    if(task.getStepWithIdentifier(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getIdentifier().equalsIgnoreCase(((QuestionStepCustom) currentStep).getPipeSocuceKey())){
                        Step step1 = task.getStepWithIdentifier(((QuestionStepCustom) currentStep).getPipeSocuceKey());
                        QuestionStepCustom currentStepPipe = (QuestionStepCustom) step1;
                        ((QuestionStepCustom) currentStep1).setGetPipingChoices(currentStepPipe.getGetPipingChoices());
                    }
                    String answer = "";
                    Object o = val;
                    if (o instanceof Object[]) {
                        Object[] objects = (Object[]) o;
                        if (objects[0] instanceof String) {
                            answer = "" + ((String) objects[0]);
                            if (((QuestionStepCustom) currentStep1).getGetPipingChoices() != null) {
                                for (int i = 0; i < ((QuestionStepCustom) currentStep1).getGetPipingChoices().size(); i++) {
                                    if (((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getValue().equals(answer)) {
                                        answer = ((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getText();
                                    }
                                }
                            }

                        } else if (objects[0] instanceof Integer) {
                            answer = "" + ((int) objects[0]);
                            if (((QuestionStepCustom) currentStep1).getGetPipingChoices() != null) {
                                for (int i = 0; i < ((QuestionStepCustom) currentStep1).getGetPipingChoices().size(); i++) {
                                    if (((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getValue().equals(answer)) {
                                        answer = ((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getText();
                                    }
                                }
                            }
                        }
                    } else {
                        answer = taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResult().toString();

                        if (((QuestionStepCustom) currentStep1).getGetPipingChoices() != null) {
                            for (int i = 0; i < ((QuestionStepCustom) currentStep1).getGetPipingChoices().size(); i++) {
                                if (((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getValue().equals(answer)) {
                                    answer = ((QuestionStepCustom) currentStep1).getGetPipingChoices().get(i).getText();
                                    break;
                                }else {
                                    answer = taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResult().toString();

                                }
                            }
                        }else {
                            answer = taskResult.getStepResult(((QuestionStepCustom) currentStep).getPipeSocuceKey()).getResult().toString();

                        }

                    }
                    String replaceString = step.getTitle().replace(((QuestionStepCustom) currentStep).getPipingSnippet(), answer);
                    nextStepPipe.setPipingSnippet(answer);
                    step.setTitle("");
                    step.setTitle(replaceString);
                }
            }
        }
    }


    private void updateActivityInfo(String activityId,String activityVersion) {
        AppController.getHelperProgressDialog().showProgress(CustomSurveyViewTaskActivity.this, "", "", false);


        GetActivityInfoEvent getActivityInfoEvent = new GetActivityInfoEvent();
        HashMap<String, String> header = new HashMap();
        //String url = "https://632adb4b713d41bc8e790540.mockapi.io/getactivitym/getactivity";
        //String url = "https://63202cce9f82827dcf26789a.mockapi.io/getActivityM";
        String url = URLs.ACTIVITY
                + "?studyId="
                + getIntent().getStringExtra(STUDYID)
                + "&activityId="
                + activityId
                + "&activityVersion="
                + activityVersion;
        WCPConfigEvent wcpConfigEvent =
                new WCPConfigEvent(
                        "get",
                        url,
                        ACTIVTTYINFO_RESPONSECODE,
                        CustomSurveyViewTaskActivity.this,
                        ActivityInfoData.class,
                        null,
                        header,
                        null,
                        false,
                        this);

        getActivityInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetActivityInfo(getActivityInfoEvent);
    }

    public void surveyTosurveyPiping(){

    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
            AppController.getHelperProgressDialog().dismissDialog();
              ActivityInfoData activityInfoData = (ActivityInfoData) response;
              activityInfoData2 = activityInfoData;
            QuestionStepCustom stepCustom = (QuestionStepCustom) currentStep;
            mStudies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), realm);
            new ResponseData(stepCustom.getPsourceQuestionKey(),stepCustom.getPactivityId(),mStudies.getParticipantId(),currentStep).execute();

        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();

    }


    private class ResponseData extends AsyncTask<String, Void, String> {

        String participateId;
        ResponseInfoActiveTaskModel responseInfoActiveTaskModel;
        String response = null;
        String responseCode = null;
        String stepId;
        String activityId;
        String participtantID;
        int position;
        Responsemodel mResponseModel;
        Step step;
        ResponseData(String stepId,String activityId,String participtantID,Step step) {
        this.stepId = stepId;
        this.activityId = activityId;
        this.participtantID = participtantID;
            this.step = step;

        }

        @Override
        protected String doInBackground(String... params) {

        ConnectionDetector connectionDetector = new ConnectionDetector(CustomSurveyViewTaskActivity.this);

        if (connectionDetector.isConnectingToInternet()) {
            mResponseModel = HttpRequest.getRequest(URLs.PROCESSRESPONSEDATAPIPING +"/BTC/"+getIntent().getStringExtra(STUDYID) + "/mobileappstudy-selectRows.api?" + "query.queryName="+activityId+"&"+"query.columns="+stepId+"&"+"participantId="+participateId, new HashMap<String, String>(), "Response");
            //mResponseModel = HttpRequest.getRequest("https://hpresp-stage.lkcompliant.net/BTC/LIMITOPEN001/mobileappstudy-selectRows.api?query.queryName=imageque&query.columns=text&participantId=dcb2f1938fd6b64c5e039ff476629a49", new HashMap<String, String>(), "Response");

            responseCode = mResponseModel.getResponseCode();
            response = mResponseModel.getResponseData();
            if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
                response = "timeout";
            } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
                response = "error";
            } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
                response = "No data";
            } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase("http_not_ok")) {
                response = "client error";
            } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase("http_not_ok")) {
                response = "server error";
            } else if (response.equalsIgnoreCase("http_not_ok")) {
                response = "Unknown error";
            } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
                response = "session expired";
            } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
                response = response;
            } else {
                response = getResources().getString(R.string.unknown_error);
            }
        }
        return response;
    }

        @Override
        protected void onPreExecute() {
        super.onPreExecute();
        AppController.getHelperProgressDialog().showProgress(CustomSurveyViewTaskActivity.this, "", "", false);
       /* id = responseInfoActiveTaskModel.getActivityId();
        stepKey = responseInfoActiveTaskModel.getKey();
        ActivityListData activityListData = dbServiceSubscriber.getActivities(studyId, mRealm);
        if (activityListData != null) {
            RealmList<ActivitiesWS> activitiesWSes = activityListData.getActivities();
            for (int i = 0; i < activitiesWSes.size(); i++) {
                if (activitiesWSes.get(i).getActivityId().equalsIgnoreCase(responseInfoActiveTaskModel.getActivityId())) {
                    if (activitiesWSes.get(i).getType().equalsIgnoreCase("task")) {
                        id = responseInfoActiveTaskModel.getActivityId() + responseInfoActiveTaskModel.getKey();
                        queryParam = "%22count%22,%22Created%22,%22duration%22";
                    }
                }
            }
        }*/
    }

        @Override
        protected void onPostExecute(String response) {
        super.onPostExecute(response);
            AppController.getHelperProgressDialog().dismissDialog();

            if (response != null) {
            if (response.equalsIgnoreCase("session expired")) {
                AppController.getHelperProgressDialog().dismissDialog();
                AppController.getHelperSessionExpired(CustomSurveyViewTaskActivity.this, "session expired");
            } else if (response.equalsIgnoreCase("timeout")) {
                 AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(CustomSurveyViewTaskActivity.this,  getResources().getString(R.string.survey_dashboard_fragment_connection_timeout), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(responseCode) == 500) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(mResponseModel.getResponseData()));
                    String exception = String.valueOf(jsonObject.get("exception"));
                    if (exception.contains("Query or table not found")) {

                             AppController.getHelperProgressDialog().dismissDialog();

                    } else {
                         AppController.getHelperProgressDialog().dismissDialog();
                    }
                } catch (JSONException e) {
                     AppController.getHelperProgressDialog().dismissDialog();
                    e.printStackTrace();
                }
            } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
                try {

                     JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
                      ArrayList<String>keyValues = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                          if(stepId.equalsIgnoreCase("text")){
                              keyValues.add(jsonObject1.getString("Key"));
                          }else if(stepId.equalsIgnoreCase("textChoice")){
                              keyValues.add(jsonObject1.getString("Key"));

                          }else if(stepId.equalsIgnoreCase("valuePicker")){
                              keyValues.add(jsonObject1.getString("Key"));

                          }else if(stepId.equalsIgnoreCase("textScale")){
                              keyValues.add(jsonObject1.getString("Key"));

                          }else {
                              //do nothing
                          }



                    }
                    Log.e("buuid", String.valueOf(keyValues.get(keyValues.size()-1)));
                    Log.e("buuid",stepId);
                    QuestionStepCustom nextStepPipe = (QuestionStepCustom) step;
                    for(int k=0;k<activityInfoData2.getActivity().getSteps().size();k++){
                        if(activityInfoData2.getActivity().getSteps().get(k).getResultType().equalsIgnoreCase("text")){
                            String replaceString = step.getTitle().replace(((QuestionStepCustom) currentStep).getPpipingSnippet(), keyValues.get(keyValues.size()-1));
                            nextStepPipe.setPipingSnippet(keyValues.get(keyValues.size()-1));
                            step.setTitle("");
                            step.setTitle(replaceString);
                        }else if(activityInfoData2.getActivity().getSteps().get(k).getResultType().equalsIgnoreCase("textChoice")){
                            RealmList<Choices> textChoices = activityInfoData2.getActivity().getSteps().get(k).getFormat().getTextChoices();
                            String answer="";
                            for(int i=0;i<textChoices.size();i++){
                                if(textChoices.get(i).getValue().equalsIgnoreCase(keyValues.get(keyValues.size()-1))){
                                    answer = textChoices.get(i).getText();
                                }
                            }

                            String replaceString = step.getTitle().replace(((QuestionStepCustom) currentStep).getPpipingSnippet(), answer);
                            nextStepPipe.setPipingSnippet(keyValues.get(keyValues.size()-1));
                            step.setTitle("");
                            step.setTitle(replaceString);
                        }else if(activityInfoData2.getActivity().getSteps().get(k).getResultType().equalsIgnoreCase("valuePicker")){
                            RealmList<Choices> textChoices = activityInfoData2.getActivity().getSteps().get(k).getFormat().getTextChoices();
                            String answer="";
                            for(int i=0;i<textChoices.size();i++){
                                if(textChoices.get(i).getValue().equalsIgnoreCase(keyValues.get(keyValues.size()-1))){
                                    answer = textChoices.get(i).getText();
                                }
                            }

                            String replaceString = step.getTitle().replace(((QuestionStepCustom) currentStep).getPpipingSnippet(), answer);
                            nextStepPipe.setPipingSnippet(keyValues.get(keyValues.size()-1));
                            step.setTitle("");
                            step.setTitle(replaceString);
                        }else if(activityInfoData2.getActivity().getSteps().get(k).getResultType().equalsIgnoreCase("textScale")){
                            RealmList<Choices> textChoices = activityInfoData2.getActivity().getSteps().get(k).getFormat().getTextChoices();
                            String answer="";
                            for(int i=0;i<textChoices.size();i++){
                                if(textChoices.get(i).getValue().equalsIgnoreCase(keyValues.get(keyValues.size()-1))){
                                    answer = textChoices.get(i).getText();
                                }
                            }

                            String replaceString = step.getTitle().replace(((QuestionStepCustom) currentStep).getPpipingSnippet(), answer);
                            nextStepPipe.setPipingSnippet(keyValues.get(keyValues.size()-1));
                            step.setTitle("");
                            step.setTitle(replaceString);
                        }else {

                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                     AppController.getHelperProgressDialog().dismissDialog();
                }
            } else {
                 AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(CustomSurveyViewTaskActivity.this,  getResources().getString(R.string.survey_dashboard_fragment_unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
            }
        } else {
             AppController.getHelperProgressDialog().dismissDialog();
            Toast.makeText(CustomSurveyViewTaskActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
        }
    }
    }
}
