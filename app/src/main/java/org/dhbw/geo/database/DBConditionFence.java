package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBConditionFence extends DBCondition {
    public static final String TYPE_ENTER = "Enter";   // The condition is triggered when you enter one of the geofences
    public static final String TYPE_LEAVE = "Leave";   // The condition is triggered when you leave one of the geofences
    public static final String TYPE_STAY = "Stay";     // The condition is triggered when you stay in the geofence

    private String type;
    private ArrayList<DBFence> fences = new ArrayList<DBFence>();

    public static ArrayList<DBCondition> selectAllFromDB(long ruleId){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_CONDITION_FENCE_ID + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_TYPE + " AS " + DBHelper.COLUMN_TYPE +
                " FROM " + DBHelper.TABLE_CONDITION_FENCE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_RULE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ruleId)});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBConditionFence fence = new DBConditionFence(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            conditions.add(fence);
            cursor.moveToNext();
        }
        return conditions;
    }

    public static DBConditionFence selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
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
        super();
        this.type = TYPE_ENTER; // set a default type
    }

    public DBConditionFence(long id, String name, String type){
        super(id, name);
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
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_TYPE, type);
        return db.insert(DBHelper.TABLE_CONDITION_FENCE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_TYPE, type);
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_CONDITION_FENCE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_CONDITION_FENCE, where, whereArgs);
    }

    @Override
    public void removeRuleFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ? AND " + DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId()), String.valueOf(getRule().getId())};
        db.delete(DBHelper.TABLE_RULE_CONDITION, where, whereArgs);
    }

    @Override
    protected void writeRuleToDB() {
        removeRuleFromDB(); // in case it was already written on the database; avoid duplicates
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_CONDITION_FENCE_ID, getId());
        db.insert(DBHelper.TABLE_RULE_CONDITION, null, values);
    }

    @Override
    public boolean isConditionMet() {
        // TODO: implement this!
        return true;
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
