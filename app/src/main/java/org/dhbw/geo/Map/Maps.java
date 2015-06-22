package org.dhbw.geo.Map;

import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.services.ConditionService;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {

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
    private LinearLayout mapLayout;

    long ruleID;
    DBConditionFence fenceGroup;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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


        @Override
    public void onBackPressed() {
        Intent parent = getParentActivityIntent();
        //pls enter ruleID
        parent.putExtra("RuleID",ruleID);
        parent.putExtra("ScreenID",1);

        startActivity(parent);
    }

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
            }
        }
        Log.i("FENCE", "ID: " + fenceGroup.getId());
        Log.i("FENCE", "NAME: " + fenceGroup.getName());


        setContentView(R.layout.activity_maps);
        markerCircelMapping = new HashMap<String, Circle>();
        markerLocationMapping = new HashMap<String, DBFence>();
        testLocations = new ArrayList<TestLocation>();
        getLocations();
        getUIObjects();
        setUpSeekerBar();
        getUpMap();
        addInitialMarkersToMap();
        setMarkerChangeVisibility(false);
    }


    private void getLocations() {
        // load locations from DB
        fenceGroup.loadAllFences();
        mDBFenceList = fenceGroup.getFences();
    }

    private void getUIObjects() {
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        radius = (SeekBar) findViewById(R.id.map_radius_seekbar);
        radiusText = (TextView) findViewById(R.id.map_radius);
        radiusTextDescription = (TextView) findViewById(R.id.map_radius_description);
        radiusTextUnit = (TextView) findViewById(R.id.map_radius_unit);
        mapMarkerName = (TextView) findViewById(R.id.map_marker_name);
        mapMarkerEditName = (TextView) findViewById(R.id.map_marker_edit_name);
        deleteMarkerButton = (ImageButton) findViewById(R.id.deleteMarkerButton);
    }

    private void setUpSeekerBar() {
        setTextViewSeekbarText(50);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextViewSeekbarText(progress);
                // change radius
                Circle circle = markerCircelMapping.get(activeMarker.getId());
                circle.setRadius(progress);
                //update database
                markerLocationMapping.get(activeMarker.getId()).setRadius(progress);
                // TODO : Update new Radius to DB
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setTextViewSeekbarText(int progress) {
        Log.d("Maps/Seekbar", String.valueOf(progress));
        radiusText.setText(String.valueOf(progress));
        radius.setProgress(progress);
    }

    private void setMarkerNameTextView(String name) {
        mapMarkerEditName.setText(name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUpMap();
    }

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

    private void setUpMap() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraChangeListener(this);
    }

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

    private DBFence createDBFence(LatLng loc, int initialRadius) {
        DBFence fence = new DBFence();
        fence.setLongitude(loc.longitude);
        fence.setLatitude(loc.latitude);
        fence.setRadius(initialRadius);
        fence.setConditionFence(fenceGroup);
        return fence;
    }

    private void writeNewMarkerToDB(DBFence dbFence) {
        //write new Geofence to DB
        dbFence.writeToDB();
        addSingleGeofence(dbFence);
    }

    private void addSingleGeofence(DBFence dbFence) {
        Intent addFence = new Intent(this, ConditionService.class);
        addFence.setAction(ConditionService.ADDGEO);
        addFence.putExtra("PendingIntent", MainActivity.gPendingIntent);
        addFence.putExtra("DBFenceID", dbFence.getId());
        addFence.putExtra("DBConditionFenceID", fenceGroup.getId());
        startService(addFence);
    }

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

    private void deleteGeofence(DBFence fence) {
        Intent removeFence = new Intent(this, ConditionService.class);
        removeFence.setAction(ConditionService.REMOVEGEO);
        removeFence.putExtra("PendingIntent", MainActivity.gPendingIntent);
        removeFence.putExtra("DBFenceID", fence.getId());
        removeFence.putExtra("DBConditionFenceID", fenceGroup.getId());
        startService(removeFence);
    }

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

    private void updateGeofences() {
        Intent updateFenceIntent = new Intent(this, ConditionService.class);
        updateFenceIntent.setAction(ConditionService.ADDGEO);
        updateFenceIntent.putExtra("PendingIntent", MainActivity.gPendingIntent);
        updateFenceIntent.putExtra("DBFenceID", 1);
        updateFenceIntent.putExtra("DBConditionFenceID", 1);
        startService(updateFenceIntent);
    }

    private void moveMapUp(LinearLayout layout){
        Animation animation = new TranslateAnimation(0,0,0,-100);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        layout.startAnimation(animation);
    }

    private void moveMapDown(LinearLayout layout){
        Animation animation = new TranslateAnimation(0,0,0,0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        layout.startAnimation(animation);
    }

    private void moveKeyPadUpdated(LinearLayout keyPad){
        ObjectAnimator mover = ObjectAnimator.ofFloat(keyPad,"translationY",0,-500);
        mover.setDuration(300);
        mover.start();
    }

    private Boolean createAlertDialog(String title, String question, String yes, String no) {
        //AlertDialog.Builder builder = new AlertDialog(getAc);
        return false;
    }

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

    public Location getCurrentLocation(){
        // check mGoogleApiClient
       Location location = LocationServices.FusedLocationApi.getLastLocation(
               mGoogleApiClient);
        return location;
    }

    public void onClickDeleteButton(View view){
        // show dialog --> are you shure to delete this geofence?
        Boolean delete = createAlertDialog(getString(R.string.MapsTitleDeleteGeofence), getString(R.string.MapsQuestionDeleteGeofence), getString(R.string.yes), getString(R.string.no));
        deleteMarker(activeMarker);
        activeMarker = null;
    }


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


    @Override
    public void onMapLongClick(LatLng latLng) {
        //create new marker
        // get string form DBConditionFence
        activeMarker = addMarkerToMap(latLng, getString(R.string.newMarker));
        //set Edit functions true
        setMarkerChangeVisibility(true);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        circle.setCenter(latLong);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        circle.setCenter(latLong);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLong = marker.getPosition();
        Circle circle = markerCircelMapping.get(marker.getId());
        circle.setCenter(latLong);
        updateGeofences();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // unselect active Marker
        activeMarker = null;
        // search possibility to hide progressbar
        setMarkerChangeVisibility(false);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // move camera
        setCameraFocus();
        // remove listener
        mMap.setOnCameraChangeListener(null);
    }
}
