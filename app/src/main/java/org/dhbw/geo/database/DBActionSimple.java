package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.dhbw.geo.hardware.HardwareController;
import org.dhbw.geo.services.ContextManager;

import java.util.ArrayList;

/**
 * This class is used for simple actions which just contain one setting which can only be turned on or off.
 * At the current point it supports the following settings: WiFi, Bluetooth.
 * @author Matthias
 */
public class DBActionSimple extends DBAction {
    /**
     * the type for turning on / off WiFi connections
     */
    public static final String TYPE_WIFI = "wifi";              // turn wifi on / off
    /**
     * the type for turning on / off Bluetooth connections
     */
    public static final String TYPE_BLUETOOTH = "bluetooth";    // turn bluetooth on / off
    /**
     * the setting which shall be turned on / off
     */
    private String type;
    /**
     * the flag whether to turn the setting on (true) or off (false)
     */
    private boolean status;

    /**
     * Selects all simple actions from the database which are assigned to a given rule.
     * @param ruleId the id of the rule for which the simple actions shall be selected
     * @return an arraylist of the simple actions fetched from the database
     */
    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SIMPLE_ID,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_STATUS,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(ruleId)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_SIMPLE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBActionSimple action = new DBActionSimple(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0, cursor.getInt(3) != 0);
            actions.add(action);
            cursor.moveToNext();
        }
        return actions;
    }

    /**
     * Selects a specific simple action from the database.
     * @param id the id of the simple action
     * @return the fetched simple action
     */
    public static DBActionSimple selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SIMPLE_ID,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_STATUS,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_SIMPLE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionSimple action = new DBActionSimple(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0, cursor.getInt(3) != 0);
        return action;
    }

    public DBActionSimple(){

    }

    /**
     * turns on ({@link #status}: true) / off ({@link #status}: false) the setting ({@link #type})
     */
    @Override
    protected void doActionStart() {
        switch(type){
            case TYPE_WIFI:
                HardwareController.getInstance().setWifi(status);
                break;
            case TYPE_BLUETOOTH:
                HardwareController.getInstance().setBluetoothStatus(status);
                break;
            default:
                Log.d("DBActionSimple", "Invalid type!");
        }
    }

    /**
     * Creates a new simple action.
     * Use this to create notification actions fetched from the database.
     * @param id the id of the simple action
     * @param type the type of the action
     * @param status the status of the action
     * @param active the flag whether the action is active
     */
    public DBActionSimple(long id, String type, boolean status, boolean active){
        super(id, active);
        this.type = type;
        this.status = status;
    }

    /**
     * Inserts the simple action into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted simple action
     */
    @Override
    protected long insertIntoDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        return db.insert(DBHelper.TABLE_ACTION_SIMPLE, null, values);
    }

    /**
     * Updates the simple action on the database.
     * @param db the reference to the sqlite database
     */
    @Override
    protected void updateOnDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        if(getRule()!= null){
            values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        }
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_SIMPLE, values, where, whereArgs);
    }

    /**
     * Deletes the simple action from the database.
     */
    @Override
    public void deleteFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId())};
            db.delete(DBHelper.TABLE_ACTION_SIMPLE, where, whereArgs);
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't delete simple action from database!", Toast.LENGTH_SHORT);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
