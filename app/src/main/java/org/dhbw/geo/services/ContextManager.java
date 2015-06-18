package org.dhbw.geo.services;

import android.content.Context;
import android.util.Log;

/**
 * This class stores the context to be used later on for Notifications etc.
 * @author Matthias
 */
public class ContextManager {
    /**
     * the object containting the context
     */
    private static Context context;

    /**
     * Gets the app context
     * @return the context
     */
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
