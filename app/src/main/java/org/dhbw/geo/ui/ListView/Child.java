package org.dhbw.geo.ui.ListView;

import android.util.Log;

import org.dhbw.geo.database.DBActionSound;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Joern on 05.06.2015.
 */
public class Child {
    public String name;
    public Group parent;
    public int type;
    public static final int CHECKBOX = 0;
    public static final int RADIOBUTTONS = 1;
    public static final int SLIDER = 2;
    public static final int SWITCH = 3;
    public static final int TEXTINPUT = 4;
    public static final int NUMBERINPUT = 5;
    public static final int SOUND = 6;

    //checkbox / switch
    public boolean checked;

    //checkbox/switch
    public Child(Group parent, String name, boolean checked, boolean checkBox){
        this.parent = parent;
        if (checkBox)
        type = CHECKBOX;
        else
        type = SWITCH;
        this.name = name;
        this.checked = checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
        parent.saveToDB();
    }

    //Radiobuttons  //*************************unused*********************
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


    //Slider //*************Unused*****************
    public int value;
    public int min;
    public int max;
    public int range;
    public Child(String name, int min , int max , int value){
        type = SLIDER;
        this.name=name;
        this.min = min;
        this.max = max;
        this.value = value;
        this.range = max-min;
    }
    public Child(String name, int min , int max){
        type = SLIDER;
        this.name=name;
        this.min = min;
        this.max = max;
        this.value = ((max-min)/2)+min;
        this.range = max-min;
    }


    //text
    String text;
    public Child( Group parent,String name, String inputText){
        this.parent = parent;
        type = TEXTINPUT;
        this.text = inputText;
        this.name = name;


    }
    public void setText(String text){
        this.text = text;
        parent.saveToDB();
    }

    //numbertext
    String numberText;
    public Child(Group parent, String name, String input, boolean number){
        this.parent = parent;
        this.name = name;
        if (number){
            type = NUMBERINPUT;
            this.numberText = input;
        }
        else{
            type = TEXTINPUT;
            this.text = input;
        }

    }
    public void setNumbertext(String text){
        numberText = text;
        parent.saveToDB();
    }

    //Sound
    public static final int MEDIA = 1;
    public static final int ALARM = 2;
    String status;
    int soundActual;
    int soundtype;
    String [] statusOption = {DBActionSound.STATUS_SOUND,"Vibrate",DBActionSound.STATUS_MUTE};
    String [] soundOptions = {"Sound","Vibrate","Mute"};

    public Child(Group parent, int soundtype){
        this.type = SOUND;
        this.parent = parent;
        this.soundActual = 50;

        switch (soundtype){
            case MEDIA: name = "Media"; break;
            case ALARM: name = "Alarm"; break;
            default: break;
        }

        this.soundtype = soundtype;
    }




}
