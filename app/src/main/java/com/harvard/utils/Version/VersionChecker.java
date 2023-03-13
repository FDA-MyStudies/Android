package com.harvard.utils.Version;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.harvard.AppConfig;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.HttpRequest;
import com.harvard.webserviceModule.apiHelper.Responsemodel;

import java.io.StringReader;
import java.net.HttpURLConnection;

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;
    private boolean force = false;
    private Upgrade upgrade;
    private Context context;
    private String versionUrl = URLs.BASE_URL_WCP_SERVER + "versionInfo";
    public final static String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=" + AppConfig.PackageName + "&hl=en";

    public VersionChecker(Upgrade upgrade, Context context) {
        this.upgrade = upgrade;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        newVersion = currentVersion();
        VersionModel versionModel;
        try {
            Responsemodel responsemodel = HttpRequest.getRequest(versionUrl, null, "WCP");

            if (responsemodel.getResponseCode().equalsIgnoreCase("" + HttpURLConnection.HTTP_OK)) {
                versionModel = parseJson(responsemodel, VersionModel.class);
                if (versionModel != null) {
                    newVersion = versionModel.getAndroid().getLatestVersion();
                    force = Boolean.parseBoolean(versionModel.getAndroid().getForceUpdate());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newVersion;
    }


    private VersionModel parseJson(Responsemodel mResponseModel, Class genericClass) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new StringReader(mResponseModel.getResponse()));
            reader.setLenient(true);
            return gson.fromJson(reader, genericClass);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String currentVersion = currentVersion();
        Boolean b = false;
        if ((currentVersion != null && (newVersion.compareTo(currentVersion) > 0)) || newVersion == null) {

            if (force) {
                b = true;
            }else{
                b = false;
            }
            upgrade.isUpgrade(b, newVersion, force);
        }else {
            upgrade.isUpgrade(false,"na",false);
        }
    }

    public interface Upgrade {
        void isUpgrade(boolean b, String newVersion, boolean force);
    }

    public String currentVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            return null;
        } else {
            return pInfo.versionName+"."+pInfo.versionCode;
        }
    }

}