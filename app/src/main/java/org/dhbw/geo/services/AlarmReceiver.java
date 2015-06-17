package org.dhbw.geo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.dhbw.geo.database.DBConditionTime;

import java.util.Calendar;

/**
 * Created by Matthias on 17.06.2015.
 * TODO: documentation!
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive fired!");
        // if an alarm is triggered
        // create a new intent
        Intent service = new Intent(context, CheckConditionService.class);
        // pass the action string
        service.setAction(intent.getAction());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, DBConditionTime conditionTime){
        // if there has already been an alarm, cancel it
        cancelAlarm();
        // get the android Alarmmanager
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // create a new intent for this
        Intent intent = new Intent(context, AlarmReceiver.class);
        // set the intent action to the conditionID to know which rule's timecondition was triggered
        intent.setAction(String.valueOf(conditionTime.getId()));
        // set a broadcast id to allow multiple alarms :-)
        alarmIntent = PendingIntent.getBroadcast(context, (int) conditionTime.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
        // set the android alarm (RTC = real time clock)
        alarmMgr.set(AlarmManager.RTC_WAKEUP, conditionTime.getStart().getTimeInMillis(), alarmIntent);
        Log.d("AlarmReceiver", "setAlarm with action: " + intent.getAction() + ": " + conditionTime.getStart().toString());
    }

    public void cancelAlarm(){
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
