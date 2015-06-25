package org.dhbw.geo.backend;

import org.dhbw.geo.database.DBObject;
import org.json.JSONObject;

/**
 * Created by Oliver on 21.06.2015.
 * Used as a mechanism for a callback function, after a REST API call has been made to the backend.
*/
public abstract class BackendCallback {

    public abstract void actionPerformed(String result);

}
