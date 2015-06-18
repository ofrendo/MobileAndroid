package org.dhbw.geo.ui.ListView;

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

    public String getAction(){
        switch (type){
            case SLIDER:
                return name + " : "+value;
            case CHECKBOX:
                return name + " : "+checked;
            case SWITCH:
                return name + " : "+checked;
            case RADIOBUTTONS:
                return name + " : "+selectedOption;
            case TEXTINPUT:
                return name + " : "+text;
            case NUMBERINPUT:
                return name + " : "+numberText;
            default:
                return "";
        }
    }

    String text;
    public Child(String name, String inputText){
        type = TEXTINPUT;
        this.text = inputText;
        this.name = name;

    }

    String numberText;
    public Child(String name, String input, boolean number){
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
