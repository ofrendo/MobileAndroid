package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.dhbw.geo.services.ContextManager;

import java.util.ArrayList;

/**
 * A geofence condition is a set of geofences which are observed.
 * @author Matthias
 */
public class DBConditionFence extends DBCondition {
    /**
     * the type for entering a geofence
     */
    public static final String TYPE_ENTER = "Enter";   // The condition is triggered when you enter one of the geofences
    /**
     * the type for leaving a geofence
     */
    public static final String TYPE_LEAVE = "Leave";   // The condition is triggered when you leave one of the geofences
    /**
     * the type for staying in a geofence
     */
    public static final String TYPE_STAY = "Stay";     // The condition is triggered when you stay in the geofence
    /**
     * the type of the geofences. Can be Enter, Leave or Stay
     */
    private String type;
    /**
     * a list of all observed geofences for this condition
     */
    private ArrayList<DBFence> fences = new ArrayList<DBFence>();
    /**
     * a unique id set on the server
     */
    private int serverId = -1;

    /**
     * Selects all geofence conditions from the database.
     * @return an arraylist of the geofence conditions fetched from the database
     */
    public static ArrayList<DBCondition> selectAllFromDB(){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_CONDITION_FENCE_ID + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_TYPE + " AS " + DBHelper.COLUMN_TYPE + ", " +
                DBHelper.COLUMN_SERVER_ID +
                " FROM " + DBHelper.TABLE_CONDITION_FENCE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION;
        Cursor cursor = db.rawQuery(query, new String[]{});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBConditionFence fence = new DBConditionFence(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
            conditions.add(fence);
            cursor.moveToNext();
        }
        return conditions;
    }

    /**
     * Selects all geofence conditions from the database which are assigned to a given rule.
     * @param ruleId the id of the rule for which the geofence conditions shall be selected
     * @return an arraylist of the geofence conditions fetched from the database
     */
    public static ArrayList<DBCondition> selectAllFromDB(long ruleId){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_CONDITION_FENCE_ID + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.TABLE_CONDITION_FENCE + "." + DBHelper.COLUMN_TYPE + " AS " + DBHelper.COLUMN_TYPE + ", " +
                DBHelper.COLUMN_SERVER_ID +
                " FROM " + DBHelper.TABLE_CONDITION_FENCE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_RULE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ruleId)});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBConditionFence fence = new DBConditionFence(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
            conditions.add(fence);
            cursor.moveToNext();
        }
        return conditions;
    }

    /**
     * Selects a specific geofence condition from the database.
     * @param id the id of the geofence condition
     * @return the geofence condition fetched from the database
     */
    public static DBConditionFence selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_CONDITION_FENCE_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_TYPE,
                DBHelper.COLUMN_SERVER_ID
        };
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_CONDITION_FENCE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBConditionFence conditionFence = new DBConditionFence(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        return conditionFence;
    }

    public DBConditionFence(){
        super();
        this.type = TYPE_ENTER; // set a default type
    }

    /**
     * Creates a new geofence condition.
     * Use this to create geofence conditions fetched from the databse
     * @param id the id of the geofence
     * @param name the name of the geofence
     * @param type the type of the geofence
     */
    public DBConditionFence(long id, String name, String type, int serverId){
        super(id, name);
        this.type = type;
        this.serverId = serverId;
    }

    /**
     * Adds a geofence to the condition
     * @param fence the geofence to be added
     */
    public void addFence(DBFence fence){
        fences.add(fence);
        fence.setConditionFence(this);
    }

    /**
     * removes a geofence from the condition
     * @param fence the geofence to be removed
     */
    public void removeFence(DBFence fence){
        fences.remove(fence);
        fence.setConditionFence(null);
    }

    /**
     * Loads all geofences of the condition from the database.
     * It only calls the database, if no geofences are loaded to the condition yet.
     */
    public void loadAllFences() {
        if (fences.size() != 0) {
            return; // don't load fences if they are already loaded!
        }
        fences.addAll(DBFence.selectAllFromDB(getId()));
        // set conditionFence for the fences!
        for(int i = 0; i < fences.size(); i++){
            fences.get(i).setConditionFence(this);
        }
    }

    /**
     * Inserts the geofence condition into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted geofence condition
     */
    @Override
    protected long insertIntoDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_SERVER_ID, serverId);
        long id = db.insert(DBHelper.TABLE_CONDITION_FENCE, null, values);
        setId(id);
        if(getRule() != null){
            writeRuleToDB();
        }
        return id;
    }

    /**
     * Updates the geofence condition on the database.
     * @param db the reference to the sqlite database
     */
    @Override
    protected void updateOnDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_TYPE, type);
        values.put(DBHelper.COLUMN_SERVER_ID, serverId);
        String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_CONDITION_FENCE, values, where, whereArgs);
        if(getRule() != null){
            writeRuleToDB();
        }
    }

    /**
     * Deletes the geofence condition from the database.
     */
    @Override
    public void deleteFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON;");
            String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId())};
            db.delete(DBHelper.TABLE_CONDITION_FENCE, where, whereArgs);
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't delete fence condition from database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Removes the conjunction to the rule.
     * This doesn't delete the rule or the condition either.
     */
    @Override
    public void removeRuleFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            String where = DBHelper.COLUMN_CONDITION_FENCE_ID + " = ? AND " + DBHelper.COLUMN_RULE_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId()), String.valueOf(getRule().getId())};
            db.delete(DBHelper.TABLE_RULE_CONDITION, where, whereArgs);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't remove rule from fence condition on database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Inserts the conjunction of the condition with the assigned rule
     */
    @Override
    protected void writeRuleToDB() {
        try {
            removeRuleFromDB(); // in case it was already written on the database; avoid duplicates
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
            values.put(DBHelper.COLUMN_CONDITION_FENCE_ID, getId());
            db.insert(DBHelper.TABLE_RULE_CONDITION, null, values);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't write rule to fence condition on database!", Toast.LENGTH_SHORT);
        }

    }

    /**
     * Checks whether the condition is fulfilled.
     * It is fulfilled, if the cell phone is in any geofence ({@link #type}: Enter) or in none ({@link #type}: Leave)
     * @return true if the condition is fulfilled, false otherwise
     */
    @Override
    public boolean isConditionMet() {
        boolean condition;
        if(type == TYPE_ENTER){
            condition = false;
            for(int i = 0; i < fences.size(); i++){
                if(fences.get(i).isInFence()) condition = true;
            }
        } else if(type == TYPE_LEAVE){
            condition = true;
            for(int i = 0; i < fences.size(); i++){
                if(fences.get(i).isInFence()) condition = false;
            }
        } else {
            condition = false;
        }
        return condition;
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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
