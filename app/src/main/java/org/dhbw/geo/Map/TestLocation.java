package org.dhbw.geo.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Max on 14.06.2015.
 */
public class TestLocation {
    private LatLng location;
    private String name;
    private int radius;

    public TestLocation(LatLng postion, String name, int radius) {
        this.location = postion;
        this.name = name;
        this.radius = radius;
    }

    public LatLng getLocation(){
        return location;
    }

    public String getName(){
        return name;
    }

    public int getRadius(){
        return radius;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }


}
