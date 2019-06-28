package com.harvard.offlineModule.auth;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.harvard.R;
import com.harvard.offlineModule.model.OfflineData;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.StudyModulePresenter;
import com.harvard.studyAppModule.events.ProcessResponseEvent;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private Context mContext;
    private int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
    private DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
        dbServiceSubscriber = new DBServiceSubscriber();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e("rajeesh", "***** FDA SYN WORKING *********");
        getPendingData();
    }


    private void getPendingData() {

        try {
            dbServiceSubscriber = new DBServiceSubscriber();
            mRealm = AppController.getRealmobj(mContext);
            RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(mRealm);
            Log.e("rajeesh", "results *********" + results);
            if (results.size() > 0) {
                Log.e("rajeesh", "size *********" + results.size());
                for (int i = 0; i < results.size(); i++) {
                    String httpMethod = results.get(i).getHttpMethod().toString();
                    Log.e("rajeesh", "httpMethod *********" + httpMethod);
                    String url = results.get(i).getUrl().toString();
                    Log.e("rajeesh", "url *********" + url);
                    String normalParam = results.get(i).getNormalParam().toString();
                    Log.e("rajeesh", "normalParam *********" + normalParam);
                    String jsonObject = results.get(i).getJsonParam().toString();
                    Log.e("rajeesh", "jsonObject *********" + jsonObject);
                    String serverType = results.get(i).getServerType().toString();
                    updateServer(httpMethod, url, normalParam, jsonObject, serverType);
                    break;
                }
            }
            dbServiceSubscriber.closeRealmObj(mRealm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateServer(String httpMethod, String url, String normalParam, String jsonObjectString, String serverType) {

//        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonObjectString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (serverType.equalsIgnoreCase("registration")) {
            HashMap<String, String> header = new HashMap();
            header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
            header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

            UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(httpMethod, url, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, header, jsonObject, false, this);
            Log.e("rajeesh", "registration Service Call *********");
            updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
        } else if (serverType.equalsIgnoreCase("response")) {
            ProcessResponseEvent processResponseEvent = new ProcessResponseEvent();
            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent(httpMethod, url, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, null, jsonObject, false, this);

            processResponseEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            Log.e("rajeesh", "response ********* Service Call ");
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performProcessResponse(processResponseEvent);
        } else if (serverType.equalsIgnoreCase("wcp")) {
            Log.e("rajeesh", "wcp Service Call  *********");
        }
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        Log.e("rajeesh", "***** 9 *********");
        if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            Log.e("rajeesh", "***** 10 *********");
            dbServiceSubscriber.removeOfflineData(mContext);
            Log.e("rajeesh", "***** 11 *********");
            getPendingData();
            Log.e("rajeesh", "***** 12 *********");
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        Log.e("rajeesh", "***** 13 *********");
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            Log.e("rajeesh", "***** 14 *********");
            dbServiceSubscriber.removeOfflineData(mContext);
            Log.e("rajeesh", "***** 15 *********");
            getPendingData();
            Log.e("rajeesh", "***** 16 *********");
        }
    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        Log.e("rajeesh", "***** 17 *********" + responseCode);
        Log.e("rajeesh", "***** 17 *********" + errormsg);
        Log.e("rajeesh", "***** 17 *********" + statusCode);
        Log.e("rajeesh", "***** 17 *********" + response);
    }
}