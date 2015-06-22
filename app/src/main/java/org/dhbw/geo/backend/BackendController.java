package org.dhbw.geo.backend;

import android.app.DownloadManager;

import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.json.JSONObject;

/**
 * Created by Oliver on 18.06.2015.
 */
public class BackendController {

    private final BackendCallback callback;

    /**
     * Use as such:
     * <pre>
     * <code>BackendController controller = new BackendController(new BackendCallback() {
     *     public void actionPerformed(String result) {
     *         //do stuff on callback with result
     *         ArrayList<DBConditionFence> groups = JSONConverter.getFenceGroups(result);
     *     }
     * }
     * controller.getAllFenceGroups();
     * </code>
     * </pre>
     * @param callback
     */
    public BackendController(BackendCallback callback) {
        this.callback = callback;
    }

    public void getAllFenceGroups() {
        Route route = new Route("/fence_group/getAll", RequestMethod.GET);
        doCall(route);
    }
    public void createFenceGroup(DBConditionFence fenceGroup) {
        Route route = new Route("/fence_group", RequestMethod.POST, fenceGroup);
        doCall(route);
    }
    public void getFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.GET);
        doCall(route);
    }
    public void getFencesForGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/getFences", RequestMethod.GET);
        doCall(route);
    }
    public void updateFenceGroup(int fence_group_id, DBConditionFence fenceGroup) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.PUT, fenceGroup);
        doCall(route);
    }
    public void deleteFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.DELETE);
        doCall(route);
    }


    public void createFence(int fence_group_id, DBFence fence) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence", RequestMethod.POST, fence);
        doCall(route);
    }
    public void getFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.GET);
        doCall(route);
    }
    public void updateFence(int fence_group_id, int fence_id, DBFence fence) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.PUT, fence);
        doCall(route);
    }
    public void deleteFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.DELETE);
        doCall(route);
    }

    private void doCall(Route route) {
        APICaller caller = new APICaller(route, callback);
        caller.execute();
    }

}
