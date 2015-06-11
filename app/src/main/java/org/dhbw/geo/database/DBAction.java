package org.dhbw.geo.database;

/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBAction extends DBObject {
    private DBRule rule;

    public DBAction(){

    }

    public DBAction(long id){
        super(id);
    }

    public void setRule(DBRule rule) {
        this.rule = rule;
    }

    public DBRule getRule() {
        return rule;
    }
}