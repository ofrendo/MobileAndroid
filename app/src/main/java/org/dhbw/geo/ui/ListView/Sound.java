package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 15.06.2015.
 */
public class Sound extends Group {
    public Sound(){
        super("Sound");

        add(new Child(this, Child.ALARM));
        add(new Child(this, Child.MEDIA));
    }
}
