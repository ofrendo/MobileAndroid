package org.dhbw.geo.database;

import android.content.ContentValues;
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

    public DBRule(int id, String name, boolean active){
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

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_ACTIVE, active);
        return db.insert(DBHelper.TABLE_RULE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {

    }

    @Override
    public void deleteFromDB() {

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