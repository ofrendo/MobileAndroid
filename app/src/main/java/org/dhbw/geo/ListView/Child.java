package org.dhbw.geo.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Joern on 05.06.2015.
 */
public class Child {
    public String name;

    public int type;
    public static final int CHECKBOX = 0;
    public static final int RADIOBUTTONS = 1;
    public static final int SLIDER = 2;
    public static final int SWITCH = 3;
    public static final int TEXTINPUT = 4;
    public static final int NUMBERINPUT = 5;

    //checkbox / switch
    public boolean checked;

    //checkbox
    public Child(String name, boolean checked, boolean checkBox){
        if (checkBox)
        type = CHECKBOX;
        else
        type = SWITCH;
        this.name = name;
        this.checked = checked;
    }

    //Radiobuttons
    public ArrayList<String> options;
    public String selectedOption;
    public Child (String name, ArrayList<String> options){
        type = RADIOBUTTONS;
        this.name = name;
        this.options = options;
    }
    public Child (String name, String [] options){
        type = RADIOBUTTONS;
        this.name = name;
        this.options = new ArrayList<String>(Arrays.asList(options));
    }


    //Slider
    public int value;
    public int min;
    public int max;


}
