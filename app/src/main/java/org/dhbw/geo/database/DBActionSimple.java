package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.dhbw.geo.hardware.HardwareController;

import java.util.ArrayList;

/**
 * Created by Matthias on 11.06.2015.
 */
public class DBActionSimple extends DBAction {

    public static final String TYPE_WIFI = "wifi";    // turn wifi on / off
    public static final String TYPE_BLUETOOTH = "bluetooth";    // turn bluetooth on / off

    private String type;
    private boolean status;

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

    @Override
    public void doAction() {
        switch(type){
            case TYPE_WIFI:
                HardwareController.getInstance().setWifi(status);
                break;
            default:
                Log.d("DBActionSimple", "Invalid type!");
        }
    }

    public DBActionSimple(long id, String type, boolean status, boolean active){
        super(id, active);
        this.type = type;
        this.status = status;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        return db.insert(DBHelper.TABLE_ACTION_SIMPLE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_SIMPLE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_ACTION_SIMPLE, where, whereArgs);
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
