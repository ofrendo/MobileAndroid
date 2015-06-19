package org.dhbw.geo.ui.ListView;

import android.util.Log;

import org.dhbw.geo.database.DBActionSimple;
import org.dhbw.geo.database.DBRule;

/**
 * Created by Joern on 15.06.2015.
 */
public class WLAN extends Group {
    DBActionSimple action;
    Child status;

    public WLAN(DBRule rule){
        super ("WLAN");

        action = new DBActionSimple();
        action.setType(DBActionSimple.TYPE_WIFI);
        rule.addAction(action);
        action.setActive(active);

        status = new Child(this,"Status",false,false);

        action.setStatus(status.checked);
        add(status);
    }
    public WLAN(DBActionSimple action){
        super("WLAN");
        this.action = action;
        status = new Child(this,"Status",action.isStatus(),false);
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
