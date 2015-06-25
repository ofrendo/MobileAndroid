package org.dhbw.geo.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dhbw.geo.R;
import org.dhbw.geo.backend.BackendCallback;
import org.dhbw.geo.backend.BackendController;
import org.dhbw.geo.backend.JSONConverter;
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.dhbw.geo.database.DBHelper;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.services.ConditionService;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //Geofencing
    private PendingIntent mGeofencePendingIntent;
    private ArrayList mGeofenceList = new ArrayList();
    private ArrayList<DBFence> mDBFenceList = new ArrayList<DBFence>();
    private List<TestLocation> testLocations;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private HashMap<String, Circle> markerCircelMapping;
    private HashMap<String, DBFence> markerLocationMapping;
    private Marker activeMarker;
    // Marker Options --> Seekbar, change name
    private SeekBar radius;
    private TextView radiusText;
    private TextView radiusTextUnit;
    private TextView radiusTextDescription;
    private TextView mapMarkerName;
    private TextView mapMarkerEditName;
    private ImageButton deleteMarkerButton;
    private Button uploadServer;

    long ruleID;
    DBConditionFence fenceGroup;

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle Back navigation
     */
    @Override
    public void onBackPressed() {
        Intent parent = getParentActivityIntent();
        //pls enter ruleID
        parent.putExtra("RuleID", ruleID);
        parent.putExtra("ScreenID", 1);

        startActivity(parent);
    }

    /**
     * onCreate - starting the activity
     * first of all get reference to the DBRule and save the DBConditionFence object
     * initialise map
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get ID from intent
        Intent i = getIntent();

        //get RuleID and ConditionFenceID
        ruleID = i.getLongExtra("DBRuleID",-1);
        long fenceGroupID = i.getLongExtra("DBConditionFenceID",-1);

        if (fenceGroupID != -1 ){
            //load DBconditionfence
            fenceGroup = DBConditionFence.selectFromDB(fenceGroupID);
        }else {
            if (ruleID != -1){

                //create new DBConditionFence
                fenceGroup = new DBConditionFence();
                fenceGroup.setRule(DBRule.selectFromDB(ruleID));
                fenceGroup.setName(i.getStringExtra("DBConditionFenceName"));
                fenceGroup.writeToDB();

                //set Extra for optional resizing event
                i.putExtra("DBConditionFenceID", fenceGroup.getId());
            }
        }
        Log.i("FENCE", "ID: " + fenceGroup.getId());
        Log.i("FENCE", "NAME: " + fenceGroup.getName());

        setContentView(R.layout.activity_maps);
        // start setup mapping variables
        markerCircelMapping = new HashMap<String, Circle>();
        markerLocationMapping = new HashMap<String, DBFence>();
        testLocations = new ArrayList<TestLocation>();
        // start setup map
        getLocations();
        getUIObjects();
        setUpSeekerBar();
        getUpMap();
        addInitialMarkersToMap();
        setMarkerChangeVisibility(false);
    }

    /**
     * resume map
     */
    @Override
    protected void onResume() {
        super.onResume();
        getUpMap();
    }

    /**
     * Load all locations from DB
     */
    private void getLocations() {
        // load locations from DB
        fenceGroup.loadAllFences();
        mDBFenceList = fenceGroup.getFences();
    }

    /**
     * get all UI elements
     */
    private void getUIObjects() {
        radius = (SeekBar) findViewById(R.id.map_radius_seekbar);
        radiusText = (TextView) findViewById(R.id.map_radius);
        radiusTextDescription = (TextView) findViewById(R.id.map_radius_description);
        radiusTextUnit = (TextView) findViewById(R.id.map_radius_unit);
        mapMarkerName = (TextView) findViewById(R.id.map_marker_name);
        mapMarkerEditName = (TextView) findViewById(R.id.map_marker_edit_name);
        deleteMarkerButton = (ImageButton) findViewById(R.id.deleteMarkerButton);
        uploadServer = (Button) findViewById(R.id.map_upload_button);
    }

    /**
     * Create a new ConditionGroup on serverside an send all fences
     */
    private void initalUploadFencesToServer() {
        Log.d("Maps/Upload", "Upload FenceGroupe to Server");
        BackendController backendController1 = new BackendController(new BackendCallback() {
            public void actionPerformed(String result) {
                Log.i("BackendCallback", "createFenceGroup: " + result);
                DBConditionFence resultFenceGroup = JSONConverter.getFenceGroup(result);
                fenceGroup.setServerId(resultFenceGroup.getServerId());
                updateConditionFencesOnDB();
                DBHelper.getInstance().logTable(DBHelper.TABLE_CONDITION_FENCE);
                sendAllFencesToServer();
            }
        });
        backendController1.createFenceGroup(fenceGroup);
    }

    /**
     * send all fences to server
     */
    private void sendAllFencesToServer() {
        for (final DBFence fence : mDBFenceList){
            BackendController backendController = new BackendController(new BackendCallback() {
                public void actionPerformed(String result) {
                    Log.i("BackendCallback", "createFence: " + result);
                }
            });
            backendController.createFence(fenceGroup.getServerId(), fence);
        }
    }

    /**
     * Get all fences from server and replace them with the current status
     */
    private void deltaSyncWithServer() {
        BackendController backendController = new BackendController(new BackendCallback() {
            public void actionPerformed(String result) {
                Log.i("BackendCallback", "GetAllFences: " + result);
                ArrayList<DBFence> serverFences = JSONConverter.getFences(result);
                //delete old fences
                deleteAllFencesServer(serverFences);
                // send new fences
                sendAllFencesToServer();
            }
        });
        backendController.getFencesForGroup(fenceGroup.getServerId());
    }

    /**
     * Delete all given fences from server
     * @param serverFences
     */
    private void deleteAllFencesServer(ArrayList<DBFence> serverFences) {
        for (DBFence fence : serverFences){
            Log.d("Maps/ServerCall", "Delete Fence: " + fence.toString());
            BackendController backendController2 = new BackendController(new BackendCallback() {
                public void actionPerformed(String result) {
                    Log.i("BackendCallback", "deleteFence: " + result);
                }
            });
            backendController2.deleteFence(fenceGroup.getServerId(), (int)fence.getId());
        }
    }

    /**
     * Write current fenceGroup (DBConditionFence) to DB
     */
    private void updateConditionFencesOnDB() {
        fenceGroup.writeToDB();
    }

    /**
     * Set Up the seekbar
     * add onProgressChanged Listener to seekbar and handle the changes
     */
    private void setUpSeekerBar() {
        setTextViewSeekbarText(50);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextViewSeekbarText(progress);
                try {
                    if (progress < 1) {
                        progress = 1;
                        radius.setProgress(progress);
                    }
                    // change radius
                    Circle circle = markerCircelMapping.get(activeMarker.getId());
                    circle.setRadius(progress);
                    //update database
                    markerLocationMapping.get(activeMarker.getId()).setRadius(progress);
                } catch (Exception e) {
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateFenceInDB(activeMarker);
            }
        });
    }

    /**
     * Set TextView with current seekbar-value
     * @param progress
     */
    private void setTextViewSeekbarText(int progress) {
        Log.d("Maps/Seekbar", String.valueOf(progress));
        radiusText.setText(String.valueOf(progress));
        radius.setProgress(progress);
    }

    /**
     * Set current Marker name
     * @param name
     */
    private void setMarkerNameTextView(String name) {
        mapMarkerEditName.setText(name);
    }

    /**
     * get Last Location
     * @return
     */
    public Location getCurrentLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * get mMap object
     */
    private void getUpMap() {
        // Do a null check to confirm that we have not already instantiated the Map.
        if (mMap == null) {
            // Try to obtain the Map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the Map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * add Listener to the Map-Object
     */
    private void setUpMap() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraChangeListener(this);
    }

    /**
     * Add all markers from DB to the map
     */
    private void addInitialMarkersToMap() {
        for (int i = 0 ; i < mDBFenceList.size(); i++){
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(mDBFenceList.get(i).getLatLng())
                    .title(fenceGroup.getName())
                    .draggable(true));
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(mDBFenceList.get(i).getLatLng())
                    .radius(mDBFenceList.get(i).getRadius())
                    .strokeColor(Color.RED));
            String circleId = circle.getId();
            String markerId = m.getId();
            Log.d("Maps/BuildMarker","CircelId: " + circleId + " MarkerId: " + markerId);
            markerCircelMapping.put(m.getId(), circle);
            markerLocationMapping.put(m.getId(), mDBFenceList.get(i));
        }
    }

    /**
     * Add new marker to map
     * @param loc
     * @param name
     * @return
     */
    private Marker addMarkerToMap(LatLng loc, String name){
        int initialRadius = 50;
        Marker m = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title(name)
                .draggable(true));
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(loc)
                .radius(initialRadius)
                .strokeColor(Color.RED));
        String circleId = circle.getId();
        String markerId = m.getId();
        Log.e("Maps/BuildMarker", "CircelId: " + circleId + " MarkerId: " + markerId);
        DBFence fence = createDBFence(loc, initialRadius);

        mDBFenceList.add(fence);
        markerCircelMapping.put(m.getId(), circle);
        markerLocationMapping.put(m.getId(), mDBFenceList.get(mDBFenceList.size() - 1));
        //save new marker to DB
        writeNewMarkerToDB(mDBFenceList.get(mDBFenceList.size() - 1));
        return m;
    }

    /**
     * Create new DBFence object with params loc Location /initialRadius int
     * @param loc
     * @param initialRadius
     * @return
     */
    private DBFence createDBFence(LatLng loc, int initialRadius) {
        DBFence fence = new DBFence();
        fence.setLongitude(loc.longitude);
        fence.setLatitude(loc.latitude);
        fence.setRadius(initialRadius);
        fence.setConditionFence(fenceGroup);
        return fence;
    }

    /**
     * Save new marker and write it to DB
     * @param dbFence
     */
    private void writeNewMarkerToDB(DBFence dbFence) {
        //write new Geofence to DB
        dbFence.writeToDB();
        addSingleGeofence(dbFence);
    }

    /**
     * add a singel geofence
     * @param dbFence
     */
    private void addSingleGeofence(DBFence dbFence) {
        Intent addFence = new Intent(this, ConditionService.class);
        addFence.setAction(ConditionService.ADDGEO);
        addFence.putExtra("PendingIntent", MainActivity.gPendingIntent);
        addFence.putExtra("DBFenceID", dbFence.getId());
        addFence.putExtra("DBConditionFenceID", fenceGroup.getId());
        startService(addFence);
    }

    /**
     * Delete a single marker
     * @param marker
     */
    private void deleteMarker(Marker marker){
        DBFence fence = markerLocationMapping.get(marker.getId());
        Circle circle = markerCircelMapping.get(marker.getId());
        testLocations.remove(fence);
        markerLocationMapping.remove(marker.getId());
        markerCircelMapping.remove(marker.getId());
        circle.remove();
        marker.remove();
        // remove geofence
        deleteGeofence(fence);
        // set activeMarker inaktive
        activeMarker = null;
        // hide Options
        setMarkerChangeVisibility(false);
    }

    /**
     * Delete geofence
     * @param fence
     */
    private void deleteGeofence(DBFence fence) {
        Intent removeFence = new Intent(this, ConditionService.class);
        removeFence.setAction(ConditionService.REMOVEGEO);
        removeFence.putExtra("PendingIntent", MainActivity.gPendingIntent);
        removeFence.putExtra("DBFenceID", fence.getId());
        removeFence.putExtra("DBConditionFenceID", fenceGroup.getId());
        startService(removeFence);
    }

    /**
     * Change visibility
     * if a marker is selected show ui elements
     * @param visibility
     */
    private void setMarkerChangeVisibility(Boolean visibility){
        if (visibility){
            radius.setVisibility(View.VISIBLE);
            radiusTextUnit.setVisibility(View.VISIBLE);
            radiusText.setVisibility(View.VISIBLE);
            radiusTextDescription.setVisibility(View.VISIBLE);
            mapMarkerName.setVisibility(View.VISIBLE);
            mapMarkerEditName.setVisibility(View.VISIBLE);
            deleteMarkerButton.setVisibility(View.VISIBLE);

        }else{
            radius.setVisibility(View.INVISIBLE);
            radiusTextUnit.setVisibility(View.INVISIBLE);
            radiusText.setVisibility(View.INVISIBLE);
            radiusTextDescription.setVisibility(View.INVISIBLE);
            mapMarkerName.setVisibility(View.INVISIBLE);
            mapMarkerEditName.setVisibility(View.INVISIBLE);
            deleteMarkerButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * update selected fence, if the location or radius change
     * @param fence
     * @param marker
     */
    private void updateGeofences(DBFence fence, Marker marker) {
        //update fence in db
        updateFenceInDB(marker);
        Intent updateFenceIntent = new Intent(this, ConditionService.class);
        updateFenceIntent.setAction(ConditionService.UPDATEGEO);
        updateFenceIntent.putExtra("PendingIntent", MainActivity.gPendingIntent);
        updateFenceIntent.putExtra("DBFenceID", fence.getId());
        updateFenceIntent.putExtra("DBConditionFenceID", fenceGroup.getId());
        startService(updateFenceIntent);
    }

    /**
     * Update selected fence in DB
     * @param marker
     */
    private void updateFenceInDB(Marker marker) {
        Log.d("Maps/UpdateDB", "Update Fence in DB");
        DBFence fence = getFence(marker);
        fence.setLatitude(marker.getPosition().latitude);
        fence.setLongitude(marker.getPosition().longitude);
        // get radius
        Circle circle = markerCircelMapping.get(marker.getId());
        fence.setRadius((int) circle.getRadius());
        fence.writeToDB();
    }

    /**
     * Returns DBFence object to given marker
     * @param marker
     * @return
     */
    private DBFence getFence(Marker marker) {
        return markerLocationMapping.get(marker.getId());
    }

    /**
     * Set camera fokus to all markers
     */
    private void setCameraFocus() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mDBFenceList.size() >= 1){
            for (DBFence fence : mDBFenceList){
                builder.include(fence.getLatLng());
            }
            LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cameraUpdate);
        }
    }

    /**
     * Delete marker from map/DB/geofencelist
     * @param view
     */
    public void onClickDeleteButton(View view){
        deleteMarker(activeMarker);
        activeMarker = null;
    }

    /**
     * Handle event onMarkerClick event
     * set active Marker
     * change seekbar progress and set visibility of ui elements true
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // set Name/Radius visible
        setMarkerChangeVisibility(true);
        // Save Marker to change radius
        activeMarker = marker;
        // set current data
        setTextViewSeekbarText((int) markerCircelMapping.get(marker.getId()).getRadius());
        setMarkerNameTextView(fenceGroup.getName());
        return false;
    }

    /**
     * Handle event onMapLongClick
     * create new Marker
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        //create new marker
        // get string form DBConditionFence
        activeMarker = addMarkerToMap(latLng, getString(R.string.newMarker));
        //set Edit functions true
        setMarkerChangeVisibility(true);

    }

    /**
     * Handdle event onMarkerDragStart
     * get dragged marker and change circlecenter
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        circle.setCenter(latLong);
    }

    /**
     * Handle event onMarkerDrag
     * get dragged marker and change circlecenter
     * @param marker
     */
    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        circle.setCenter(latLong);
    }

    /**
     * Handle event onMarkerDragEnd
     * get dragged marker and change circlecenter
     * save new position --> on DB and update geofence
     * @param marker
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        DBFence fence = markerLocationMapping.get(marker.getId());
        circle.setCenter(latLong);
        updateGeofences(fence, marker);
    }

    /**
     * Handle event onMapClick
     * set activeMarker = null and visibility of ui elements false
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // unselect active Marker
        activeMarker = null;
        // search possibility to hide progressbar
        setMarkerChangeVisibility(false);
    }

    /**
     * Handle event onCameraChange
     * if map is initiated change cameraposition to all markers and delete onCameraChangeListener
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // move camera
        setCameraFocus();
        // remove listener
        mMap.setOnCameraChangeListener(null);
    }

    /**
     * Handle event onClickUploadToServer
     * trigger upload of current fences to server
     * check if there is already a version
     * yes : update server
     * no : create new
     * @param view
     */
    public void onClickUploadToServer(View view) {
        //check if serverID is -1
        if (fenceGroup.getServerId() == -1){
            Log.d("Maps/Upload", "Initial upload");
            initalUploadFencesToServer();
        }else{
            Log.d("Maps/Upload", "Delta Sync");
            //delete old and set new version
            deltaSyncWithServer();
        }
        DBHelper.getInstance().logTable(DBHelper.TABLE_CONDITION_FENCE);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
