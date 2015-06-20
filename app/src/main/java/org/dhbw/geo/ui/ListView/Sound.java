package org.dhbw.geo.ui.ListView;

import android.media.AudioManager;
import android.util.Log;

import org.dhbw.geo.database.DBActionSound;
import org.dhbw.geo.database.DBRule;

/**
 * Created by Joern on 15.06.2015.
 */
public class Sound extends Group {
    Child soundAlarm;
    Child soundRing;
    Child soundMusic;

    DBActionSound actionAlarm;
    DBActionSound actionRing;
    DBActionSound actionMusic;



    public Sound(){
        super("Sound");
    }
    public void addChild(DBActionSound action){
        switch (action.getType()){
            case AudioManager.STREAM_ALARM:
                soundAlarm = new Child(this,action.getType(),action.getVolume(),action.isActive(),action.getStatus());
                actionAlarm = action;
                break;
            case AudioManager.STREAM_MUSIC:
                soundMusic = new Child(this,action.getType(),action.getVolume(),action.isActive(),action.getStatus());
                actionMusic = action;
                break;
            case AudioManager.STREAM_RING:
                soundRing = new Child(this,action.getType(),action.getVolume(),action.isActive(),action.getStatus());
                actionRing = action;
                break;
            default:
                break;
        }

    }

    public void addChildren (DBRule rule){
        //adds all soundtypes that werent loaded from db
        if (soundAlarm == null){
            soundAlarm = new Child(this,AudioManager.STREAM_ALARM,50,false,DBActionSound.STATUS_SOUND);
            actionAlarm = new DBActionSound();
            actionAlarm.setActive(false);
            actionAlarm.setRule(rule);
            actionAlarm.setType(AudioManager.STREAM_ALARM);
            actionAlarm.setVolume(50);
            actionAlarm.setStatus(DBActionSound.STATUS_SOUND);
    }
        if (soundMusic == null){
            soundMusic = new Child(this,AudioManager.STREAM_ALARM,50,false,DBActionSound.STATUS_SOUND);
            actionMusic = new DBActionSound();
            actionMusic.setActive(false);
            actionMusic.setRule(rule);
            actionMusic.setType(AudioManager.STREAM_MUSIC);
            actionMusic.setVolume(50);
            actionMusic.setStatus(DBActionSound.STATUS_SOUND);

        }
        if (soundRing == null){
            soundRing = new Child(this,AudioManager.STREAM_RING,50,false,DBActionSound.STATUS_SOUND);
            actionRing = new DBActionSound();
            actionRing.setActive(false);
            actionRing.setRule(rule);
            actionRing.setType(AudioManager.STREAM_RING);
            actionRing.setVolume(50);
            actionRing.setStatus(DBActionSound.STATUS_SOUND);
        }
        add(soundAlarm);
        add(soundMusic);
        add(soundRing);
    }

    @Override
    public void saveToDB(int soundtype) {
        DBActionSound action;
        Child child;
        switch (soundtype){
            case AudioManager.STREAM_ALARM:
                action = actionAlarm;
                child = soundAlarm;
                break;
            case AudioManager.STREAM_MUSIC:
                action = actionMusic;
                child = soundMusic;
                break;
            case AudioManager.STREAM_RING:
                action = actionRing;
                child = soundRing;
                break;
            default:
                return;
        }

        Log.e("SOUND", "write to db");
        Log.e("SOUND", ""+child.active);
        Log.e("SOUND", ""+child.status);
        Log.e("SOUND", ""+child.soundActual);

        action.setActive(child.active);
        action.setStatus(child.status);
        action.setVolume(child.soundActual);
        action.writeToDB();
    }


}
