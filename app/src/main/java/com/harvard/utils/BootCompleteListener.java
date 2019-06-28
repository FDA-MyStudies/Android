package com.harvard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rohit on 7/25/2017.
 */

public class BootCompleteListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
//        settings.edit().clear().apply();
//// delete passcode from keystore
//        String pass = AppController.refreshKeys("passcode");
//        if (pass != null)
//            AppController.deleteKey("passcode_" + pass);
//        DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
//        Realm realm = AppController.getRealmobj();
//        dbServiceSubscriber.deleteDb();
//        try {
//            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, realm);
//            notificationModuleSubscriber.cancleActivityLocalNotification(context);
//            notificationModuleSubscriber.cancleResourcesLocalNotification(context);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dbServiceSubscriber.closeRealmObj(realm);
//
//        // clear notifications from notification tray
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.cancelAll();
    }
}
