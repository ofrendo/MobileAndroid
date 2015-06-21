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
import com.google.android.gms.maps.model.LatLng;

import org.dhbw.geo.Map.GeofenceTransistionsIntentService;
import org.dhbw.geo.Map.TestLocation;
import org.dhbw.geo.database.DBCondition;
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBFence;
import org.dhbw.geo.database.DBHelper;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.ui.ListView.Notification;

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

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<DBFence> mDBGeofenceList;
    private ArrayList<DBCondition> dbConditionFences;
    private ArrayList mGeofenceList = new ArrayList();
    private LocationRequest mLocationRequest;
    private PendingIntent mPendingIntent;

    //mappingtable
    private HashMap<String , Integer> typeMapping ;

    // just for testing
    private List<TestLocation> testLocations;

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
            default:
                // check if geofenceevent was triggerd
                handleGeofence(intent);
        }
    }

    private void handleGeofence(Intent intent) {
        try {
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

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                NotificationFactory.createNotification(this,"ConditionService", "Triggering Geos" + triggeringGeofences.toString(), false);

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
        }catch (Exception e){
            NotificationFactory.createNotification(this, "Map/Geofence","Error:" + e.getMessage(), false);
        }
    }

    private void handleRemoveGeofence(Intent intent) {
        // TODO: remove Geofence
    }

    private void handleAddGeofence(Intent intent) {
        //TODO: add Geofence
    }

    private void handleStartApp(Intent intent) {
        Bundle bundle = intent.getExtras();
        mPendingIntent = (PendingIntent) bundle.get("pendingIntent");
        // TODO: add all geofences and build a list with them
        // get all active ContionFences from DB
        try {
            dbConditionFences = DBConditionFence.selectAllFromDB();
        }catch (Exception e){
            Log.e("Error", "Can't get ConditionFences from DB");
            e.printStackTrace();
        }
        if (dbConditionFences != null){
            // set Up List with fences
            setUpGeofenceList(dbConditionFences);
            //set geofences active
            startGeofencing();
        }

    }

    private void setUpGeofenceList(ArrayList<DBCondition> dbConditionFences) {
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
    }

    private void setUpTestList() {
    // ONLY FOR TESTING!!!!
        // TODO: Get data from database and build new geofences
        testLocations = new ArrayList<TestLocation>();
            testLocations.add(new TestLocation(new LatLng(49.474275, 8.533699), "Lidl, BW", 10));
            testLocations.add(new TestLocation(new LatLng(49.474292, 8.534501), "DHBW, BW", 30));
            testLocations.add(new TestLocation(new LatLng(49.543011, 8.663158), "HomeSweetHome", 20));
            testLocations.add(new TestLocation(new LatLng(49.430101, 8.529264),"Bei Matthias", 20));
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations.get(0).getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations.get(0).getLocation().latitude, testLocations.get(0).getLocation().longitude, testLocations.get(0).getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations.get(1).getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations.get(1).getLocation().latitude, testLocations.get(1).getLocation().longitude, testLocations.get(1).getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations.get(2).getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations.get(2).getLocation().latitude, testLocations.get(2).getLocation().longitude, testLocations.get(2).getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations.get(3).getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations.get(3).getLocation().latitude, testLocations.get(3).getLocation().longitude, testLocations.get(3).getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
    }

    private String getGeofenceTransitionDetails(ConditionService geofenceTransistionsIntentService, int geofenceTransition, List triggeringGeofences) {
        Geofence geo = (Geofence) triggeringGeofences.get(geofenceTransition);
        return geo.getRequestId();
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

    public Location getLastLocation(){
        // get last location
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void startGeofencing(){
        // start geofencing
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                mPendingIntent
        ).setResultCallback(this);
    }

    private GeofencingRequest getGeofencingRequest(){
        // create GeofenceRequest
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("ConditionService", "Connection to GoogleAPI successful");
        setUpTestList();
        if (mPendingIntent != null){
            startGeofencing();
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
