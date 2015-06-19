package org.dhbw.geo.ui.ListView;

import org.dhbw.geo.database.DBActionNotification;
import org.dhbw.geo.database.DBRule;

/**
 * Created by Joern on 15.06.2015.
 */
public class Notification extends Group {
    Child notification;
    DBActionNotification action;
    public Notification(DBRule rule){
        super("Notification");
        action = new DBActionNotification();
        action.setActive(active);
        action.setRule(rule);

        notification = new Child(this,"Message","");

        action.setMessage(notification.text);

        add(notification);
    }
    public Notification(DBActionNotification action){


        super("Notification");
        this.action = action;

        this.notification = new Child(this,"Message",action.getMessage());

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
