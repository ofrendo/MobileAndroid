package org.dhbw.geo.database;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An action is a task which can be executed. Every action is assigned to a rule. The specific action can be to turn on wifi etc.
 * @author Matthias
 */
public abstract class DBAction extends DBObject {
    /**
     * the rule the action is assigned to
     */
    private DBRule rule;
    /**
     * the flag whether the rule is active
     * This is a user setting whether the action should be fired at all. It doesn't represent whether the action has been started!
     */
    private boolean active;

    /**
     * Selects all actions from the sqlite database which are assigned to a given rule.
     * @param ruleId the id of the rule
     * @return an arraylist of the actions fetched from the database
     */
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

    /**
     * Starts / Performs the specific action.
     * It is used by {@link #doActionStart()}.
     */
    protected abstract void doActionStart();

    /**
     * Stops the specific action. (optional)
     * This is only needed, if the start action should be undone after the condition became invalid.
     * it is used by {@link: #doActionStop()}.
     */
    protected abstract void doActionStop();

    /**
     * Starts / Performs the specific action, if the {@link #active} flag is true.
     * @see #doActionStart()
     */
    public void startAction(){
        if(active){
            doActionStart();
        }
    }

    /**
     * Stops the specific action, if the {@link #active} flag is true.
     * @see #doActionStop()
     */
    public void stopAction(){
        if(active){
            doActionStop();
        }
    }

    /**
     * Creates a new action with a given id.
     * Use this to create actions fetched from a database.
     * @param id the id of the object in the database
     * @param active the flag whether the action is activated
     */
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