package com.harvard;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.offlineModule.auth.SyncAdapterManager;
import com.harvard.passcodeModule.PasscodeSetupActivity;
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

    private static final int UPGRADE = 100;
    private static final int APP_UPDATES = 100;
    private static final int PASSCODE_RESPONSE = 101;
    String version = "", updatedversion = "";
    private static AlertDialog alertDialog;
    AppVersionData appVersionData;
    VersionChecker versionChecker;
    String newVersion = "";
    private boolean force = false;
    private static final int RESULT_CODE_UPGRADE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Log.e("rohith",getPackageName());
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
            Log.e("Krishna", "onCreate: online  " + Locale.getDefault().getLanguage());
            if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.isLocaleChanged))
            {
              if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.CurrentLanguage)){
                  if(!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                      AppController.getLocalePreferenceHelper().writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                      AppController.getLocalePreferenceHelper().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                      AppController.getLocalePreferenceHelper().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                      Log.e("Krishna", "SplashActivity: latest localchanged,currentlanguage and previous language is null so writing fresh");
                  }
                  else {
                      AppController.getLocalePreferenceHelper().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                      AppController.getLocalePreferenceHelper().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                      Log.e("Krishna", "SplashActivity: latest localchanged,currentlanguage  is null so writing fresh it has previous language and value is "+AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.PreviousLanguage,null));
                  }
              }
              else{
                  if(!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                      AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                      AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                      Log.e("Krishna", "SplashActivity: latest localchanged, previous language is null so writing fresh but has currentlanguage and value is "+AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null));
                  }
                  else {
                      AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                      Log.e("Krishna", "onAppGotoForeground: latest, localchanged is null so writing fresh but has currentlanguage and previous lang value and value is "+AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null) + " Prev Lang "+AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.PreviousLanguage,null));
                  }
              }
            }
            else {
                //Compare current and previous locale if both are present
                if (!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.CurrentLanguage))
                {
                    if(!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        Log.e("Krishna", "SplashActivity: latest currentlanguage and previous language is null so writing fresh but has localchage value" +AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),"isLocaleChanged",false));
                    }
                    else {
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                        Log.e("Krishna", "SplashActivity: latest currentlanguage and previous language is null so writing fresh but has localchange value and previous Valule" +AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),"isLocaleChanged",false) + " " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.PreviousLanguage,null) );
                    }
                }
                else
                    {
                    if(!AppController.getLocalePreferenceHelper().getLocalePreferences(getApplicationContext()).contains(AppController.PreviousLanguage)){
                        AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                        AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                        Log.e("Krishna", "SplashActivity: latest  previous language is null so writing fresh but has localchange value and " +AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),"isLocaleChanged",false) + " Current value " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null) );
                    }
                    else {
                        // localchanged , current language, and previous exists
                        if (AppController.getLocalePreferenceHelper().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null).equalsIgnoreCase(Locale.getDefault().getLanguage())){
                            //locale update
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,false);
                            Log.e("Krishna", "SplashActivity: latest  islocal,current and previous language is has value and value is local changed " +AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),"isLocaleChanged",false) + " Current value " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null)+ " Previous value " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.PreviousLanguage,null) );
                            Log.e("Krishna", "SplashActivity: latest  is local,current and previous language is has value and value is local changed  current value is equal to  device/ app locale language " );
                        }else {//if(!AppController.getLocalePreferenceStringFor(getApplicationContext(),AppController.CurrentLanguage).equalsIgnoreCase(Locale.getDefault().getLanguage())){
                            AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.PreviousLanguage,Locale.getDefault().getLanguage());
                            AppController.getHelperSharedPreference().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage,Locale.getDefault().getLanguage());
                            AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),AppController.isLocaleChanged,true);
                            Log.e("Krishna", "SplashActivity: latest  islocal,current and previous language is has value and value is local changed " +AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),"isLocaleChanged",false) + " Current value " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null)+ " Previous value " +AppController.getHelperSharedPreference().readLocalePreference(getApplicationContext(),AppController.PreviousLanguage,null) );
                            Log.e("Krishna", "SplashActivity: latest  is local,current and previous language is has value and value is local changedcurrent value is not equal to  device/ app locale language  so changing language" );
                        }
                    }
                }

            }
        }
        else {
//            Log.e("Krishna", "onCreate: offline without condition offline Preferences  "+AppController.getLocalePreferenceHelper().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null));
//            Log.e("Krishna", "onCreate: offline without condition online Preferences  "+AppController.getHelperSharedPreference().readPreference(getApplicationContext(),AppController.CurrentLanguage,null));
//            if(AppController.getLocalePreferenceHelper().readLocalePreference(getApplicationContext(),AppController.CurrentLanguage,null) == null){
//                Log.e("krishna", "onCreate: writing offline preference");
//                AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),"isLocaleChanged",false);
//                AppController.getLocalePreferenceHelper().writeLocalePreference(getApplicationContext(),AppController.CurrentLanguage, Resources.getSystem().getConfiguration().locale.getLanguage());
//            }
//            if(AppController.getHelperSharedPreference().readPreference(getApplicationContext(),AppController.CurrentLanguage,null) != null && !AppController.getHelperSharedPreference().readPreference(getApplicationContext(),AppController.CurrentLanguage,null).equalsIgnoreCase(Resources.getSystem().getConfiguration().locale.getLanguage())){
//               AppController.getHelperSharedPreference().writeLocaleBoolPreference(getApplicationContext(),"isLocaleChanged",false);
//            }
//            Log.e("Krishna", "onCreate: offline  " + Locale.getDefault().getLanguage());


        }

        AppController.getHelperSharedPreference().writePreference(SplashActivity.this, getString(R.string.json_object_filter), "");

    }

    public void loadsplash() {
        new LongOperation().execute();
    }

    public void startmain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppController.getHelperSharedPreference().readPreference(getBaseContext(), getResources().getString(R.string.userid), "").equalsIgnoreCase("") && AppController.getHelperSharedPreference().readPreference(getBaseContext(), getResources().getString(R.string.verified), "").equalsIgnoreCase("true")) {
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
        if (requestCode == RESULT_CODE_UPGRADE) {
            if (versionChecker.currentVersion() != null && versionChecker.currentVersion().equalsIgnoreCase(newVersion)) {
                if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                    intent.putExtra("from", "signin");
                    startActivityForResult(intent, PASSCODE_RESPONSE);
                } else {
                    loadsplash();
                }
            } else {
//                if (force) {
//                    Toast.makeText(SplashActivity.this, "Please update the app to continue using", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
                    if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                        Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                        intent.putExtra("from", "signin");
                        startActivityForResult(intent, PASSCODE_RESPONSE);
                    } else {
                        loadsplash();
                    }
              //  }
            }
        } else if (requestCode == PASSCODE_RESPONSE) {
            startmain();
        }
    }

    private String currentVersion() {
        PackageInfo pInfo = null;
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
            if (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), getResources().getString(R.string.usepasscode), "").equalsIgnoreCase("yes")) {
                while (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("no")) {
                    if (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), "passcodeAnswered", "no").equalsIgnoreCase("yes")) {
                        break;
                    }else{

//                        if(PasscodeSetupActivity.pcActivity.isActivityTransitionRunning()){
//                            Log.e("KRishna", "doInBackground: activity is running"+PasscodeSetupActivity.pcActivity.isActivityTransitionRunning());
//                        }
                        Intent intent = new Intent(getApplicationContext(), PasscodeSetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            startmain();
        }

        @Override
        protected void onPreExecute() {

        }

    }


    @Override
    public void isUpgrade(boolean b, String newVersion, final boolean force) {
        this.newVersion = newVersion;
        this.force = force;
        //Log.e("Krishna", "isUpgrade: "+AppController.getHelperSharedPreference().readLocaleBooleanPreference(getApplicationContext(),AppController.isUpdateCancelledByUser,false));
        //if (b){
            /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
            alertDialogBuilder.setTitle("Upgrade");
            alertDialogBuilder.setMessage("Please upgrade the app to continue.").setCancelable(false)
                    .setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)), RESULT_CODE_UPGRADE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (force) {
                                Toast.makeText(SplashActivity.this, "Please update the app to continue using", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                                    Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                                    intent.putExtra("from", "signin");
                                    startActivityForResult(intent, PASSCODE_RESPONSE);
                                } else {
                                    loadsplash();
                                }
                            }
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();*/
        Log.e("Krishna ", "isUpgrade: new version in splashscreen is  "+newVersion);

        if(!newVersion.equalsIgnoreCase("na")) {
            if ((AppController.getLocalePreferenceHelper().getLocalePreferences(SplashActivity.this).contains(AppController.isUpdateCancelledByUser) &&
                    AppController.getLocalePreferenceHelper().readLocaleBooleanPreference(SplashActivity.this, AppController.isUpdateCancelledByUser, false).equals(false)) || !currentVersion().equalsIgnoreCase(newVersion)) {
                Intent intent = new Intent(SplashActivity.this, UpgradeAppActivity.class);
                intent.putExtra("forceUpgrade", force);
                startActivityForResult(intent, RESULT_CODE_UPGRADE);
                if (force) {
                    finish();
                }
            }
            else if (b) {
                Intent intent = new Intent(SplashActivity.this, UpgradeAppActivity.class);
                intent.putExtra("forceUpgrade", force);
                startActivityForResult(intent, RESULT_CODE_UPGRADE);
                if (force) {
                    finish();
                    // }
                }
            }
        }
        else {
            if (AppController.getHelperSharedPreference().readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes").equalsIgnoreCase("no")) {
                Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
                intent.putExtra("from", "signin");
                startActivityForResult(intent, PASSCODE_RESPONSE);
            } else {
                loadsplash();
            }
        }
    }

}
