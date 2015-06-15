package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 15.06.2015.
 */
public class Bluetooth extends Group{
    public Bluetooth(){
        super("Bluetooth");
        add(new Child("Status",false,false));
    }
}
