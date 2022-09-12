package com.harvard.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Timson on 5/5/2015.
 */
public class SharedPreferenceHelper {

    private static final String PREF_NAME = "AppCredentials";
    private static final String OFFLINE_PREF_NAME = "OfflineAppCredentials";
    private static final int MODE = Context.MODE_PRIVATE;

    /**
     * Read shared preference value
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String readPreference(Context context, String key,
                                        String defValue) {
        return getPreferences(context).getString(key, defValue);
    }
    public static Boolean readLocaleBooleanPreference(Context context, String key,
                                                      Boolean defValue) {
        return getLocalePreferences(context).getBoolean(key, defValue);
    }
    public static String readLocalePreference(Context context, String key,
                                              String defValue) {
        return getLocalePreferences(context).getString(key, defValue);
    }

    /**
     * Write shared preference value
     *
     * @param context
     * @param key
     * @param value
     */
    public static void writePreference(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static void writeLocaleBoolPreference(Context context, String key, Boolean value) {
        getLocaleEditor(context).putBoolean(key, value).apply();
    }
    public static void writeLocalePreference(Context context, String key, String value) {
        getLocaleEditor(context).putString(key, value).apply();
    }

    /**
     * Helper class for getting shared preference instance
     *
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static SharedPreferences getLocalePreferences(Context context) {
        return context.getSharedPreferences(OFFLINE_PREF_NAME, MODE);
    }

    /**
     * Helper class to get shared preference editor instance
     *
     * @param context
     * @return
     */
    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
    private static SharedPreferences.Editor getLocaleEditor(Context context) {
        return getLocalePreferences(context).edit();
    }
}