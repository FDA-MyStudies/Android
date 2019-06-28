package com.harvard.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.harvard.FDAApplication;
import com.harvard.R;

import java.util.Calendar;

/**
 * Created by Naveen Raj on 06/02/2017.
 */

public class ActiveTaskService extends Service {
    int sec;
    Thread t;
    boolean alerted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null)
            if (intent.getStringExtra("broadcast") != null && intent.getStringExtra("broadcast").equalsIgnoreCase("yes")) {
                startAlarm();
            } else {
                try {
                    sec = 0;
                    startAlarm();
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle(getResources().getString(R.string.prject_name))
                            .setTicker(getResources().getString(R.string.fetal_kick_recorder_activity))
                            .setContentText(getResources().getString(R.string.fetal_kick_recorder_activity_in_progress))
                            .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_SERVICE)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(
                                    Bitmap.createScaledBitmap(icon, 128, 128, false))
                            .setOngoing(true).build();

                    startForeground(101, notification);


                    Runnable r = new Runnable() {
                        public void run() {
                            try {
                                while (!t.isInterrupted()) {
                                    sec = sec + 1;
                                    Intent i = new Intent("com.harvard.ActiveTask");
                                    i.putExtra("sec", "" + sec);
                                    sendBroadcast(i);
                                    if (sec % 1800 == 0) {
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ActiveTaskService.this);
                                        builder.setSmallIcon(R.mipmap.ic_launcher)
                                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                                .setContentTitle(getResources().getString(R.string.prject_name))
                                                .setContentText(getString(R.string.activetaskremindertxt))
                                                .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_SERVICE);


                                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        mNotificationManager.notify(sec, builder.build());
                                    }

                                    if (sec >= Integer.parseInt(intent.getStringExtra("remaining_sec"))) {
                                        t.interrupt();
                                        stopSelf();
                                    }
                                    t.sleep(1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    t = new Thread(r);
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            t.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAlarm() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Calendar localCalendar = Calendar.getInstance();
                Intent intent1 = new Intent(this, AlarmService.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);//21600000
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(localCalendar.getTimeInMillis() + 180000, alarmIntent), alarmIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}