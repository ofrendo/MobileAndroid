package org.dhbw.geo.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dhbw.geo.R;
import org.dhbw.geo.hardware.NotificationFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Maps extends FragmentActivity implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //Geofencing
    private PendingIntent mGeofencePendingIntent;
    private ArrayList mGeofenceList = new ArrayList();
    private static final TestLocation[] testLocations = new TestLocation[] {
            new TestLocation(new LatLng(49.474275, 8.533699), "Lidl, BW", 10),
            new TestLocation( new LatLng(49.474292, 8.534501), "DHBW, BW", 30),
            new TestLocation( new LatLng(49.543011, 8.663211), "HomeSweetHome", 100)
    };
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private HashMap<String, Circle> markerCircelMapping;
    private HashMap<String, TestLocation> markerLocationMapping;
    private Marker activeMarker;
    // Marker Options --> Seekbar, change name
    private SeekBar radius;
    private TextView radiusText;
    private TextView radiusTextUnit;
    private TextView radiusTextDescription;
    private TextView mapMarkerName;
    private EditText mapMarkerEditName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        markerCircelMapping = new HashMap<String, Circle>();
        markerLocationMapping = new HashMap<String, TestLocation>();
        getUIObjects();
        getUpMap();
        setUpSeekerBar();
    }

    private void getUIObjects() {
        radius = (SeekBar) findViewById(R.id.map_radius_seekbar);
        radiusText = (TextView) findViewById(R.id.map_radius);
        radiusTextDescription = (TextView) findViewById(R.id.map_radius_description);
        radiusTextUnit = (TextView) findViewById(R.id.map_radius_unit);
        mapMarkerName = (TextView) findViewById(R.id.map_marker_name);
        mapMarkerEditName = (EditText) findViewById(R.id.map_marker_edit_name);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setUpSeekerBar() {
        radius.setProgress(testLocations[0].getRadius());
        setTextViewSeekbarText(testLocations[0].getRadius());
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextViewSeekbarText(progress);
                // change radius
                Circle circle = markerCircelMapping.get(activeMarker.getId());
                circle.setRadius(progress);
                //update database
                markerLocationMapping.get(activeMarker.getId()).setRadius(progress);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUpMap();
    }

    private void getUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
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
    }

    private void addMarkerToMap() {
        for (int i = 0 ; i < mGeofenceList.size(); i++){
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(testLocations[i].getLocation())
                    .title(testLocations[i].getName())
                    .draggable(true));
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(testLocations[i].getLocation())
                    .radius(testLocations[i].getRadius())
                    .strokeColor(Color.RED));
            String circleId = circle.getId();
            String markerId = m.getId();
            Log.e("Maps/BuildMarker","CircelId: " + circleId + " MarkerId: " + markerId);
            markerCircelMapping.put(m.getId(), circle);
            markerLocationMapping.put(m.getId(), testLocations[i]);
        }
    }

    private void setMarkerChangeVisibility(Boolean visibility){
        if (visibility){
            radius.setVisibility(View.VISIBLE);
            radiusTextUnit.setVisibility(View.VISIBLE);
            radiusText.setVisibility(View.VISIBLE);
            radiusTextDescription.setVisibility(View.VISIBLE);
            mapMarkerName.setVisibility(View.VISIBLE);
            mapMarkerEditName.setVisibility(View.VISIBLE);
        }else{
            radius.setVisibility(View.INVISIBLE);
            radiusTextUnit.setVisibility(View.INVISIBLE);
            radiusText.setVisibility(View.INVISIBLE);
            radiusTextDescription.setVisibility(View.INVISIBLE);
            mapMarkerName.setVisibility(View.INVISIBLE);
            mapMarkerEditName.setVisibility(View.INVISIBLE);
        }
    }

    private void startGeofencing(){
        setUpGeofenceList();
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    private void setUpGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations[0].getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations[0].getLocation().latitude, testLocations[0].getLocation().longitude, testLocations[0].getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations[1].getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(testLocations[1].getLocation().latitude, testLocations[1].getLocation().longitude, testLocations[1].getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations[2].getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setCircularRegion(testLocations[2].getLocation().latitude, testLocations[2].getLocation().longitude, testLocations[0].getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(5000)
                .build());
        addMarkerToMap();
    }


    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        Log.d("Maps", "getGEofencePendingIntent triggered!");
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransistionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void startLocationUpdates() {
        createLocationRequest();

    }

    @Override
    public void onResult(Status status) {
        Log.e("Maps/geofancing/Log", "Status = " + status.getStatus());
        }

    @Override
    public void onConnected(Bundle bundle) {
        mMap.setMyLocationEnabled(true);
        Log.e("Maps/GoogleAp/conn", "Connection succ");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        setCameraFocus(mLastLocation);
        Log.e("Maps/googleAp/LaLo", String.valueOf(mLastLocation.getLatitude()) + ", " + String.valueOf(mLastLocation.getLongitude()));
        startLocationUpdates();
        startGeofencing();
    }

    private void setCameraFocus(Location mLastLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Maps/GoogleApi/connSus", "Connection fail");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Maps/GoogleApi/connFail", "Connection failed");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        NotificationFactory.createNotification(this, "Test Map Marker", marker.getId(), false);
        // set Name/Radius visible
        setMarkerChangeVisibility(true);
        // Save Marker to change radius
        activeMarker = marker;
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //create new marker
        NotificationFactory.createNotification(this, "Test Map Long Click", "Create new Marker", false);
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
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // unselect active Marker
        activeMarker = null;
        // search possibility to hide progressbar
        setMarkerChangeVisibility(false);
    }
}
