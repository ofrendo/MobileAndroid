package org.dhbw.geo;

/**
 * Created by Matthias on 12.05.2015.
 */
public class Condition extends DBObject {
    private Rule rule;

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}