package org.dhbw.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.dhbw.geo.database.DBCondition;
import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBRule;

import java.util.ArrayList;

/**
 * Created by Matthias on 17.06.2015.
 */
public class CheckConditionService extends IntentService {
    public static final String TAG = "checkConditionService";

    public CheckConditionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent fired!");
        try {
            // get the corresponding condition
            long conditionId = Long.valueOf(intent.getAction());
            DBConditionTime condition = DBConditionTime.selectFromDB(conditionId);
            Log.d(TAG, "Condition: " + condition.getName());
            // get the corresponding rules
            ArrayList<DBRule> rules = DBRule.selectFromDB(condition);
            Log.d(TAG, "Number of rules to be checked: " + rules.size());
            // loop through the rules and check whether conditions are met
            for(int i = 0; i < rules.size(); i++){
                if(rules.get(i).allConditionsMet()){ // if all conditions are met
                    rules.get(i).performAllActions();
                }
            }
        } catch (Exception e){
            Log.e(TAG, "Couldn't handle time condition: " + e.getMessage());
        }
    }
}
