package org.dhbw.geo.services;

import android.content.Context;
import android.util.Log;

/**
 * Created by Matthias on 17.06.2015.
 * TODO: documentation!
 */
public class ContextManager {
    private static Context context;

    public static Context getContext(){
        if(context == null){
            Log.e("ContextManager", "There is no registered Context!");
            return null;
        }
        return context;
    }

    public static void setContext(Context context){
        ContextManager.context = context;
    }
}
