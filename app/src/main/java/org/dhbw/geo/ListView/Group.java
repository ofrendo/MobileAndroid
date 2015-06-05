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


    public Group(String string) {
        this.string = string;
    }
    public void add(Child child){
        children.add(child);
    }





}