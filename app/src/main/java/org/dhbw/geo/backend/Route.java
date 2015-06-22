package org.dhbw.geo.backend;

import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;

/**
 * Created by Oliver on 18.06.2015.
 */
public class Route {

    private static final String baseURL = "http://mobileandroidbackend.herokuapp.com";

    public final String url;
    public final RequestMethod method;
    public String jsonContent = null;

    public Route(String urlEnd, RequestMethod method) {
        this.url = baseURL + urlEnd;
        this.method = method;
    }

    public Route(String urlEnd, RequestMethod method, DBConditionFence fenceGroup) {
        this(urlEnd, method);
        this.jsonContent = JSONConverter.toJSONString(fenceGroup);
    }

    public Route(String urlEnd, RequestMethod method, DBFence fence) {
        this(urlEnd, method);
        this.jsonContent = JSONConverter.toJSONString(fence);
    }

}
