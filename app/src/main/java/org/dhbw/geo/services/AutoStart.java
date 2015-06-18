package org.dhbw.geo.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import org.dhbw.geo.hardware.NotificationFactory;

/**
 * Created by Matthias on 17.06.2015.
 * TODO: documentation!
 */
public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.e("Autostart", "Autostart called!");
            NotificationFactory.createNotification(context, "Autostart", "Willkommen!", false);
            // TODO: set geofences for all rules
            // TODO: set alarms for all rules
        }
    }
}
