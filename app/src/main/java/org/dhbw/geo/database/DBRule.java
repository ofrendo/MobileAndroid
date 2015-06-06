package org.dhbw.geo.database;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.05.2015.
 */
public class DBRule extends DBObject {
    private String name;

    private ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
    private ArrayList<DBAction> actions = new ArrayList<DBAction>();

    public DBRule(int id, String name, int priority){
        super(DBHelper.TABLE_RULE, id);
        this.id = id;
        this.name = name;
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
    public void writeToDB() {

    }

    @Override
    public void deleteFromDB() {

    }
}