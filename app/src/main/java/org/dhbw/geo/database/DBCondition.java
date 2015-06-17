package org.dhbw.geo.database;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBCondition extends DBObject {
    private DBRule rule;
    private String name;

    public static ArrayList<DBCondition> selectAllFromDB(long ruleId){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // get through all condition classes
        conditions.addAll(DBConditionFence.selectAllFromDB(ruleId));
        conditions.addAll(DBConditionTime.selectAllFromDB(ruleId));
        return conditions;
    }

    public DBCondition(){

    }

    public DBCondition(long id, String name){
        super(id);
        this.name = name;
    }

    public void setRule(DBRule rule) {
        this.rule = rule;
    }

    public abstract void removeRuleFromDB();

    public abstract void writeRuleToDB();

    public abstract boolean isConditionMet();

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