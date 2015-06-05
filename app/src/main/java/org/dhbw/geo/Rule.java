package org.dhbw.geo;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.05.2015.
 */
public class Rule extends DBObject {
    private String name;

    private ArrayList<Condition> conditions = new ArrayList<Condition>();
    private ArrayList<Action> actions = new ArrayList<Action>();

    public Rule(int id, String name, int priority){
        super(DBHelper.TABLE_RULE, id);
        this.id = id;
        this.name = name;
    }

    public void addCondition(Condition condition){
        conditions.add(condition);
        condition.setRule(this);
    }

    public void addAction(Action action){
        actions.add(action);
        action.setRule(this);
    }
}
