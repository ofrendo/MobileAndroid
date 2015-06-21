package org.dhbw.geo.backend;

import android.util.Log;

import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Oliver on 21.06.2015.
 */
public class JSONConverter {

    public static DBFence getFence(String jsonResult) {
        DBFence result = null;
        try {
            JSONObject obj = new JSONObject(jsonResult);
            result = createFence(obj);
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }
    public static ArrayList<DBFence> getFences(String jsonResult) {
        ArrayList<DBFence> result = new ArrayList<DBFence>();
        try {
            JSONArray array = new JSONArray(jsonResult);
            for (int i=0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                DBFence fence = createFence(obj);
                result.add(fence);
            }
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }

    public static DBConditionFence getFenceGroup(String jsonResult) {
        DBConditionFence result = null;
        try {
            JSONObject obj = new JSONObject(jsonResult);
            result = createFenceGroup(obj);
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }

    public static ArrayList<DBConditionFence> getFenceGroups(String jsonResult) {
        ArrayList<DBConditionFence> result = new ArrayList<DBConditionFence>();
        try {
            JSONArray array = new JSONArray(jsonResult);
            for (int i=0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                DBConditionFence fenceGroup = createFenceGroup(obj);
                result.add(fenceGroup);
            }
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }

    private static DBConditionFence createFenceGroup(JSONObject obj) {
        DBConditionFence result = null;
        try {
            String name = (obj.isNull("name")) ? "null" : (String) obj.get("name");
            String type = (obj.isNull("type")) ? "null" : (String) obj.get("type");

            result = new DBConditionFence(
                   (int) obj.get("fence_group_id"),
                   name,
                   type
            );
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }
    private static DBFence createFence(JSONObject obj) {
        DBFence result = null;
        try {
            result = new DBFence(
                    (int) obj.get("fence_id"),
                    (float) obj.get("lat"),
                    (float) obj.get("lng"),
                    (int) obj.get("radius")
            );
        }
        catch (Exception e) {
            Log.i("JSONConverter", "Error: " + e.getMessage());
        }
        return result;
    }

}
