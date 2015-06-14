package org.dhbw.geo.Map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Max on 14.06.2015.
 */
public class TestLocation {
    private LatLng location;
    private String name;

    public TestLocation(LatLng postion, String name) {
        this.location = postion;
        this.name = name;
    }

    public LatLng getLocation(){
        return location;
    }

    public String getName(){
        return name;
    }
}
