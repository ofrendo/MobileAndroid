package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 05.06.2015.
 */

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String name;
    public final List<Child> children = new ArrayList<Child>();
    public boolean active = false;


    public Group(String name) {
        this.name = name;
    }
    public void add(Child child){
        children.add(child);
    }
    public void setActive (boolean active){
        this.active = active;
        saveToDB();
    }

    public void saveToDB(){
        return;
    };

    public ArrayList<String> getActions(){
        if (active){
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i<children.size();i++){
                list.add(name+" : "+children.get(i).getAction());
            }
            return list;
        }
        else
            return null;
    }


}