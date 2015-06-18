package org.dhbw.geo.backend;

import android.app.DownloadManager;

/**
 * Created by Oliver on 18.06.2015.
 */
public class BackendController {

    public BackendController() {

    }

    public void getAllFenceGroups() {
        Route route = new Route("/fence_group/getAll", RequestMethod.GET);
        APICaller caller = new APICaller(route);
        caller.execute();
    }
    public void createFenceGroup() {
        Route route = new Route("/fence_group", RequestMethod.POST);
    }
    public void getFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.GET);
    }
    public void getFencesForGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/getFences", RequestMethod.GET);
    }
    public void updateFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.PUT);
    }
    public void deleteFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.DELETE);
    }


    public void createFence(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.POST);
    }
    public void getFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.GET);
    }
    public void updateFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.PUT);
    }
    public void deleteFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.DELETE);
    }

}
