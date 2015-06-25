package org.dhbw.geo.backend;

import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;

/**
 * Created by Oliver on 18.06.2015.
 */
public class Route {

    /**
     * Start of the URL to the backend
     */
    private static final String baseURL = "http://mobileandroidbackend.herokuapp.com";

    /**
     * URL for the specific REST API call to make
     */
    public final String url;
    /**
     * HTTP Method
     */
    public final RequestMethod method;
    /**
     * Any JSON content to send. Used in POST and PUT methods.
     */
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
