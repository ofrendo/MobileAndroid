package org.dhbw.geo.ui.ListView;

import android.util.Log;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBActionMessage;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.services.ContextManager;

/**
 * Group-Class for WLAN
 * @author Joern
 */
public class Message extends Group {
    Child number;
    Child message;
    DBActionMessage action;

    public Message(DBRule rule){
        super(ContextManager.getContext().getString(R.string.action_Message));
        action = new DBActionMessage();
        rule.addAction(action);
        action.setActive(active);
        number = new Child(this,ContextManager.getContext().getString(R.string.action_Number),"",true);
        message = new Child(this, ContextManager.getContext().getString(R.string.action_text),"");
        //sets action
        action.setMessage(message.text);
        action.setNumber(number.numberText);
        addAll();
    }
    public Message(DBActionMessage action){
        super(ContextManager.getContext().getString(R.string.action_Message));
        this.action = action;
        this.number = new Child(this,ContextManager.getContext().getString(R.string.action_Number), action.getNumber(), true);
        this.message = new Child(this, ContextManager.getContext().getString(R.string.action_text),action.getMessage());
        active = action.isActive();
        addAll();
    }
    public void addAll(){
        add(number);
        add(message);
    }

    @Override
    public void saveToDB() {

        action.setMessage(message.text);
        action.setNumber(number.numberText);
        action.setActive(active);
        action.writeToDB();


    }
}
