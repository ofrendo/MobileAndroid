package org.dhbw.geo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBAction extends DBObject {
    private DBRule rule;
    private boolean active;

    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // get through all action classes
        actions.addAll(DBActionSimple.selectAllFromDB(ruleId));
        actions.addAll(DBActionSound.selectAllFromDB(ruleId));
        actions.addAll(DBActionBrightness.selectAllFromDB(ruleId));
        actions.addAll(DBActionNotification.selectAllFromDB(ruleId));
        actions.addAll(DBActionMessage.selectAllFromDB(ruleId));
        return actions;
    }

    public DBAction(){

    }

    protected abstract void doAction();

    public void performAction(){
        if(active){
            doAction();
        }
    }

    public DBAction(long id, boolean active){
        super(id);
        this.active = active;
    }

    public void setRule(DBRule rule) {
        this.rule = rule;
    }

    public DBRule getRule() {
        return rule;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}