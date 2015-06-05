package org.dhbw.geo.database;

import org.dhbw.geo.Rule;
import org.dhbw.geo.database.DBObject;
/**
 * Created by Matthias on 12.05.2015.
 */
public abstract class DBAction extends DBObject {
    private DBRule rule;

    public void setRule(DBRule rule) {
        this.rule = rule;
    }
}