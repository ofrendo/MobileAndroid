package org.dhbw.geo.ui.ListView;

import android.util.Log;

import org.dhbw.geo.database.DBActionMessage;
import org.dhbw.geo.database.DBRule;

/**
 * Created by Joern on 15.06.2015.
 */
public class Message extends Group {
    Child number;
    Child message;
    DBActionMessage action;

    public Message(DBRule rule){
        super("Message");
        action = new DBActionMessage();
        rule.addAction(action);
        action.setActive(active);
        number = new Child(this,"Number","",true);
        message = new Child(this, "Message","");
        //sets action
        action.setMessage(message.text);
        action.setNumber(number.numberText);
        addAll();
    }
    public Message(DBActionMessage action){
        super("Message");
        this.action = action;
        this.number = new Child(this,"Number", action.getNumber(), true);
        this.message = new Child(this, "Message",action.getMessage());
        active = action.isActive();
        addAll();
    }
    public void addAll(){
        add(number);
        add(message);
    }

    @Override
    public void saveToDB() {
        Log.e("MESSAGE","Write to DB");
        Log.e("MESSAGE", message.text);
        Log.e("MESSAGE",number.numberText);
        Log.e("MESSAGE",""+active);
        action.setMessage(message.text);
        action.setNumber(number.numberText);
        action.setActive(active);
        action.writeToDB();


    }
}
