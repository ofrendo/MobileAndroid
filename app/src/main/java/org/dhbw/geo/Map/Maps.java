package org.dhbw.geo.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dhbw.geo.R;

import java.util.ArrayList;
import java.util.Map;

public class Maps extends FragmentActivity implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //Geofencing
    private PendingIntent mGeofencePendingIntent;
    private ArrayList mGeofenceList = new ArrayList();
    private static final TestLocation[] testLocations = new TestLocation[] {
            new TestLocation(new LatLng(49.474225, 8.534489), new String("DHBW Mannheim, BW"), 50),
            new TestLocation( new LatLng(49.540980, 8.660827), new String("Weinheim, BW"), 50)
    };
    private GoogleApiClient mGoogleApiClient;


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
        getUpMap();
        setUpSeekerBar();
    }

    protected synchronized void buildGoogleApiClient() {

    }

    private void setUpSeekerBar() {
        SeekBar radius = (SeekBar) findViewById(R.id.map_radius_seekbar);
        radius.setProgress(testLocations[0].getRadius());
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView radiusText = (TextView) findViewById(R.id.map_radius);
                Log.d("Maps/Seekbar", String.valueOf(progress));
                radiusText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUpMap();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.540980, 8.660827), 10));
        Drawable iconDrawable = getResources().getDrawable(R.drawable.ic_play_light);
        Bitmap iconBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
        mMap.addMarker(new MarkerOptions().position(testLocations[0].getLocation()).title(testLocations[0].getName()));
        mMap.addMarker(new MarkerOptions().position(testLocations[1].getLocation()).title(testLocations[1].getName()));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void startGeofencing(){
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(testLocations[0].getName())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(testLocations[0].getLocation().latitude, testLocations[0].getLocation().longitude, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build());

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }


    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        if (mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransistionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        Log.e("Maps/geofancing/Log", "Test-Callback-Log-Scheiss");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("Maps/GoogleAp/conn", "Connection succ");
        startGeofencing();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Maps/GoogleApi/connSus", "Connection fail");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("Maps/GoogleApi/connFail", "Connection failed");
    }
}
