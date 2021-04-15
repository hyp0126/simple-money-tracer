package com.phoenixgroup10.simplemoneytracer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;


import androidx.preference.PreferenceManager;
import com.phoenixgroup10.simplemoneytracer.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.phoenixgroup10.simplemoneytracer.R;

public class NotificationService extends Service {

    private SharedPreferences sharedPref;
    private Date reservationDateTime;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        final Timer timer = new Timer(true);

        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Set Notification DateTime
        SharedPreferences.Editor ed = sharedPref.edit();

        String notiTime = sharedPref.getString("notiTime", "10");
        String[] notiStr = notiTime.split(":");
        notiTime = notiStr[0];
        int notiHour = Integer.parseInt(notiTime);

        String NotiTimeMidday = sharedPref.getString("timeMidday", "2");
        if (NotiTimeMidday.equals("2") || NotiTimeMidday.equals("PM")) {
            notiHour += 12;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, notiHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DATE, 1); // tomorrow
        //calendar.add(Calendar.DATE, -1); // for test

        ed.putLong("notificationDateTime", calendar.getTime().getTime());
        ed.commit();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reservationDateTime = new Date(sharedPref.getLong("notificationDateTime", 0));
                Date currentDateTime = Calendar.getInstance().getTime();
                if (currentDateTime.getTime() > reservationDateTime.getTime() ) {
                    manager.notify(1, notification);

                    Calendar c = Calendar.getInstance();
                    c.setTime(reservationDateTime);
                    c.add(Calendar.DATE, 1);

                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putLong("notificationDateTime", c.getTime().getTime());
                    ed.commit();
                }
                //timer.cancel();
                //stopSelf();
            }
        },1000, 10000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}