package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 15.06.2015.
 */
public class WLAN extends Group {
    public WLAN(){
        super("WLAN");
        add(new Child("Status", false, false));
    }

}
