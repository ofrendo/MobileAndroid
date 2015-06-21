package org.dhbw.geo.backend;

import org.dhbw.geo.database.DBObject;
import org.json.JSONObject;

/**
 * Created by Oliver on 21.06.2015.
*/
public abstract class BackendCallback<T extends DBObject> {

    public abstract void actionPerformed(JSONObject jsonObject);

}
