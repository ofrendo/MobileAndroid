package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 15.06.2015.
 */
public class Notification extends Group {
    public Notification(){
        super("Notification");
        add(new Child(this,"Text","Hallo"));
    }
    public Notification(String message){
        super("Notification");
        add(new Child(this,"Text",message));
    }
}
