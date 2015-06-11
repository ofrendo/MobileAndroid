package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Matthias on 11.06.2015.
 */
public class DBActionSimple extends DBAction {

    public static final String TYPE_WIFI = "wifi";    // turn wifi on / off
    public static final String TYPE_BLUETOOTH = "bluetooth";    // turn bluetooth on / off

    private String type;
    private boolean status;

    public static DBActionSimple selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_SIMPLE_ID,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_STATUS,
                DBHelper.COLUMN_RULE_ID
        };
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_SIMPLE, columns, null, null, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionSimple action = new DBActionSimple(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0);
        return action;
    }

    public DBActionSimple(){

    }

    public DBActionSimple(long id, String type, boolean status){
        super(id);
        this.type = type;
        this.status = status;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        return db.insert(DBHelper.TABLE_ACTION_SIMPLE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_STATUS, status);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        String where = DBHelper.COLUMN_ACTION_SIMPLE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_SIMPLE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
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
