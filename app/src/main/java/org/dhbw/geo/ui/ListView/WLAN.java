package org.dhbw.geo.ui.ListView;

import android.util.Log;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBActionSimple;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.services.ContextManager;

/**
 * Group-Class for WLAN
 * @author Joern
 */
public class WLAN extends Group {
    DBActionSimple action;
    Child status;

    public WLAN(DBRule rule){
        super (ContextManager.getContext().getString(R.string.action_wifi));

        action = new DBActionSimple();
        action.setType(DBActionSimple.TYPE_WIFI);
        rule.addAction(action);
        action.setActive(active);

        status = new Child(this,ContextManager.getContext().getString(R.string.action_Status),false,false);

        action.setStatus(status.checked);
        add(status);
    }
    public WLAN(DBActionSimple action){
        super(ContextManager.getContext().getString(R.string.action_wifi));
        this.action = action;
        status = new Child(this,ContextManager.getContext().getString(R.string.action_Status),action.isStatus(),false);
        active = action.isActive();
        add(status);
    }

    @Override
    public void saveToDB() {
        action.setStatus(status.checked);
        action.setActive(active);
        action.writeToDB();
    }
}
