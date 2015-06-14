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
    public boolean active = false;


    public Group(String string) {
        this.string = string;
    }
    public void add(Child child){
        children.add(child);
    }
    public void setActive (boolean active){
        this.active = active;
    }


    public ArrayList<String> getActions(){
        if (active){
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i<children.size();i++){
                list.add(string+" : "+children.get(i).getAction());
            }
            return list;
        }
        else
            return null;
    }


}