package org.dhbw.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.NotificationFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Matthias on 17.06.2015.
 * TODO: documentation!
 */
public class ConditionService extends IntentService {
    public static final String TAG = "checkConditionService";

    public ConditionService() {
        super(TAG);
    }

    private void handleCheckConditionTime(long id){
        // get the corresponding condition
        long conditionId = Long.valueOf(id);
        DBConditionTime condition = DBConditionTime.selectFromDB(conditionId);
        Log.d(TAG, "Condition: " + condition.getName());
        // reset the alarm
        condition.updateAlarm();
        // get the corresponding rules
        ArrayList<DBRule> rules = DBRule.selectFromDB(condition);
        Log.d(TAG, "Number of rules to be checked: " + rules.size());
        // loop through the rules and check whether conditions are met
        for(int i = 0; i < rules.size(); i++){
            if(rules.get(i).allConditionsMet()){ // if all conditions are met
                rules.get(i).startAllActions();
            }
        }
    }

    private void handleAutoStart(){
        NotificationFactory.createNotification(this, "Autostart", "Willkommen!", false);
        // execute rules for which the conditions are met
        ArrayList<DBRule> rules = DBRule.selectAllFromDB();
        for(int i = 0; i < rules.size(); i++){
            DBRule rule = rules.get(i);
            if(rule.allConditionsMet()){
                rule.startAllActions();
            }
        }
        // TODO: set geofences for all rules
        // TODO: set alarms for all rules
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent fired!");
        // register context
        ContextManager.setContext(this);
        if(intent.getAction().equals(AutoStart.AUTO_START)){
            handleAutoStart();
        } else {
            try {
                long id = Long.valueOf(intent.getAction());
                handleCheckConditionTime(id);
            } catch(Exception e){
                Log.e(TAG, "An error occurred for handleCheckConditionTime: " + e.getMessage());
            }
        }

    }
}
