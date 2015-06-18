package org.dhbw.geo.map;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.dhbw.geo.hardware.NotificationFactory;

import java.util.List;

/**
 * Created by Max on 15.06.2015.
 */
public class GeofenceTransistionsIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GeofenceTransistionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d("Maps/Geofencing", String.valueOf(geofencingEvent.getErrorCode()));
            return;
        }

        NotificationFactory.createNotification(this, "GeofenceIntentService", "onHandle", false);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            NotificationFactory.createNotification(this, "Test Map", "IntentWorks", false);

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            NotificationFactory.createNotification(this, "Test Map", geofenceTransitionDetails, false);
            Log.d("Map/Geofancing", geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e("Map/Geofancing - Error", "Fehler");
        }
    }

    private String getGeofenceTransitionDetails(GeofenceTransistionsIntentService geofenceTransistionsIntentService, int geofenceTransition, List triggeringGeofences) {
        Geofence geo = (Geofence) triggeringGeofences.get(geofenceTransition);
        return geo.getRequestId();
    }
}
