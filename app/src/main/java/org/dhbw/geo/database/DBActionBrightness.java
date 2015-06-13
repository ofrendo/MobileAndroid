package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBActionBrightness extends DBAction {

    private boolean automatic;
    private int value;

    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // read from database
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_BRIGHTNESS_ID,
                DBHelper.COLUMN_AUTOMATIC,
                DBHelper.COLUMN_VALUE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(ruleId)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_BRIGHTNESS, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBActionBrightness action = new DBActionBrightness(cursor.getLong(0), cursor.getInt(1) != 0, cursor.getInt(2));
            actions.add(action);
            cursor.moveToNext();
        }
        return actions;
    }

    public static DBActionBrightness selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SIMPLE_ID,
                DBHelper.COLUMN_AUTOMATIC,
                DBHelper.COLUMN_VALUE
        };
        String where = DBHelper.COLUMN_ACTION_BRIGHTNESS_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_BRIGHTNESS, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionBrightness action = new DBActionBrightness(cursor.getLong(0), cursor.getInt(1) != 0, cursor.getInt(2));
        return action;
    }

    public DBActionBrightness(){

    }

    @Override
    public void performAction() {

    }

    public DBActionBrightness(long id, boolean automatic, int value){
        super(id);
        this.automatic = automatic;
        this.value = value;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_AUTOMATIC, automatic);
        values.put(DBHelper.COLUMN_VALUE, value);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        return db.insert(DBHelper.TABLE_ACTION_BRIGHTNESS, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_AUTOMATIC, automatic);
        values.put(DBHelper.COLUMN_VALUE, value);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        String where = DBHelper.COLUMN_ACTION_BRIGHTNESS_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_BRIGHTNESS, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getHelper().getWritableDatabase();
        String where = DBHelper.COLUMN_ACTION_BRIGHTNESS_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_ACTION_BRIGHTNESS, where, whereArgs);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
}
