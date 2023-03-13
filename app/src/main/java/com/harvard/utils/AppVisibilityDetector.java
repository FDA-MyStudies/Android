package com.harvard.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.harvard.SplashActivity;
import com.harvard.UpgradeAppActivity;

import java.util.List;
import java.util.Locale;

/**
 * Created by Naveen Raj on 05/06/2017.
 */

public class AppVisibilityDetector {
    private static boolean DEBUG = false;
    private static final String TAG = "AppVisibilityDetector";
    private static AppVisibilityCallback sAppVisibilityCallback;
    private static boolean sIsForeground = false;
    private static Handler sHandler;
    private static final int MSG_GOTO_FOREGROUND = 1;
    private static final int MSG_GOTO_BACKGROUND = 2;
    public static Activity currentActivity;

    public static void init(final Application app, AppVisibilityCallback appVisibilityCallback) {
        checkIsMainProcess(app);
        sAppVisibilityCallback = appVisibilityCallback;
        app.registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());

        sHandler = new Handler(app.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GOTO_FOREGROUND:
                        if (DEBUG) {
                            Log.d(TAG, "handleMessage(MSG_GOTO_FOREGROUND)");
                        }
                        performAppGotoForeground();
                        break;
                    case MSG_GOTO_BACKGROUND:
                        if (DEBUG) {
                            Log.d(TAG, "handleMessage(MSG_GOTO_BACKGROUND)");
                        }
                        performAppGotoBackground();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private static void checkIsMainProcess(Application app) {
        ActivityManager activityManager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        String currProcessName = null;
        int currPid = android.os.Process.myPid();
        //find the process name
        for (RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.pid == currPid) {
                currProcessName = processInfo.processName;
            }
        }

    }

    private static void performAppGotoForeground() {
        if (!sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = true;
            sAppVisibilityCallback.onAppGotoForeground();
        }
    }

    private static void performAppGotoBackground() {
        if (sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = false;
            sAppVisibilityCallback.onAppGotoBackground();
        }
    }

    public interface AppVisibilityCallback {
        void onAppGotoForeground();

        void onAppGotoBackground();
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    private static class AppActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        int activityDisplayCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityCreated");
            }
            currentActivity = activity;
            // checkForLocaleChanges();
            Log.d("Krishna", activity.getClass().getName() + " onActivityCreated");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_FOREGROUND);
            }
            activityDisplayCount++;

            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityStarted "
                        + " activityDisplayCount: " + activityDisplayCount);
            }
            Log.d("Krishna", activity.getClass().getName() + " onActivityStarted "
                    + " activityDisplayCount: " + activityDisplayCount);
            currentActivity = activity;
            // checkForLocaleChanges();
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityResumed");
            }
            Log.d("Krishna", activity.getClass().getName() + " onActivityResumed");
            currentActivity = activity;
            if(!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.isUpdateCancelledByUser)) {
                AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isUpdateCancelledByUser, false);
            }
            checkForLocaleChanges();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityPaused");
            }
            Log.d("Krishna", activity.getClass().getName() + " onActivityPaused");
            //checkForLocaleChanges();

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivitySaveInstanceState");
            }

            //checkForLocaleChanges();
            Log.d("Krishna", activity.getClass().getName() + " onActivitySaveInstanceState");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount > 0) {
                activityDisplayCount--;
            }

            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_BACKGROUND);
            }

            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityStopped "
                        + " activityDisplayCount: " + activityDisplayCount);
            }
            Log.d("Krishna", activity.getClass().getName() + " onActivityStopped "
                    + " activityDisplayCount: " + activityDisplayCount);
            // checkForLocaleChanges();

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, activity.getClass().getName() + " onActivityDestroyed");
            }
            Log.d("Krishna", activity.getClass().getName() + " onActivityStopped "
                    + " activityDisplayCount: " + activityDisplayCount);


        }
    }

    private static void checkForLocaleChanges(){
        if(ConnectivityReceiver.isConnected()) {
            // Latest Changes

            if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.isLocaleChanged))
            {
                if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.CurrentLanguage)) {
                    if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.PreviousLanguage)) {
                        AppController.getLocalePreferenceHelper().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, Locale.getDefault().getLanguage());
                        AppController.getLocalePreferenceHelper().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, Locale.getDefault().getLanguage());
                        AppController.getLocalePreferenceHelper().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);

                    } else {

                        AppController.getLocalePreferenceHelper().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);
                        AppController.getLocalePreferenceHelper().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, Locale.getDefault().getLanguage());
                    }
                }
                else {
                    if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.PreviousLanguage)) {

                        AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);
                    } else {
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);
                    }
                }
            }
            else {
                //Compare current and previous locale if both are present
                if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.CurrentLanguage)) {
                    if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.PreviousLanguage)) {
                        AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);

                    } else {
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);
                        AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, Locale.getDefault().getLanguage());
                    }
                } else {
                    if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getCurrentActivity().getApplicationContext()).contains(AppController.PreviousLanguage)) {
                        AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);

                    } else {
                        // localchanged , current language, and previous exists
                        if (AppController.getLocalePreferenceHelper().readLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, null).equalsIgnoreCase(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                            //locale update
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, false);
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isRefreshNeeded, false);
                        } else if (!AppController.getLocalePreferenceHelper().readLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, null).equalsIgnoreCase(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                            AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, AppController.getLocalePreferenceHelper().readLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, null));
                            AppController.getHelperSharedPreference().writeLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, Resources.getSystem().getConfiguration().locale.getLanguage());
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isLocaleChanged, true);
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getCurrentActivity().getApplicationContext(), AppController.isRefreshNeeded, true);
                        }
                    }
                }

            }
        }
        else{
            Log.e("Krishna", "AppVisibilityDetector: latest, localchanged is null so writing fresh but has currentlanguage and previous lang value and value is " + AppController.getHelperSharedPreference().readLocalePreference(getCurrentActivity().getApplicationContext(), AppController.CurrentLanguage, null) + " Prev Lang " + AppController.getHelperSharedPreference().readLocalePreference(getCurrentActivity().getApplicationContext(), AppController.PreviousLanguage, null));
        }
    }
}