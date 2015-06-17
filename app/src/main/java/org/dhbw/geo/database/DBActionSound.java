package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.dhbw.geo.hardware.HardwareController;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBActionSound extends DBAction {
    // the sound types are now used from class AudioManager

    // the sound statuses
    public static final String STATUS_SOUND = "Sound";
    //public static final String STATUS_VIBRATE = "Vibrate";
    public static final String STATUS_MUTE = "Mute";

    private int type;
    private String status;
    private int volume;

    public DBActionSound(long id, int type, String status, int volume, boolean active) {
        super(id, active);
        this.type = type;
        this.status = status;
        this.volume = volume;
    }

    public DBActionSound(){

    }

    @Override
    protected void doActionStart() {
        // set audio status
        switch (status){
            case STATUS_MUTE:
                HardwareController.getInstance().setAudioStatus(type, false);
                break;
            case STATUS_SOUND:
                HardwareController.getInstance().setAudioStatus(type, true);
                break;
        }
        // set volume

    }

    @Override
    protected void doActionStop() {
        // TODO: discuss what to do here!! (dilemma?)
    }

    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SOUND_ID,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_STATUS,
                DBHelper.COLUMN_VOLUME,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(ruleId)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_SOUND, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBActionSound action = new DBActionSound(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4) != 0);
            actions.add(action);
            cursor.moveToNext();
        }
        return actions;
    }

    public static DBActionSound selectFromDB(long id){
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SOUND_ID,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_STATUS,
                DBHelper.COLUMN_VOLUME,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_ACTION_SOUND_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_SOUND, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionSound action = new DBActionSound(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4) != 0);
        return action;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_VOLUME, volume);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        return db.insert(DBHelper.TABLE_ACTION_SOUND, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_VOLUME, volume);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        String where = DBHelper.COLUMN_ACTION_SOUND_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_SOUND, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_ACTION_SOUND_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_ACTION_SOUND, where, whereArgs);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
