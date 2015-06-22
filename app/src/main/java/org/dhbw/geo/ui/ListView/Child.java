package org.dhbw.geo.ui.ListView;

import android.media.AudioManager;
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

    String status;
    int soundActual;
    int soundtype;
    String [] statusOption = {DBActionSound.STATUS_SOUND,DBActionSound.STATUS_MUTE};
    String [] soundOptions = {"Sound","Mute"};
    boolean active;
    int soundMax = 100;

    public Child(Group parent, int soundtype, int actual, boolean active, String status){


        this.type = SOUND;
        this.parent = parent;
        this.soundActual = actual;

        switch (soundtype){
            case AudioManager.STREAM_MUSIC: name = "Music"; break;
            case AudioManager.STREAM_RING: name = "Ring"; break;
            case AudioManager.STREAM_ALARM: name = "Alarm"; break;
            default: break;
        }
        this.active = active;
        this.soundtype = soundtype;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
        parent.saveToDB(soundtype);
    }

    public void setActive(boolean active) {
        this.active = active;
        parent.saveToDB(soundtype);
    }

    public void setSoundActual(int soundActual) {
        this.soundActual = soundActual;
        parent.saveToDB(soundtype);
    }
}
