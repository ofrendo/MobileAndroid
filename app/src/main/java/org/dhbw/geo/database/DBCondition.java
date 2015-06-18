package org.dhbw.geo.database;

import java.util.ArrayList;

/**
 * A condition is used to define whether the rule's actions shall be triggered or not.
 * @author Matthias
 */
public abstract class DBCondition extends DBObject {
    /**
     * the rule the condition is assigned to
     */
    private DBRule rule;
    /**
     * the name of the condition (an arbitrary name given by the user)
     */
    private String name = "";

    /**
     * Selects all conditions from the database which are assigned to a given rule.
     * @param ruleId the id of the rule for which the conditions shall be selected
     * @return an arraylist of the conditions fetched from the database
     */
    public static ArrayList<DBCondition> selectAllFromDB(long ruleId){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // get through all condition classes
        conditions.addAll(DBConditionFence.selectAllFromDB(ruleId));
        conditions.addAll(DBConditionTime.selectAllFromDB(ruleId));
        return conditions;
    }

    public DBCondition(){

    }

    /**
     * Creates a new condition.
     * Use this to create conditions fetched from the database.
     * @param id the id of the condition
     * @param name the name of the condition
     */
    public DBCondition(long id, String name){
        super(id);
        this.name = name;
    }

    public void setRule(DBRule rule) {
        this.rule = rule;
    }

    /**
     * Removes the conjunction to the rule.
     * This doesn't delete the rule or the condition either.
     */
    public abstract void removeRuleFromDB();

    /**
     * Inserts the conjunction of the condition with the assigned rule.
     */
    protected abstract void writeRuleToDB();

    /**
     * Checks whether the condition is fulfilled.
     * @return true if the condition is fulfilled, false otherwise
     */
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