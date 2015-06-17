package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Matthias on 12.05.2015.
 */
public class DBRule extends DBObject {

    private static final String TAG = "DBRule";

    private String name;
    private boolean active;

    private ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
    private ArrayList<DBAction> actions = new ArrayList<DBAction>();

    public DBRule(){

    }

    public DBRule(long id, String name, boolean active){
        super(id);
        this.name = name;
        this.active = active;
    }

    public void addCondition(DBCondition condition){
        conditions.add(condition);
        condition.setRule(this);
    }

    public void addAction(DBAction action){
        actions.add(action);
        action.setRule(this);
    }

    public void performAllActions(){
        // load all actions if there aren't any actions existent
        loadAllActions();
        // perform all actions if the rule is active
        if(active) {
            for (int i = 0; i < actions.size(); i++) {
                actions.get(i).performAction();
            }
        }
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_ACTIVE, active);
        return db.insert(DBHelper.TABLE_RULE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_ACTIVE, active);
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_RULE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_RULE, where, whereArgs);
    }

    public static DBRule selectAllFromDB(){
        // TODO: implement it!
        return new DBRule();
    }

    public static DBRule selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_RULE_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_RULE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBRule rule = new DBRule(cursor.getLong(0), cursor.getString(1), cursor.getInt(2)!=0);
        return rule;
    }

    public static ArrayList<DBRule> selectFromDB(DBCondition condition){
        ArrayList<DBRule> rules = new ArrayList<DBRule>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_RULE_ID + ", " +
                DBHelper.TABLE_RULE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.COLUMN_ACTIVE +
                " FROM " + DBHelper.TABLE_RULE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(condition.getId())});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBRule rule = new DBRule(cursor.getLong(0), cursor.getString(1), cursor.getInt(2)!=0);
            rules.add(rule);
            cursor.moveToNext();
        }
        return rules;
    }

    public void loadAllActions() {
        if (actions.size() != 0) {
            return; // don't load actions if they already exist!
        }
        actions.addAll(DBAction.selectAllFromDB(getId()));
        // set rule for the actions!
        for(int i = 0; i < actions.size(); i++){
            actions.get(i).setRule(this);
        }
    }

    public void loadAllConditions() {
        if(conditions.size() != 0){
            return; // don't load conditions if they already exist!
        }
        conditions.addAll(DBCondition.selectAllFromDB(getId()));
        // add rule for the conditions
    }

    public boolean allConditionsMet(){
        // load all conditions in case they aren't loaded
        loadAllConditions();
        // check all conditions
        // TODO: implement this!
        HashMap<String, Boolean> classes = new HashMap<>();
        for(int i = 0; i < conditions.size(); i++){
            DBCondition condition = conditions.get(i);
            // create a new entry in the hashmap if it doesn't exist
            if(!classes.containsKey(condition.getClass().toString())){
                classes.put(condition.getClass().toString(), false);
            }
            // check the condition and reset value if condition is met and value is false
            if(condition.isConditionMet() && !classes.get(condition.getClass().toString())){
                classes.remove(condition.getClass().toString());
                classes.put(condition.getClass().toString(), true);
            }
            // check all entries in the hashmap
            Iterator<Boolean> iterator = classes.values().iterator();
            while(iterator.hasNext()){
                if(!iterator.next()){
                    Log.d(TAG, "allConditionsMet: Not all conditions are met!");
                    return false;
                }
            }
        }
        Log.d(TAG, "allConditionsMet: All conditions are met!");
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}