package org.dhbw.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.NotificationFactory;

import java.util.ArrayList;

/**
 * Handles conditions and sets conditions on startup.
 * @author Matthias
 */
public class ConditionService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "checkConditionService";
    public static final String AUTO_START = "Autostart";
    public static final String ADDGEO = "addGeofence";
    public static final String STARTAPP = "startApp";
    public static final String REMOVESTRING = "removeGeofence";
    public static final String CHECKCONDITIONTIME = "checkConditionTime";

    private GoogleApiClient mGoogleApiClient;

    public ConditionService() {
        super(TAG);
    }


    public void onCreate(){
    super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Handles the intent either fired by an alarm triggered or an autostart.
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent fired!");
        // register context
        ContextManager.setContext(this);
        switch (intent.getAction()){
            case AUTO_START:
                handleAutoStart(intent);
                break;
            case STARTAPP:
                startApp(intent);
                break;
            case CHECKCONDITIONTIME:
                handleCheckConditionTime(intent);
                break;
        }
    }

    private void startApp(Intent intent) {

    }

    /**
     * Checks the conditions and fires the actions if all conditions are met after an alarm was triggered.
     * @param id the id of the condition
     */
    private void handleCheckConditionTime(Intent intent){
        // get Id out of intent
        long id = intent.getLongExtra("conditionId", 0);
        // get the corresponding condition
        DBConditionTime condition = DBConditionTime.selectFromDB(id);
        Log.d(TAG, "Condition: " + condition.getName());
        // reset the alarm
        condition.updateAlarm();
        // get the corresponding rules
        ArrayList<DBRule> rules = DBRule.selectFromDB(condition);
        Log.d(TAG, "Number of rules to be checked: " + rules.size());
        // loop through the rules and check whether conditions are met
        for(int i = 0; i < rules.size(); i++){
            if(rules.get(i).allConditionsMet()){ // if all conditions are met
                rules.get(i).startAllActions();
            }
        }
    }

    /**
     * Sets all geofences and alarms after a reboot of the phone.
     * @param intent
     */
    private void handleAutoStart(Intent intent){
        NotificationFactory.createNotification(this, "Autostart", "Willkommen!", false);
        // execute rules for which the conditions are met
        ArrayList<DBRule> rules = DBRule.selectAllFromDB();
        for(int i = 0; i < rules.size(); i++){
            DBRule rule = rules.get(i);
            if(rule.allConditionsMet()){
                rule.startAllActions();
            }
        }
        // register alarms
        for(int i = 0; i < rules.size(); i++){
            rules.get(i).registerAllAlarms();
        }
        // TODO: set geofences for all rules
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("ConditionService","Connection to GoogleAPI successful");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("ConditionService","Connection to GoogleAPI suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("ConditionService","Connection to GoogleAPI failed");
    }
}
