package org.dhbw.geo.backend;

/**
 * Created by Oliver on 18.06.2015.
 */
public class Route {

    private static final String baseURL = "http://mobileandroidbackend.herokuapp.com";

    public final String url;
    public final RequestMethod method;

    public Route(String urlEnd, RequestMethod method) {
        this.url = baseURL + urlEnd;
        this.method = method;
    }

}
