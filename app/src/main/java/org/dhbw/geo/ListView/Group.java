package org.dhbw.geo.ListView;

/**
 * Created by Joern on 05.06.2015.
 */
import android.graphics.Color;

import org.dhbw.geo.R;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<Child> children = new ArrayList<Child>();
    public int color = COLORACTIVE;
    public static final int COLORACTIVE = Color.BLUE;
    public static final int COLORINACTIVE = Color.TRANSPARENT;

    public Group(String string) {
        this.string = string;
    }
    public void add(Child child){
        children.add(child);
    }
    public void setColor (int color){
        this.color = color;


    }


}