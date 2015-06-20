package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.dhbw.geo.hardware.HardwareController;

import java.util.ArrayList;

/**
 * This action sets the sound setting for a specific audio stream.
 * @author Matthias
 */
public class DBActionSound extends DBAction {
    // the sound types are now used from class AudioManager

    // the sound statuses
    public static final String STATUS_SOUND = "Sound";
    //public static final String STATUS_VIBRATE = "Vibrate";
    public static final String STATUS_MUTE = "Mute";

    /**
     * the type of the audio stream. Types are defined in AudioManager (android class).
     */
    private int type;
    /**
     * the type of the sound. Can be Sound or Mute at the current time
     */
    private String status;
    /**
     * the volume setting for the audio stream
     */
    private int volume;

    /**
     * Creates a new sound action.
     * Use this to create sound actions fetched from the database.
     * @param id the id of the sound action
     * @param type the type of the sound action
     * @param status the status of the sound action
     * @param volume the volume of the sound action
     * @param active the flag whether the sound action is active
     */
    public DBActionSound(long id, int type, String status, int volume, boolean active) {
        super(id, active);
        this.type = type;
        this.status = status;
        this.volume = volume;
    }

    public DBActionSound(){

    }

    /**
     * Sets the audio status and the volume.
     */
    @Override
    protected void doActionStart() {
        // set volume
        HardwareController.getInstance().setAudioVolume(type, volume);
        // set audio status
        switch (status){
            case STATUS_MUTE:
                HardwareController.getInstance().setAudioStatus(type, false);
                break;
            case STATUS_SOUND:
                HardwareController.getInstance().setAudioStatus(type, true);
                break;
        }
    }

    /**
     * Selects all sound actions from the database which are assigned to a given rule.
     * @param ruleId the id of the rule for which the sound actions shall be selected
     * @return an arraylist of the sound actions fetched from the database
     */
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

    /**
     * Selects a specific sound action from the database.
     * @param id the id of the sound action
     * @return the fetched sound action
     */
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

    /**
     * Inserts the sound action into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted sound action
     */
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

    /**
     * Updates the sound action on the database.
     * @param db the reference to the sqlite database
     */
    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_VOLUME, volume);
        if(getRule()!= null){
            values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        }
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        String where = DBHelper.COLUMN_ACTION_SOUND_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_SOUND, values, where, whereArgs);
    }

    /**
     * Deletes the sound action from the database.
     */
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
