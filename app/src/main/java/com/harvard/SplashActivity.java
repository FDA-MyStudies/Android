package com.harvard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.offlineModule.auth.SyncAdapterManager;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.userModule.NewPasscodeSetupActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.ConnectivityReceiver;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Version.VersionChecker;
import java.util.Locale;
import io.fabric.sdk.android.services.common.CommonUtils;

public class SplashActivity extends AppCompatActivity implements VersionChecker.Upgrade {

    private static final int PASSCODE_RESPONSE = 101;
    String version = "", updatedversion = "";
    AppVersionData appVersionData;
    VersionChecker versionChecker;
    String newVersion = "";
    private static final int RESULT_CODE_UPGRADE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        version = currentVersion();
        if (CommonUtils.isRooted(SplashActivity.this)) {
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.rooted_device), Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        } else {
            // sync registration
            SyncAdapterManager.init(this);
            AppController.keystoreInitilize(SplashActivity.this);
            versionChecker = new VersionChecker(SplashActivity.this, SplashActivity.this);
            versionChecker.execute();
        }
        if(ConnectivityReceiver.isConnected()){
            if (!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.isLocaleChanged))
            {
                if (!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.CurrentLanguage)){
                    if(!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                    }
                    else {
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                    }
                }
                else{
                    if(!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                    }
                    else {
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                    }
                }
            }
            else {
                //Compare current and previous locale if both are present
                if (!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.CurrentLanguage))
                {
                    if(!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);

                    }
                    else {
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());

                    }
                }
                else
                {
                    if(!SharedPreferenceHelper.getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                    }
                    else {
                        // localchanged , current language, and previous exists
                        if (SharedPreferenceHelper.readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null).equalsIgnoreCase(Locale.getDefault().getLanguage())){
                            //locale update
                            SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        }else {
                            SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                            SharedPreferenceHelper.writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                            SharedPreferenceHelper.writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,true);

                        }
                    }
                }

            }
        }
        SharedPreferenceHelper.writePreference(SplashActivity.this, getString(R.string.json_object_filter), "");

    }

    public void loadsplash() {
        new LongOperation().execute();
    }

    public void startmain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SharedPreferenceHelper.readPreference(getBaseContext(), getResources().getString(R.string.userid), "").equalsIgnoreCase("") && SharedPreferenceHelper.readPreference(getBaseContext(), getResources().getString(R.string.verified), "").equalsIgnoreCase("true")) {
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(getBaseContext(), StudyActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getBaseContext(), StandaloneActivity.class);
                        startActivity(intent);
                    }
                } else {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(SplashActivity.this);
                    settings.edit().clear().apply();
                    Log.e("krishna", "run: delete sharedpref");
                    // delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_" + pass);
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(SplashActivity.this, GatewayActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, StandaloneActivity.class);
                        startActivity(intent);
                    }
                }
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String signin = "signin";
        if (requestCode == RESULT_CODE_UPGRADE) {
            if (versionChecker.currentVersion() != null && versionChecker.currentVersion().equalsIgnoreCase(newVersion)) {
                if (SharedPreferenceHelper.readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                    intent.putExtra("from", signin);
                    startActivityForResult(intent, PASSCODE_RESPONSE);
                } else {
                    loadsplash();
                }
            }
            else {

                if (SharedPreferenceHelper.readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                    intent.putExtra("from", signin);
                    startActivityForResult(intent, PASSCODE_RESPONSE);
                } else {
                    loadsplash();
                }
            }
        }
        else if (requestCode == PASSCODE_RESPONSE) {
            startmain();
        }
    }

    private String currentVersion() {
        PackageInfo pInfo = new PackageInfo();
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName+"."+pInfo.versionCode;
    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... params) {
            if (SharedPreferenceHelper.readPreference(getApplicationContext(), getResources().getString(R.string.usepasscode), "").equalsIgnoreCase("yes")) {
                while (SharedPreferenceHelper.readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("no")) {
                    if (SharedPreferenceHelper.readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("yes")) {
                        break;
                    }
                }
            }
            return "Executed";
        }

        @Deprecated @Override
        protected void onPostExecute(String result) {
            startmain();
        }

        @Deprecated @Override
        protected void onPreExecute() {

        }

    }


    @Override
    public void isUpgrade(boolean b, String newVersion, final boolean force) {
        this.newVersion = newVersion;
        if(!newVersion.equalsIgnoreCase("na")) {
            if ((SharedPreferenceHelper.getLocalePreferences(SplashActivity.this).contains(AppController.isUpdateCancelledByUser) &&
                    SharedPreferenceHelper.readLocaleBooleanPreference(SplashActivity.this, AppController.isUpdateCancelledByUser, false).equals(false)) || !currentVersion().equalsIgnoreCase(newVersion)) {
                Intent intent = new Intent(SplashActivity.this, UpgradeAppActivity.class);
                intent.putExtra("forceUpgrade", force);
                startActivityForResult(intent, RESULT_CODE_UPGRADE);
            }
            else if (b) {
                Intent intent = new Intent(SplashActivity.this, UpgradeAppActivity.class);
                intent.putExtra("forceUpgrade", force);
                startActivityForResult(intent, RESULT_CODE_UPGRADE);

            }
            if (force) {
                finish();
            }
        }
        else {
            if (SharedPreferenceHelper.readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                intent.putExtra("from", "signin");
                startActivityForResult(intent, PASSCODE_RESPONSE);
            } else {
                loadsplash();
            }
        }
    }
}