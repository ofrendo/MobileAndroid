package org.dhbw.geo.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.dhbw.geo.Map.TestLocation;
import org.dhbw.geo.database.DBCondition;
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBFence;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.NotificationFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles conditions and sets conditions on startup.
 * @author Matthias
 */
public class ConditionService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    public static final String TAG = "CheckConditionService";
    public static final String AUTO_START = "Autostart";
    public static final String ADDGEO = "AddGeofence";
    public static final String STARTAPP = "StartApp";
    public static final String REMOVEGEO = "RemoveGeofence";
    public static final String CHECKCONDITIONTIME = "CheckConditionTime";
    public static final String UPDATEGEO ="UpdateGeofence";

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<DBFence> mDBGeofenceList;
    private ArrayList<DBCondition> dbConditionFences;
    private ArrayList mGeofenceList = new ArrayList();
    private LocationRequest mLocationRequest;
    private PendingIntent mPendingIntent;
    private Boolean addSingleFence = false;
    private Boolean updateFence = false;
    private DBFence fenceToRemove = null;

    //mappingtable
    private HashMap<String , Integer> typeMapping ;

    // just for testing
    private List<TestLocation> testLocations;

    public ConditionService() {
        super(TAG);
    }


    public void onCreate(){
    super.onCreate();
        // get GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        setUpTypeMapping();
    }

    private void setUpTypeMapping() {
        typeMapping = new HashMap<String, Integer>();
        typeMapping.put(DBConditionFence.TYPE_ENTER, Geofence.GEOFENCE_TRANSITION_ENTER);
        typeMapping.put(DBConditionFence.TYPE_LEAVE, Geofence.GEOFENCE_TRANSITION_EXIT);
        typeMapping.put(DBConditionFence.TYPE_STAY, Geofence.GEOFENCE_TRANSITION_DWELL);
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
                handleStartApp(intent);
                break;
            case CHECKCONDITIONTIME:
                handleCheckConditionTime(intent);
                break;
            case ADDGEO:
                handleAddGeofence(intent);
                break;
            case REMOVEGEO:
                handleRemoveGeofence(intent);
                break;
            case UPDATEGEO:
                handleUpdateGeofence(intent);
            default:
                // check if geofenceevent was triggerd
                handleGeofence(intent);
        }
    }

    private void handleUpdateGeofence(Intent intent) {
        updateFence = true;
        Bundle bundle = intent.getExtras();
        long fenceId = bundle.getLong("DBFenceID", -1);
        if (fenceId != -1){
            DBFence fence = DBFence.selectFromDB(fenceId);
            removeGeofence(fence);
            addGeofence(fence);
        }
    }

    private void addGeofence(DBFence fence) {
        // TODO: add single geofence
        Log.d("Geofence Single", "Add single Geofence");
    }

    private void handleGeofence(Intent intent) {
        try {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.d("Maps/Geofencing", String.valueOf(geofencingEvent.getErrorCode()));
                return;
            }


            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            if (geofenceTransition != -1){
                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Object object : triggeringGeofences){
                    Geofence geofence = (Geofence) object;
                    DBFence fence = DBFence.selectFromDB(Long.parseLong(geofence.getRequestId()));
                    DBConditionFence conditionFence = fence.getConditionFence();
                    performeAction(conditionFence);
                }

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(
                        this,
                        geofenceTransition,
                        triggeringGeofences
                );

                // Send notification and log the transition details.
                Log.d("ConditionService/Geo", geofenceTransitionDetails);
            }

        }catch (Exception e){
            NotificationFactory.createNotification(this, "Map/Geofence","Error:" + e.getMessage(), false);
        }
    }

    private void handleRemoveGeofence(Intent intent) {
        Bundle bundle = intent.getExtras();
        //remove fence from db
        long fenceId = bundle.getLong("DBFenceID", -1);
        if (fenceId != -1) {
            DBFence fence = DBFence.selectFromDB(fenceId);
            long conditionFenceId = bundle.getLong("DBConditionFenceID", -1);
            if (conditionFenceId != -1) {
                DBConditionFence conditionFence = DBConditionFence.selectFromDB(conditionFenceId);
                conditionFence.removeFence(fence);
                conditionFence.writeToDB();
                removeGeofence(fence);
            }
        }
    }

    private void removeGeofence(DBFence fence) {
        // remove Geofence from GoogleApiScoupe
        List<String> fences = new ArrayList<String>();
        fences.add(String.valueOf(fence.getId()));
        try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, fences);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", "Romoving Geofence: Google API is not connected");
        }
        if (!updateFence){
            // delete Geofence from DB
            fence.deleteFromDB();
        }
    }

    private void handleAddGeofence(Intent intent) {
        Bundle bundle = intent.getExtras();
        long fenceId = bundle.getLong("DBFenceID", -1);
        if (fenceId != -1){
            long conditionFenceId = bundle.getLong("DBConditionFenceID", -1);
            if (conditionFenceId != -1){
                mPendingIntent = (PendingIntent) bundle.get("PendingIntent");
                DBFence fence = DBFence.selectFromDB(fenceId);
                DBConditionFence conditionFence = DBConditionFence.selectFromDB(conditionFenceId);
                //TODO: add Geofence
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(fence.getId()))
                        .setTransitionTypes(typeMapping.get(conditionFence.getType()))
                        .setCircularRegion(fence.getLatitude(), fence.getLongitude(), fence.getRadius())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(500)
                        .build());
            }
        }
        addSingleFence = true;
        if (mPendingIntent == null){
            Log.e("ERROR", "PendingIntent must be specified!");
        }
        Log.d("ConditionService/add", "AddGeofence succ");
    }

    private void handleStartApp(Intent intent) {
        // set PendingIntent
        Bundle bundle = intent.getExtras();
        mPendingIntent = (PendingIntent) bundle.get("PendingIntent");
    }

    private void setUpGeofenceList() {
        // get all active ContionFences from DB
        try {
            dbConditionFences = DBConditionFence.selectAllFromDB();
        }catch (Exception e){
            Log.e("Error", "Can't get ConditionFences from DB");
            e.printStackTrace();
        }
        for (int i = 0; i< dbConditionFences.size() ; i++){
            DBConditionFence cf = (DBConditionFence) dbConditionFences.get(i);
            cf.loadAllFences();
            ArrayList<DBFence> fences = cf.getFences();
            for (int j = 0; j<fences.size() ; j++){
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(fences.get(j).getId()))
                        .setTransitionTypes(typeMapping.get(cf.getType()))
                        .setCircularRegion(fences.get(j).getLatitude(), fences.get(j).getLongitude(), fences.get(j).getRadius())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(500)
                        .build());
            }
        }
        Log.d("ConditionService", "Setup List successful");
    }


    private String getGeofenceTransitionDetails(ConditionService geofenceTransistionsIntentService, int geofenceTransition, List triggeringGeofences) {
        Geofence geo = (Geofence) triggeringGeofences.get(geofenceTransition);
        return geo.getRequestId();
    }

    /**
     * Checks the conditions and fires the actions if all conditions are met after an alarm was triggered.
     */
    private void handleCheckConditionTime(Intent intent){
        // get Id out of intent
        long id = intent.getLongExtra("conditionId", 0);
        // get the corresponding condition
        DBConditionTime condition = DBConditionTime.selectFromDB(id);
        if(condition == null) return;
        Log.d(TAG, "Condition: " + condition.getName());
        // reset the alarm
        condition.updateAlarm();
        performeAction(condition);
    }

    private void performeAction(DBCondition condition) {
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
     * registers all alarms after a reboot of the phone.
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
    }

    public Location getLastLocation(){
        // get last location
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void registerGeofences(){
        // start geofencing
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    mPendingIntent
            ).setResultCallback(this);
            addSingleFence = false;
        }catch (Exception e){
            Log.e("ERROR", "Register Geofences failed");
            e.printStackTrace();
        }
        Log.d("ConditionService/Reg", "Geofence Registration succ");

    }

    private GeofencingRequest getGeofencingRequest(){
        // create GeofenceRequest
        try {
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(mGeofenceList);
            return builder.build();
        }catch (Exception e){
            Log.e("ERROR", "No geofences has been added to this request");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("ConditionService", "Connection to GoogleAPI successful");
        if (mPendingIntent != null){
            if (!addSingleFence){
                setUpGeofenceList();
            }
            registerGeofences();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("ConditionService","Connection to GoogleAPI suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("ConditionService","Connection to GoogleAPI failed");
    }

    @Override
    public void onResult(Status status) {
        Log.d("ConditionService","Add Geofence: " + status.toString());
    }
}
