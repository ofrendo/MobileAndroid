package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.05.2015.
 */
public class DBRule extends DBObject {
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