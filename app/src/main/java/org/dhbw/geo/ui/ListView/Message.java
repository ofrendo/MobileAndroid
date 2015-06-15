package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 15.06.2015.
 */
public class Message extends Group {
    public Message(){
        super("Message");
        add(new Child("Number","015468754",true));
        add(new Child("Message","Hallo"));
    }
}
