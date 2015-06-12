package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBConditionFence extends DBCondition {
    private static final String TYPE_ENTER = "Enter";   // The condition is triggered when you enter one of the geofences
    private static final String TYPE_LEAVE = "Leave";   // The condition is triggered when you leave one of the geofences

    private String name;
    private String type;
    private ArrayList<DBFence> fences = new ArrayList<DBFence>();

    /*public static ArrayList<DBConditionFence> selectAllFromDB(long ruleId){
        ArrayList<DBConditionFence> conditionFences = new ArrayList<DBConditionFence>();
        // read from database
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_CONDITION_FENCE_ID + ", " +
                DBHelper.COLUMN_CONDITION_TIME_ID + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME +
                " FROM " + DBHelper.TABLE_CONDITION_FENCE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_RULE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ruleId)});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBFence fence = new DBFence(cursor.getLong(0), cursor.getFloat(1), cursor.getFloat(2), cursor.getInt(3));
            fences.add(fence);
            cursor.moveToNext();
        }
        return fences;
    }*/

    public static DBConditionFence selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getHelper().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_CONDITION_FENCE_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_TYPE
        };
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_CONDITION_FENCE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBConditionFence conditionFence = new DBConditionFence(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
        return conditionFence;
    }

    public DBConditionFence(){

    }

    public DBConditionFence(long id, String name, String type){
        super(id);
        this.name = name;
        this.type = type;
    }

    public void addFence(DBFence fence){
        fences.add(fence);
        fence.setConditionFence(this);
    }

    public void removeFence(DBFence fence){
        fences.remove(fence);
        fence.setConditionFence(null);
    }

    public void loadAllFences() {
        if (fences.size() == 0) {
            return; // don't load fences if they are already loaded!
        }
        fences.addAll(DBFence.selectAllFromDB(getId()));
        // set conditionFence for the fences!
        for(int i = 0; i < fences.size(); i++){
            fences.get(i).setConditionFence(this);
        }
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_TYPE, type);
        return db.insert(DBHelper.TABLE_CONDITION_FENCE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_TYPE, type);
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_CONDITION_FENCE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getHelper().getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_CONDITION_FENCE, where, whereArgs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<DBFence> getFences() {
        return fences;
    }
}
