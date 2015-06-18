package org.dhbw.geo.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import org.dhbw.geo.hardware.NotificationFactory;

/**
 * The autostart creates the system triggers after startup (geofences and alarms).
 * @author Matthias
 */
public class AutoStart extends BroadcastReceiver {

    public static final String AUTO_START = "Autostart";

    /**
     * Starts the conditionService to set alarms and triggers
     * It is called when the phone has successfully booted.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.d("Autostart", "Autostart called!");
            Intent service = new Intent(context, ConditionService.class);
            // pass the action string
            service.setAction(AUTO_START);
            // Start the service, keeping the device awake while it is launching.
            context.startService(service);
        }
    }
}
