package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.dhbw.geo.map.Maps;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 * TODO: documentation!
 */
public class DBFence extends DBObject {

    private DBConditionFence conditionFence;
    private double latitude;
    private double longitude;
    private int radius;

    public static ArrayList<DBFence> selectAllFromDB(long conditionFenceId){
        ArrayList<DBFence> fences = new ArrayList<DBFence>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_FENCE_ID,
                DBHelper.COLUMN_LATITUDE,
                DBHelper.COLUMN_LONGITUDE,
                DBHelper.COLUMN_RADIUS
        };
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(conditionFenceId)};
        Cursor cursor = db.query(DBHelper.TABLE_FENCE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBFence fence = new DBFence(cursor.getLong(0), cursor.getFloat(1), cursor.getFloat(2), cursor.getInt(3));
            fences.add(fence);
            cursor.moveToNext();
        }
        return fences;
    }

    public static DBFence selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_FENCE_ID,
                DBHelper.COLUMN_LATITUDE,
                DBHelper.COLUMN_LONGITUDE,
                DBHelper.COLUMN_RADIUS
        };
        String where = DBHelper.COLUMN_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_FENCE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBFence fence = new DBFence(cursor.getLong(0), cursor.getFloat(1), cursor.getFloat(2), cursor.getInt(3));
        return fence;
    }

    public DBFence(){

    }

    public DBFence(long id, float latitude, float longitude, int radius){
        super(id);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LATITUDE, latitude);
        values.put(DBHelper.COLUMN_LONGITUDE, longitude);
        values.put(DBHelper.COLUMN_RADIUS, radius);
        values.put(DBHelper.COLUMN_CONDITION_FENCE_ID, getConditionFence().getId());
        return db.insert(DBHelper.TABLE_FENCE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LATITUDE, latitude);
        values.put(DBHelper.COLUMN_LONGITUDE, longitude);
        values.put(DBHelper.COLUMN_RADIUS, radius);
        values.put(DBHelper.COLUMN_CONDITION_FENCE_ID, getConditionFence().getId());
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_FENCE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_FENCE, where, whereArgs);
    }

    public DBConditionFence getConditionFence() {
        return conditionFence;
    }

    public void setConditionFence(DBConditionFence conditionFence) {
        this.conditionFence = conditionFence;
    }

    public LatLng getLatLng(){
        LatLng loc = new LatLng(latitude, longitude);
        return loc;
    }

    public Boolean isInFence(){
        // get current Location
        Maps maps = new Maps();
        Location currentLocation = maps.getCurrentLocation();
        // check if currentLocation is in geofence
        if(Math.pow(currentLocation.getLongitude() - longitude,2) + Math.pow((currentLocation.getLatitude() - latitude),2) < Math.pow(radius,2)){
            return true;
        }
        return false;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
