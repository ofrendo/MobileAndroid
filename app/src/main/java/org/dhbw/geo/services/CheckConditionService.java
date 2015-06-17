package org.dhbw.geo.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Matthias on 17.06.2015.
 */
public class CheckConditionService extends IntentService {

    public CheckConditionService() {
        super("CheckConditionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("CheckConditionService", "onHandleIntent fired!");
    }
}
