package org.dhbw.geo.database;

/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBCondition extends DBObject {
    private DBRule rule;


    public void setRule(DBRule rule) {
        this.rule = rule;
    }
}