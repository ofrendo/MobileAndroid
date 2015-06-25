package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.dhbw.geo.services.ContextManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A rule contains conditions and actions. The actions are fired, if all conditions are fulfilled.
 * @author Matthias
 */
public class DBRule extends DBObject {

    private static final String TAG = "DBRule";
    /**
     * the name for the rule (an arbitrary name chosen by the user)
     */
    private String name;
    /**
     * the flag wheter this rule is active
     */
    private boolean active;

    /**
     * the arraylist containing all conditions assigned to this rule
     */
    private ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
    /**
     * the arraylist containing all actions assigned to this rule
     */
    private ArrayList<DBAction> actions = new ArrayList<DBAction>();

    public DBRule(){

    }

    /**
     * Creates a new rule.
     * Use this to create rules fetched from teh database.
     * @param id the id of the rule
     * @param name the name of the rule
     * @param active the flag whether the rule is active
     */
    public DBRule(long id, String name, boolean active){
        super(id);
        this.name = name;
        this.active = active;
    }

    /**
     * Adds a condition to the rule.
     * @param condition the condition to be added
     */
    public void addCondition(DBCondition condition){
        conditions.add(condition);
        condition.setRule(this);
    }

    /**
     * Adds an action to the rule.
     * @param action the action to be added
     */
    public void addAction(DBAction action){
        actions.add(action);
        action.setRule(this);
    }

    /**
     * Performs the startAction for all actions of this rule.
     */
    public void startAllActions(){
        // load all actions if there aren't any actions existent
        loadAllActions();
        // start all actions if the rule is active
        if(active) {
            for (int i = 0; i < actions.size(); i++) {
                actions.get(i).startAction();
            }
        }
    }

    /**
     * Registers alarm for all time conditions assigned to this rule
     */
    public void registerAllAlarms(){
        // load all conditions if there aren't any actions existent
        loadAllConditions();
        // loop through conditions and set alarm
        for(int i = 0; i < conditions.size(); i++){
            if(conditions.get(i) instanceof DBConditionTime){
                DBConditionTime condition = (DBConditionTime) conditions.get(i);
                condition.updateAlarm();
            }
        }
    }

    /**
     * Inserts the rule into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted rule
     */
    @Override
    protected long insertIntoDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_ACTIVE, active);
        return db.insert(DBHelper.TABLE_RULE, null, values);
    }

    /**
     * Updates the rule on the database.
     * @param db the reference to the sqlite database
     */
    @Override
    protected void updateOnDB(SQLiteDatabase db) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_ACTIVE, active);
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_RULE, values, where, whereArgs);
    }

    /**
     * Deletes the rule from the database
     */
    @Override
    public void deleteFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON;");
            String where = DBHelper.COLUMN_RULE_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId())};
            db.delete(DBHelper.TABLE_RULE, where, whereArgs);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't delete rule from database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Selects all rules from the database.
     * @return an arraylist containing all rules
     */
    public static ArrayList<DBRule> selectAllFromDB(){
        ArrayList<DBRule> rules = new ArrayList<DBRule>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_RULE_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_ACTIVE
        };
        Cursor cursor = db.query(DBHelper.TABLE_RULE, columns, null, null, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBRule rule = new DBRule(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0);
            rules.add(rule);
            cursor.moveToNext();
        }
        return rules;
    }

    /**
     * Selects a specific rule from the database
     * @param id the id of the rule to be fetched
     * @return the rule fetched from the database
     */
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

    /**
     * Selects all rules which are assigned to a condition
     * @param condition the condition
     * @return an arraylist of the rules assigned to the given condition
     */
    public static ArrayList<DBRule> selectFromDB(DBCondition condition){
        ArrayList<DBRule> rules = new ArrayList<DBRule>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_RULE_ID + ", " +
                DBHelper.TABLE_RULE + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.COLUMN_ACTIVE +
                " FROM " + DBHelper.TABLE_RULE + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_CONDITION_FENCE_ID + " = ? OR " + DBHelper.COLUMN_CONDITION_TIME_ID+ " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(condition.getId()), String.valueOf(condition.getId())});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBRule rule = new DBRule(cursor.getLong(0), cursor.getString(1), cursor.getInt(2)!=0);
            rules.add(rule);
            cursor.moveToNext();
        }
        return rules;
    }

    /**
     * Loads all actions assigned to this rule from the database.
     */
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

    /**
     * Loads all conditions assigned to this rule from the database.
     */
    public void loadAllConditions() {
        if(conditions.size() != 0){
            return; // don't load conditions if they already exist!
        }
        conditions.addAll(DBCondition.selectAllFromDB(getId()));
        // add rule for the conditions
    }

    /**
     * Checks wheter all conditions are met.
     * @return true, if all conditions are met, false otherwise
     */
    public boolean allConditionsMet(){
        // load all conditions in case they aren't loaded
        loadAllConditions();
        // check all conditions
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