package org.dhbw.geo.ui.ListView;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBActionNotification;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.services.ContextManager;

/**
 * Group-Class for WLAN
 * @author Joern
 */
public class Notification extends Group {
    Child notification;
    DBActionNotification action;
    public Notification(DBRule rule){
        super(ContextManager.getContext().getString(R.string.action_Notification));
        action = new DBActionNotification();
        action.setActive(active);
        action.setRule(rule);

        notification = new Child(this,ContextManager.getContext().getString(R.string.action_text),"");

        action.setMessage(notification.text);

        add(notification);
    }
    public Notification(DBActionNotification action){


        super(ContextManager.getContext().getString(R.string.action_Notification));
        this.action = action;

        this.notification = new Child(this,ContextManager.getContext().getString(R.string.action_text),action.getMessage());

        //set active status
        active = action.isActive();

        //add child
        add(notification);
    }

    @Override
    public void saveToDB() {
        action.setMessage(notification.text);
        action.setActive(active);
        action.writeToDB();
    }
}
