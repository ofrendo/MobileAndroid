package org.dhbw.geo.database;

/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBCondition extends DBObject {
    private DBRule rule;
    private String name;

    public DBCondition(){

    }

    public DBCondition(long id, String name){
        super(id);
        this.name = name;
    }

    public void setRule(DBRule rule) {
        this.rule = rule;
    }

    public DBRule getRule() {
        return rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}